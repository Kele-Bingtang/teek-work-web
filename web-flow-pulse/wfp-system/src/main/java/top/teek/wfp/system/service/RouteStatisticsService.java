package top.teek.wfp.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.wfp.system.model.dto.RouteStatisticsDTO;
import top.teek.wfp.system.model.po.RouteStatistics;

/**
 * @author Teeker
 * @date 2026/1/17 00:13:44
 * @since 1.0.0
 */
public interface RouteStatisticsService extends IService<RouteStatistics> {
    Boolean addOne(RouteStatisticsDTO routeStatisticsDTO);

    void syncToMysqlFromRedis();
}
