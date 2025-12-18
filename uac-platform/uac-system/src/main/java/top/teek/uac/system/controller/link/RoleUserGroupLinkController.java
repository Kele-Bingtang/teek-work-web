package top.teek.uac.system.controller.link;

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
import top.teek.uac.system.model.dto.RoleUserGroupLinkDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserGroupListDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkRoleListDTO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;
import top.teek.uac.system.service.link.RoleUserGroupLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:43:56
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/roleUserGroupLink")
public class RoleUserGroupLinkController {

    private final RoleUserGroupLinkService roleUserGroupLinkService;

    // ------- 用户组关联角色相关 API（以用户组为主）-------

    @GetMapping("listRoleLinkByGroupId/{appId}/{userGroupId}")
    @Operation(summary = "角色列表查询", description = "通过用户组 ID 查询角色列表")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<TablePage<RoleLinkVO>> listRoleLinkByGroupId(@PathVariable String appId, @PathVariable String userGroupId, SysRoleDTO sysRoleDTO, PageQuery pageQuery) {
        TablePage<RoleLinkVO> tablePage = roleUserGroupLinkService.listRoleLinkByGroupId(appId, userGroupId, sysRoleDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("/listWithSelectedByGroupId/{appId}/{userGroupId}")
    @Operation(summary = "角色列表查询", description = "查询所有角色列表（查询所有角色列表，如果角色绑定了用户组，则 disabled 属性为 true）")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<List<RoleBindSelectVO>> listWithSelectedByGroupId(@PathVariable String appId, @PathVariable String userGroupId) {
        List<RoleBindSelectVO> roleBindSelectVOList = roleUserGroupLinkService.listWithSelectedByGroupId(appId, userGroupId);
        return HttpResult.ok(roleBindSelectVOList);
    }

    @PostMapping("/addRoleListToUserGroup")
    @Operation(summary = "添加角色到用户组", description = "添加角色到用户组（多个角色）")
    @OperateLog(title = "用户组角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:userGroup:add')")
    public Response<Boolean> addRoleListToUserGroup(@Validated(RestGroup.AddGroup.class) @RequestBody UserGroupLinkRoleListDTO userGroupLinkRoleListDTO) {
        if (roleUserGroupLinkService.checkRoleListExistUserGroup(userGroupLinkRoleListDTO)) {
            return HttpResult.failMessage("添加角色到用户组失败，用户组已存在于角色中");
        }
        boolean result = roleUserGroupLinkService.addRoleListToUserGroup(userGroupLinkRoleListDTO);
        return HttpResult.ok(result);
    }

    // ------- 角色关联用户组相关 API（以角色为主）-------

    @GetMapping("/listUserGroupByRoleId/{appId}/{roleId}")
    @Operation(summary = "用户组列表查询", description = "查询某个角色绑定的用户组列表")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<TablePage<UserGroupLinkVO>> listUserGroupByRoleId(@PathVariable String appId, @PathVariable String roleId, SysUserGroupDTO sysUserGroupDTO, PageQuery pageQuery) {
        TablePage<UserGroupLinkVO> tablePage = roleUserGroupLinkService.listUserGroupByRoleId(appId, roleId, sysUserGroupDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("listWithSelectedByRoleId/{appId}/{roleId}")
    @Operation(summary = "用户组列表查询", description = "查询所有用户组列表，如果用户组绑定角色，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupBindSelectVO>> listWithSelectedByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<UserGroupBindSelectVO> sysUserGroupVOList = roleUserGroupLinkService.listWithSelectedByRoleId(appId, roleId);
        return HttpResult.ok(sysUserGroupVOList);
    }

    @PostMapping("/addUserGroupListToRole")
    @Operation(summary = "添加角色到用户组", description = "添加角色到用户组（多个用户组）")
    @OperateLog(title = "用户组角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addUserGroupListToRole(@Validated(RestGroup.AddGroup.class) @RequestBody RoleLinkUserGroupListDTO roleLinkUserGroupListDTO) {
        if (roleUserGroupLinkService.checkUserGroupListExistRole(roleLinkUserGroupListDTO)) {
            return HttpResult.failMessage("添加角色到用户组失败，用户组已存在于角色中");
        }
        boolean result = roleUserGroupLinkService.addUserGroupListToRole(roleLinkUserGroupListDTO);
        return HttpResult.ok(result);
    }

    // ------- 公共 API -------
    
    @PutMapping("/editRoleUserGroupLink")
    @Operation(summary = "角色用户组关联信息修改", description = "修改角色用户组关联信息")
    @OperateLog(title = "角色用户组关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:linkUserGroup')")
    public Response<Boolean> editRoleUserGroupLink(@Validated(RestGroup.EditGroup.class) @RequestBody RoleUserGroupLinkDTO roleUserGroupLinkDTO) {
        return HttpResult.ok(roleUserGroupLinkService.editRoleUserGroupLink(roleUserGroupLinkDTO));
    }

    @DeleteMapping("/removeRoleUserGroupLink/{ids}")
    @Operation(summary = "角色用户组解除关联关系", description = "解除角色用户组关联关系")
    @OperateLog(title = "角色用户组关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeRoleUserGroupLink(@PathVariable Long[] ids) {
        boolean result = roleUserGroupLinkService.removeRoleUserGroupLink(List.of(ids));
        return HttpResult.ok(result);
    }
}
