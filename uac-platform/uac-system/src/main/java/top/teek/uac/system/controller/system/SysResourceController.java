package top.teek.uac.system.controller.system;

import cn.hutool.core.lang.tree.Tree;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.core.validate.RestGroup;
import top.teek.excel.helper.ExcelHelper;
import top.teek.idempotent.annotation.PreventRepeatSubmit;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.core.log.annotation.OperateLog;
import top.teek.uac.core.log.enums.BusinessType;
import top.teek.uac.system.model.dto.SysResourceDTO;
import top.teek.uac.system.model.vo.SysResourceVO;
import top.teek.uac.system.model.vo.router.RouterVO;
import top.teek.uac.system.service.link.RoleResourceLinkService;
import top.teek.uac.system.service.system.SysResourceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Teeker
 * @date 2023/12/4 16:41
 * @note
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/resource")
public class SysResourceController {
    
    private final SysResourceService sysResourceService;
    private final RoleResourceLinkService roleResourceLinkService;

    @GetMapping("/listRoutes/{appId}")
    @Operation(summary = "路由列表查询", description = "查询前端需要的路由列表")
    public Response<List<RouterVO>> listRoutes(@PathVariable String appId) {
        List<RouterVO> routerVOList = sysResourceService.listRoutes(appId);
        return HttpResult.ok(routerVOList);
    }
     

    @GetMapping("/list")
    @Operation(summary = "资源列表查询", description = "通过查询条件查询资源列表")
    @PreAuthorize("hasAuthority('system:resource:list')")
    public Response<List<SysResourceVO>> list(SysResourceDTO sysResourceDTO) {
        List<SysResourceVO> sysResourceVOList = sysResourceService.listAll(sysResourceDTO);
        return HttpResult.ok(sysResourceVOList);
    }

    @GetMapping("/listPage")
    @Operation(summary = "资源列表查询", description = "通过查询条件查询资源列表（支持分页）")
    @PreAuthorize("hasAuthority('system:resource:list')")
    public Response<TablePage<SysResourceVO>> listPage(SysResourceDTO sysResourceDTO, PageQuery pageQuery) {
        TablePage<SysResourceVO> tablePage = sysResourceService.listPage(sysResourceDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("/treeSelect")
    @Operation(summary = "资源下拉值查询", description = "通过查询条件查询资源下拉值（下拉框查询使用）")
    @PreAuthorize("hasAuthority('system:resource:query')")
    public Response<List<Tree<String>>> listResourceTreeSelect(@Validated(RestGroup.QueryGroup.class) SysResourceDTO sysResourceDTO) {
        List<Tree<String>> treeList = sysResourceService.listResourceTreeSelect(sysResourceDTO);
        return HttpResult.ok(treeList);
    }

    @GetMapping("/treeTable")
    @Operation(summary = "资源树表查询", description = "通过查询条件查询资源树表")
    @PreAuthorize("hasAuthority('system:resource:list')")
    public Response<List<SysResourceVO>> listResourceTreeTable(@Validated(RestGroup.QueryGroup.class) SysResourceDTO sysResourceDTO) {
        List<SysResourceVO> treeTable = sysResourceService.listResourceTreeTable(sysResourceDTO);
        return HttpResult.ok(treeTable);
    }

    @PostMapping
    @Operation(summary = "资源新增", description = "新增资源")
    @OperateLog(title = "资源管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:resource:add')")
    @PreventRepeatSubmit
    public Response<Boolean> addResource(@Validated(RestGroup.AddGroup.class) @RequestBody SysResourceDTO sysResourceDTO) {
        if (sysResourceService.checkResourceCodeUnique(sysResourceDTO)) {
            return HttpResult.failMessage("新增资源「" + sysResourceDTO.getResourceName() + "」失败，资源名称「" + sysResourceDTO.getResourceCode() + "」已存在");
        }

        if (sysResourceService.checkResourceNameUnique(sysResourceDTO)) {
            return HttpResult.failMessage("新增资源「" + sysResourceDTO.getResourceName() + "」失败，资源名称已存在");
        }

        return HttpResult.ok(sysResourceService.addResource(sysResourceDTO));
    }

    @PutMapping
    @Operation(summary = "资源修改", description = "修改资源")
    @OperateLog(title = "资源管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:resource:edit')")
    @PreventRepeatSubmit
    public Response<Boolean> editResource(@Validated(RestGroup.EditGroup.class) @RequestBody SysResourceDTO sysResourceDTO) {

        if (sysResourceDTO.getParentId().equals(sysResourceDTO.getResourceId())) {
            return HttpResult.failMessage("修改资源「" + sysResourceDTO.getResourceName() + "」失败，上级资源不能是自己");
        }

        if (sysResourceService.checkResourceCodeUnique(sysResourceDTO)) {
            return HttpResult.failMessage("修改资源「" + sysResourceDTO.getResourceName() + "」失败，资源名称「" + sysResourceDTO.getResourceCode() + "」已存在");
        }
        
        if (sysResourceService.checkResourceNameUnique(sysResourceDTO)) {
            return HttpResult.failMessage("修改资源「" + sysResourceDTO.getResourceName() + "」失败，资源名称已存在");
        }

        return HttpResult.ok(sysResourceService.editResource(sysResourceDTO));
    }

    @DeleteMapping("/{id}/{resourceId}")
    @Operation(summary = "资源删除", description = "通过主键删除资源")
    @OperateLog(title = "资源管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:resource:remove')")
    @PreventRepeatSubmit
    public Response<Boolean> removeResource(@PathVariable Long id, @PathVariable String resourceId) {
        if (sysResourceService.hasChild(resourceId)) {
            return HttpResult.failMessage("存在下级资源，不允许删除");
        }

        if (sysResourceService.checkResourceExistRole(resourceId)) {
            return HttpResult.failMessage("资源存在角色，不允许删除");
        }

        return HttpResult.ok(sysResourceService.removeResource(id));
    }

    @PostMapping("/export")
    @Operation(summary = "资源数据导出", description = "导出资源数据")
    @OperateLog(title = "资源管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:resource:export')")
    public void export(@RequestBody SysResourceDTO sysResourceDTO, HttpServletResponse response) {
        List<SysResourceVO> sysResourceVOList = sysResourceService.listAll(sysResourceDTO);
        ExcelHelper.exportExcel(sysResourceVOList, "资源数据", SysResourceVO.class, response);
    }
    
}
