package top.teek.wfp.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.teek.core.http.Assert;
import top.teek.redis.utils.RedisUtil;
import top.teek.utils.ListUtil;
import top.teek.web.utils.WebUtil;
import top.teek.wfp.system.mapper.RouteStatisticsMapper;
import top.teek.wfp.system.model.dto.RouteStatisticsDTO;
import top.teek.wfp.system.model.po.RouteStatistics;
import top.teek.wfp.system.service.RouteStatisticsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Service
@Slf4j

public class RouteStatisticsServiceImpl extends ServiceImpl<RouteStatisticsMapper, RouteStatistics> implements RouteStatisticsService {
    
    @Override
    public Boolean addOne(RouteStatisticsDTO routeStatisticsDTO) {
        
        RouteStatistics routeStatistics = MapStructUtil.convert(routeStatisticsDTO, RouteStatistics.class);
        HttpServletRequest request = WebUtil.getRequest();
        Assert.nonNull(request, "request 对象无法获取");
        String ip = WebsiteUtils.getIPAddress(request);
        String os = WebsiteUtils.getOs(request);
        String browser = WebsiteUtils.getBrowser(request);
        routeStatistics.setIp(ip);
        routeStatistics.setOs(os);
        routeStatistics.setBrowser(browser);
        routeStatistics.setVisitTime(LocalDateTime.now());

        // userId:route： RouteStatisticsDTO1、RouteStatisticsDTO2 ...... 
        String listKey = getListKey(routeStatisticsDTO.getAppCode(), routeStatisticsDTO.getSystemName(), routeStatisticsDTO.getUserId());
        // 两天缓存时间 
        RedisUtil.expire(listKey, 2, TimeUnit.DAYS);
        return RedisUtil.lPush(listKey, routeStatistics);
    }

    // @Scheduled(cron = "* * 8,20 * * ? *") 
    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncToMysqlFromRedis() {
        log.info("---------------- 开始同步 ----------------");
        Set<String> keys = RedisUtil.keys(CacheConstant.ALL + CacheConstant.ROUTE);
        if (CollectionUtils.isEmpty(keys)) {
            log.info("---------------- 同步结束，暂无数据同步 ----------------");
            return;
        }
        for (String key : keys) {
            List<Object> routeList = RedisUtil.lGet(key, 0, -1);
            if (CollectionUtils.isEmpty(routeList)) {
                log.info("Redis 没有 Route 同步到 MySQL");
                return;
            }
            List<RouteStatistics> statisticsList = ListUtil.convert(routeList, RouteStatistics.class);

            // TODO 可能存在的问题：如果 saveBatch 成功，但是此时程序 down 掉，则 Redis 不会删除缓存，下次重复 saveBatch 
            boolean result = saveBatch(statisticsList);
            if (result) {
                RedisUtil.del(key);
                log.info("同步成功，共同步了 {} 条数据", statisticsList.size());
            } else {

                // 重置为两天缓存时间 
                RedisUtil.expire(key, 2, TimeUnit.DAYS);
                log.error("同步失败");
            }
        }
        log.info("---------------- 同步结束，共同步了 {} 个用户的数据 ----------------", keys.size());
    }

    private String getListKey(String appCode, String systemName, String userId) {
        return userId + CacheConstant.SEPARATOR_COLO + appCode + CacheConstant.SEPARATOR_COLO + systemName + CacheConstant.ROUTE;
    }
}