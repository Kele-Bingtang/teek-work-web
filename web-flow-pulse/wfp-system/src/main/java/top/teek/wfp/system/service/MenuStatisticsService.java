package top.teek.wfp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.wfp.system.model.dto.MenuStatisticsDTO;
import top.teek.wfp.system.model.po.MenuStatistics;
import top.teek.wfp.system.model.vo.MenuVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2026/1/17 00:18:08
 * @since 1.0.0
 */
public interface MenuStatisticsService extends IService<MenuStatistics> {
    List<MenuVO> query(String appCode, String systemName, String userId, String uacAppCode);

    Boolean addOne(MenuStatisticsDTO menuStatisticsDTO);

    void syncToMysqlFromRedis();
    
}
