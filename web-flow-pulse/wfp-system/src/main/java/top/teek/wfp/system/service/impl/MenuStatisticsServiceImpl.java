package top.teek.wfp.system.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.teek.redis.utils.RedisUtil;
import top.teek.utils.DateTimeUtil;
import top.teek.utils.JacksonUtil;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.wfp.system.mapper.MenuStatisticsMapper;
import top.teek.wfp.system.model.bo.MenuBO;
import top.teek.wfp.system.model.dto.MenuStatisticsDTO;
import top.teek.wfp.system.model.po.MenuStatistics;
import top.teek.wfp.system.model.vo.MenuVO;
import top.teek.wfp.system.service.MenuStatisticsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MenuStatisticsServiceImpl extends ServiceImpl<MenuStatisticsMapper, MenuStatistics> implements MenuStatisticsService {
    private final RestTemplateHelper restTemplateHelper;
    
    @Value("${uac.menuListUrl}")
    private String menuListUrl;

    @Override
    public List<MenuVO> query(String appCode, String systemName, String userId, String uacAppCode) {

        // 查询 UAC 的 Menu 
        String params = "?PrivilegeType=Menu&AppName={appName}&SecretKey={secretKey}:0.0.0.0:{secretKey}&UpdateColList=ALL";
        String menuListStr = restTemplateHelper.get(menuListUrl + params, String.class, Objects.isNull(uacAppCode) ? systemName : uacAppCode, userId, userId);
        List<MenuBO> menuList = JacksonUtil.toBeanList(menuListStr, MenuBO.class);
        if (CollectionUtils.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        String key = getScoreKey(appCode, systemName, userId);
        Set<Object> range = RedisUtil.zGetReverse(key, 0, -1);
        if (CollectionUtils.isEmpty(range)) {
            return MapstructUtil.convert(menuList, MenuVO.class);
        }
        List<Object> menuCodeList = new ArrayList<>(range);
        // 按照 menuCodeList 的顺序排序，如果不存在 menuCodeList 的内容，则保 menuList 的持默认排序 
        menuList.sort((m1, m2) ->{
            for (Object menuCode : menuCodeList) {
                if (m1.getMenuCode().equals(menuCode) && !m2.getMenuCode().equals(menuCode)) {
                    return -1;
                } else if (!m1.getMenuCode().equals(menuCode) && m2.getMenuCode().equals(menuCode)) {
                    return 1;
                }
            }
            return 0;
        });
        return MapstructUtil.convert(menuList, MenuVO.class);
    }

    /**
     * 新增一笔记录到 Redis
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addOne(MenuStatisticsDTO menuStatisticsDTO) {
        String appCode = menuStatisticsDTO.getAppCode();
        String menuCode = menuStatisticsDTO.getMenuCode();

        // Redis 点击量排序的 Key 
        String scoreKey = getScoreKey(appCode, menuStatisticsDTO.getSystemName(), menuStatisticsDTO.getUserId());
        // Key 过期时间 1 天
        RedisUtil.expire(scoreKey, 1, TimeUnit.DAYS);
        Double score = RedisUtil.zScore(scoreKey, menuCode);

        // 如果没有过数据，则置为 1，有则在原有基础上自增 1 
        if (Objects.isNull(score)) {
            RedisUtil.zAddIfAbsent(scoreKey, menuCode, 1);
        } else {
            RedisUtil.zIncrementScore(scoreKey, menuCode, 1);
        }
        MenuStatistics menuStatistics = MapStructUtil.convert(menuStatisticsDTO, MenuStatistics.class);
        menuStatistics.setTriggerTime(DateTime.now());
        String listKey = getListKey(appCode, menuStatisticsDTO.getSystemName(), menuStatisticsDTO.getUserId());

        // 两天缓存时间 
        RedisUtil.expire(listKey, 2, TimeUnit.DAYS);
        return RedisUtil.lPush(listKey, menuStatistics);
    }

    /**
     * 将 Redis 的数据同步到 Mysql
     */
    @Async
    @Override
    public void syncToMysqlFromRedis() {
        log.info("---------------- 开始同步 ----------------");
        Set<String> keys = RedisUtil.keys(CacheConstant.ALL + CacheConstant.MENU);
        if (CollectionUtils.isEmpty(keys)) {
            log.info("---------------- 同步结束，暂无数据同步 ----------------");
            return;
        }
        for (String key : keys) {
            List<Object> menuList = RedisUtil.lGet(key, 0, -1);
            if (CollectionUtils.isEmpty(menuList)) {
                log.info("Redis 没有 Route 同步到 MySQL");
                return;
            }
            List<MenuStatistics> menuStatisticsList = ListUtil.convert(menuList, MenuStatistics.class);

            // TODO 可能存在的问题：如果 saveBatch 成功，但是此时程序 down 掉，则 Redis 不会删除缓存，下次重复 saveBatch 
            boolean result = saveBatch(menuStatisticsList);
            if (result) {
                RedisUtil.del(key);
                log.info("同步成功，共同步了 {} 条数据", menuStatisticsList.size());
            } else {
                // 重置为两天缓存时间 
                RedisUtil.expire(key, 2, TimeUnit.DAYS);
                log.error("同步失败");
            }
        }

        // 更新 score 
        updateScore();
        log.info("---------------- 同步结束，共同步了 {} 个用户的数据 ----------------", keys.size());
    }

    /**
     * 缓存一周的点击量，形成周报
     */
    public Boolean cacheUserMenuScoreWeek() {
        String now = DateTimeUtil.now();
        String minus = DateTimeUtil.minus(1, ChronoUnit.WEEKS);
        return cacheUserMenuScore(minus, now, CacheConstant.WEEK, true);
    }

    /**
     * 更新点击量排序到 Redis
     */
    private void updateScore() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        String twoMonthBefore = LocalDateTime.now().minusMonths(2).format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        cacheUserMenuScore(twoMonthBefore, now, CacheConstant.NORMAL, false);
    }

    private Boolean cacheUserMenuScore(String starTime, String endTime, String cacheKey, boolean cacheBean) {
        List<MenuStatistics> menuStatisticsList = baseMapper.selectList(Wrappers.<MenuStatistics>lambdaQuery().between(MenuStatistics::getTriggerTime, starTime, endTime));
        Map<String, List<MenuStatistics>> menuGroup = menuStatisticsList.stream().collect(Collectors.groupingBy(this::buildGroupKey));

        // 更新前，将所有 Key 清空 
        RedisUtil.del(CacheConstant.ALL + CacheConstant.MENU_SCORE + CacheConstant.SEPARATOR_BAR + cacheKey);
        menuGroup.forEach((key, value) -> {

            // key 为 buildGroupKey() 方法构建，0 userid，1 appCode 2，systemName，3 menuCode 
            String[] keys = key.split(CacheConstant.SEPARATOR_COLO);
            if (keys.length == buildGroupKey(value.get(0)).length()) {
                String k = keys[0] + CacheConstant.SEPARATOR_COLO + keys[1] + CacheConstant.MENU_SCORE + CacheConstant.SEPARATOR_BAR + cacheKey;
                MenuStatistics menuStatistics = ListUtil.find(value, menu -> menu.getMenuCode().equals(keys[1]));
                if (cacheBean && Objects.nonNull(menuStatistics)) {
                    RedisUtil.zAdd(k, menuStatistics, value.size());
                } else if (!cacheBean) {
                    RedisUtil.zAdd(k, keys[2] + CacheConstant.SEPARATOR_VERTICAL + keys[3], value.size());
                }
            }
        });
        log.info("更新 {} 笔点击量统计数据到 Redis，By {}", menuGroup.size(), cacheKey);
        return true;
    }

    /**
     * 获取缓存菜单点击量的 Key
     */
    private String getScoreKey(String appCode, String systemName, String userId) {
        return userId + CacheConstant.SEPARATOR_COLO + appCode + CacheConstant.SEPARATOR_COLO + systemName + CacheConstant.MENU_SCORE + CacheConstant.SEPARATOR_BAR + CacheConstant.NORMAL;
    }

    /**
     * 获取缓存菜单信息的 Key
     */
    private String getListKey(String appCode, String systemName, String userId) {
        return userId + CacheConstant.SEPARATOR_COLO + appCode + CacheConstant.SEPARATOR_COLO + systemName + CacheConstant.MENU;
    }

    /**
     * 自定义分组的 Key 规则
     */
    private String buildGroupKey(MenuStatistics menuStatistics) {
        // key 规则如：k100338:My:Passdown 
        return menuStatistics.getUserId() + CacheConstant.SEPARATOR_COLO + menuStatistics.getAppCode() + CacheConstant.SEPARATOR_COLO + menuStatistics.getSystemName() + CacheConstant.SEPARATOR_COLO + menuStatistics.getMenuCode();
    }
}