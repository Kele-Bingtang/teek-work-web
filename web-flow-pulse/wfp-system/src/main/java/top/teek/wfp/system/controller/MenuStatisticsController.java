package top.teek.wfp.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.core.validate.RestGroup;
import top.teek.wfp.system.model.dto.MenuStatisticsDTO;
import top.teek.wfp.system.model.vo.MenuVO;
import top.teek.wfp.system.service.MenuStatisticsService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/menuStatistics")
@Tag(name = "菜单点击量统计接口", description = "MenuStatisticsController")
public class MenuStatisticsController {
    private final MenuStatisticsService menuStatisticsService;

    @GetMapping("/{userId}/{appCode}/{systemName}")
    @Operation(description = "按照点击量查询菜单")
    public Response<List<MenuVO>> query(@PathVariable String userId, @PathVariable String appCode, @PathVariable String systemName, @RequestParam(required = false) String uacAppCode) {
        List<MenuVO> query = menuStatisticsService.query(appCode, systemName, userId, uacAppCode);
        return HttpResult.ok(query);
    }

    @PostMapping
    @Operation(description = "添加一笔记录")
    public Response<Boolean> addOne(@Validated(RestGroup.AddGroup.class) @RequestBody MenuStatisticsDTO menuStatisticsDTO) {
        Boolean add = menuStatisticsService.addOne(menuStatisticsDTO);
        return HttpResult.ok(add);
    }

    @GetMapping("/syncData")
    @Operation(description = "从 Redis 同步数据到 MySQL")
    public Response<String> syncData() {
        menuStatisticsService.syncToMysqlFromRedis();
        return HttpResult.ok("异步同步 ...");
    }
}