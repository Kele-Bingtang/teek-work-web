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
import top.teek.uac.system.model.dto.link.RoleLinkInfoDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserGroupsDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkRolesDTO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkUserVO;
import top.teek.uac.system.service.link.UserGroupRoleLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:43:56
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userGroupRoleLink")
public class UserGroupRoleLinkController {
    
    private final UserGroupRoleLinkService userGroupRoleLinkService;

    @GetMapping("/listUserGroupByRoleId/{roleId}")
    @Operation(summary = "用户组列表查询", description = "查询某个角色绑定的用户组列表")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<TablePage<UserGroupLinkUserVO>> listUserGroupByRoleId(@PathVariable String roleId, UserGroupLinkInfoDTO userGroupLinkInfoDTO, PageQuery pageQuery) {
        TablePage<UserGroupLinkUserVO> tablePage = userGroupRoleLinkService.listUserGroupByRoleId(roleId, userGroupLinkInfoDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("listRoleLinkByGroupId/{userGroupId}")
    @Operation(summary = "角色列表查询", description = "通过用户组 ID 查询角色列表")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<TablePage<RoleLinkVO>> listRoleLinkByGroupId(@PathVariable String userGroupId, RoleLinkInfoDTO roleLinkInfoDTO, PageQuery pageQuery) {
        TablePage<RoleLinkVO> tablePage = userGroupRoleLinkService.listRoleLinkByGroupId(userGroupId, roleLinkInfoDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("listWithDisabledByRoleId/{roleId}")
    @Operation(summary = "用户组列表查询", description = "查询所有用户组列表，如果用户组绑定角色，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupBindSelectVO>> listWithDisabledByRoleId(@PathVariable String roleId) {
        List<UserGroupBindSelectVO> sysUserGroupVOList = userGroupRoleLinkService.listWithDisabledByRoleId(roleId);
        return HttpResult.ok(sysUserGroupVOList);
    }

    @GetMapping("/listWithDisabledByGroupId/{userGroupId}")
    @Operation(summary = "角色列表查询", description = "查询所有角色列表（查询所有角色列表，如果角色绑定了用户组，则 disabled 属性为 true）")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<List<RoleBindSelectVO>> listWithDisabledByGroupId(@PathVariable String userGroupId) {
        List<RoleBindSelectVO> roleBindSelectVOList = userGroupRoleLinkService.listWithDisabledByGroupId(userGroupId);
        return HttpResult.ok(roleBindSelectVOList);
    }
    
    @PostMapping("/addRolesToUserGroup")
    @Operation(summary = "添加角色到用户组", description = "添加角色到用户组（多个角色）")
    @OperateLog(title = "用户组角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:userGroup:add')")
    public Response<Boolean> addRolesToUserGroup(@Validated(RestGroup.AddGroup.class) @RequestBody UserGroupLinkRolesDTO userGroupLinkRolesDTO) {
        if (userGroupRoleLinkService.checkRolesExistUserGroup(userGroupLinkRolesDTO)) {
            return HttpResult.failMessage("添加角色到用户组失败，用户组已存在于角色中");
        }
        boolean result = userGroupRoleLinkService.addRolesToUserGroup(userGroupLinkRolesDTO);
        return HttpResult.ok(result);
    }

    @PostMapping("/addUserGroupsToRole")
    @Operation(summary = "添加角色到用户组", description = "添加角色到用户组（多个用户组）")
    @OperateLog(title = "用户组角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addUserGroupsToRole(@Validated(RestGroup.AddGroup.class) @RequestBody RoleLinkUserGroupsDTO roleLinkUserGroupsDTO) {
        if (userGroupRoleLinkService.checkRoleExistUserGroups(roleLinkUserGroupsDTO)) {
            return HttpResult.failMessage("添加角色到用户组失败，用户组已存在于角色中");
        }
        boolean result = userGroupRoleLinkService.addUserGroupsToRole(roleLinkUserGroupsDTO);
        return HttpResult.ok(result);
    }

    @DeleteMapping("/removeUserGroupFromRole/{ids}")
    @Operation(summary = "移出角色", description = "将用户组移出角色")
    @OperateLog(title = "用户组角色关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeUserGroupFromRole(@PathVariable Long[] ids) {
        boolean result = userGroupRoleLinkService.removeUserGroupFromRole(List.of(ids));
        return HttpResult.ok(result);
    }
}
