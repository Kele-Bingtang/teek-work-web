package top.teek.uac.system.controller.link;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.core.validate.RestGroup;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.core.log.annotation.OperateLog;
import top.teek.uac.core.log.enums.BusinessType;
import top.teek.uac.system.model.dto.RoleDeptLinkDTO;
import top.teek.uac.system.model.dto.link.RoleLinkDeptsDTO;
import top.teek.uac.system.model.dto.link.RoleLinkInfoDTO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.service.link.RoleDeptLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:20:22
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/roleDeptLink")
public class RoleDeptLinkController {

    private final RoleDeptLinkService roleDeptLinkService;

    @GetMapping("/listDeptListByRoleId/{appId}/{roleId}")
    @Operation(summary = "部门列表查询", description = "通过角色 ID 查询部门列表（树形结构）")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<Tree<String>>> listDeptListByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<Tree<String>> sysMenuVOList = roleDeptLinkService.listDeptListByRoleId(roleId, appId);
        return HttpResult.ok(sysMenuVOList);
    }

    @GetMapping("/listDeptIdsByRoleId/{appId}/{roleId}")
    @Operation(summary = "部门 ID 列表查询", description = "通过角色 ID 查询部门 ID 列表")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<String>> listDeptIdsByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<String> deptIds = roleDeptLinkService.listDeptIdsByRoleId(roleId, appId);
        return HttpResult.ok(deptIds);
    }

    @GetMapping("/listRoleLinkByDeptId/{appId}/{deptId}")
    @Operation(summary = "角色列表查询", description = "通过部门 ID 查询角色列表")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<TablePage<RoleLinkVO>> listRoleLinkByDeptId(@PathVariable String appId, @PathVariable String deptId, RoleLinkInfoDTO roleLinkInfoDTO, PageQuery pageQuery) {
        TablePage<RoleLinkVO> tablePageList = roleDeptLinkService.listRoleLinkByDeptId(deptId, appId, roleLinkInfoDTO, pageQuery);
        return HttpResult.ok(tablePageList);
    }

    @GetMapping("listWithSelectedByDeptId/{deptId}")
    @Operation(summary = "部门列表查询", description = "查询所有部门列表，如果部门绑定角色，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<RoleBindSelectVO>> listWithSelectedByDeptId(@PathVariable String deptId) {
        List<RoleBindSelectVO> roleBindSelectVOList = roleDeptLinkService.listWithSelectedByDeptId(deptId);
        return HttpResult.ok(roleBindSelectVOList);
    }

    @PostMapping("/addDeptsToRole")
    @Operation(summary = "添加部门到角色", description = "添加部门到角色")
    @OperateLog(title = "部门角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addDeptsToRole(@RequestBody RoleLinkDeptsDTO roleLinkDeptsDTO) {
        return HttpResult.ok(roleDeptLinkService.addDeptsToRole(roleLinkDeptsDTO, true));
    }

    @PutMapping("/editRoleDeptLink")
    @Operation(summary = "部门关联角色信息修改", description = "修改部门和角色关联信息")
    @OperateLog(title = "部门角色关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:linkDept')")
    public Response<Boolean> editRoleDeptLink(@Validated(RestGroup.EditGroup.class) @RequestBody RoleDeptLinkDTO roleDeptLinkDTO) {
        return HttpResult.ok(roleDeptLinkService.updateOne(roleDeptLinkDTO));
    }

    @DeleteMapping("/removeDeptFromRole/{ids}")
    @Operation(summary = "移出角色", description = "将部门移出角色")
    @OperateLog(title = "部门角色关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeDeptFromRole(@PathVariable Long[] ids) {
        boolean result = roleDeptLinkService.removeDeptFromRole(List.of(ids));
        return HttpResult.ok(result);
    }
}
