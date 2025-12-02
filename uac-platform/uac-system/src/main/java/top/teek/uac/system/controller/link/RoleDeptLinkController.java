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
import top.teek.uac.system.model.dto.link.DeptLinkRoleListDTO;
import top.teek.uac.system.model.dto.link.RoleLinkDeptListDTO;
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
    
    // ------- 部门关联角色相关 API（以部门为主）-------

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

    @PostMapping("/addRoleListToDept")
    @Operation(summary = "添加角色到部门", description = "添加角色到部门")
    @OperateLog(title = "部门角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addRoleListToDept(@RequestBody DeptLinkRoleListDTO deptLinkRoleListDTO) {
        if (roleDeptLinkService.checkRoleListExistDept(deptLinkRoleListDTO)) {
            return HttpResult.failMessage("添加角色到部门失败，角色已存在于部门中");
        }
        
        return HttpResult.ok(roleDeptLinkService.addRoleListToDept(deptLinkRoleListDTO));
    }

    // ------- 角色关联部门相关 API（以角色为主） -------

    @GetMapping("/listDeptListByRoleId/{appId}/{roleId}")
    @Operation(summary = "部门列表查询", description = "通过角色 ID 查询部门列表（树形结构）")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<Tree<String>>> listDeptListByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<Tree<String>> roleDeptLinkList = roleDeptLinkService.listDeptListByRoleId(roleId, appId);
        return HttpResult.ok(roleDeptLinkList);
    }

    @GetMapping("/listDeptIdsByRoleId/{appId}/{roleId}")
    @Operation(summary = "部门 ID 列表查询", description = "通过角色 ID 查询部门 ID 列表")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<String>> listDeptIdsByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<String> deptIds = roleDeptLinkService.listDeptIdsByRoleId(roleId, appId);
        return HttpResult.ok(deptIds);
    }
    
    @PostMapping("/addDeptListToRole")
    @Operation(summary = "添加部门到角色", description = "添加部门到角色")
    @OperateLog(title = "部门角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addDeptListToRole(@RequestBody RoleLinkDeptListDTO roleLinkDeptListDTO) {
        return HttpResult.ok(roleDeptLinkService.addDeptListToRole(roleLinkDeptListDTO, true));
    }
    
    // ------- 公共 API -------

    @PutMapping("/editRoleDeptLink")
    @Operation(summary = "角色部门关联信息修改", description = "修改角色部门关联信息")
    @OperateLog(title = "角色部门关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:linkDept')")
    public Response<Boolean> editRoleDeptLink(@Validated(RestGroup.EditGroup.class) @RequestBody RoleDeptLinkDTO roleDeptLinkDTO) {
        return HttpResult.ok(roleDeptLinkService.editRoleDeptLink(roleDeptLinkDTO));
    }

    @DeleteMapping("/removeRoleDeptLink/{ids}")
    @Operation(summary = "角色部门解除关联关系", description = "解除角色部门关联关系")
    @OperateLog(title = "角色部门关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeRoleDeptLink(@PathVariable Long[] ids) {
        boolean result = roleDeptLinkService.removeRoleDeptLink(List.of(ids));
        return HttpResult.ok(result);
    }
}
