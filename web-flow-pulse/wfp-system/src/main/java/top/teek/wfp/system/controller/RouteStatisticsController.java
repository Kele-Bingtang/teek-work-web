package top.teek.wfp.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.core.validate.RestGroup;
import top.teek.wfp.system.model.dto.RouteStatisticsDTO;
import top.teek.wfp.system.service.RouteStatisticsService;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/routeStatistics")
@Tag(name = "路由访问量统计接口", description = "RouteStatisticsController")
public class RouteStatisticsController {
    private final RouteStatisticsService routeStatisticsService;

    @PostMapping
    @Operation(description = "添加一笔记录")
    public Response<Boolean> add(@Validated(RestGroup.AddGroup.class) @RequestBody RouteStatisticsDTO routeStatisticsDTO) {
        Boolean isResult = routeStatisticsService.addOne(routeStatisticsDTO);
        return HttpResult.ok(isResult);
    }

    @GetMapping("/syncData")
    @Operation(description = "从 Redis 同步数据到 MySQL")
    public Response<String> syncData() {
        routeStatisticsService.syncToMysqlFromRedis();
        return HttpResult.ok("异步同步 ...");
    }
}