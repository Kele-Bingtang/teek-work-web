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
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.UserRoleLinkDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUsersDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkRolesDTO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.UserRoleLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:49:02
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userRoleLink")
public class UserRoleLinkController {

    private final UserRoleLinkService userRoleLinkService;

    @GetMapping("/listRoleLinkByUserId/{appId}/{userId}")
    @Operation(summary = "角色列表查询", description = "查询某个用户所在的角色列表")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<List<RoleLinkVO>> listRoleListByUserId(@PathVariable String appId, @PathVariable String userId, SysRoleDTO sysRoleDTO) {
        List<RoleLinkVO> roleLinkVOS = userRoleLinkService.listRoleLinkByUserId(appId, userId, sysRoleDTO);
        return HttpResult.ok(roleLinkVOS);
    }

    @GetMapping("listWithSelectedByUserId/{appId}/{userId}")
    @Operation(summary = "角色列表查询", description = "查询所有角色列表，如果角色绑定了用户，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<List<RoleBindSelectVO>> listWithSelectedByUserId(@PathVariable String appId, @PathVariable String userId) {
        List<RoleBindSelectVO> roleBindSelectVOList = userRoleLinkService.listWithSelectedByUserId(appId, userId);
        return HttpResult.ok(roleBindSelectVOList);
    }

    @GetMapping("listUserLinkByRoleId/{roleId}")
    @Operation(summary = "用户列表查询", description = "通过角色 ID 查询用户列表（分页）")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<TablePage<UserLinkVO>> listUserLinkByRoleId(@PathVariable String roleId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        TablePage<UserLinkVO> userLinkVOList = userRoleLinkService.listUserLinkByRoleId(roleId, userLinkInfoDTO, pageQuery);
        return HttpResult.ok(userLinkVOList);
    }

    @GetMapping("/listWithSelectedByRoleId/{roleId}")
    @Operation(summary = "用户列表查询", description = "下拉查询用户列表，如果用户绑定了角色，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<List<UserBindSelectVO>> listWithSelectedByRoleId(@PathVariable String roleId) {
        List<UserBindSelectVO> userBindSelectVOList = userRoleLinkService.listWithSelectedByRoleId(roleId);
        return HttpResult.ok(userBindSelectVOList);
    }

    @PostMapping("/addRolesToUser")
    @Operation(summary = "添加角色到用户", description = "添加角色到用户（多个角色）")
    @OperateLog(title = "用户角色管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    public Response<Boolean> addRolesToUser(@Validated(RestGroup.AddGroup.class) @RequestBody UserLinkRolesDTO userLinkRolesDTO) {
        if (userRoleLinkService.checkUserExistRoles(userLinkRolesDTO)) {
            return HttpResult.failMessage("添加用户到角色失败，用户已存在于角色中");
        }
        boolean result = userRoleLinkService.addRolesToUser(userLinkRolesDTO);
        return HttpResult.ok(result);
    }

    @PostMapping("/addUsersToRole")
    @Operation(summary = "添加用户到角色", description = "添加用户到角色（多个用户）")
    @OperateLog(title = "用户角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addUsersToRole(@Validated(RestGroup.AddGroup.class) @RequestBody RoleLinkUsersDTO roleLinkUsersDTO) {
        if (userRoleLinkService.checkRoleExistUser(roleLinkUsersDTO)) {
            return HttpResult.failMessage("添加用户到角色失败，用户已存在于角色中");
        }
        boolean result = userRoleLinkService.addUsersToRole(roleLinkUsersDTO);
        return HttpResult.ok(result);
    }

    @DeleteMapping("/removeUsersFromRole/{ids}")
    @Operation(summary = "移出角色", description = "将用户移出角色")
    @OperateLog(title = "用户组角色关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeUserFromRole(@PathVariable Long[] ids) {
        boolean result = userRoleLinkService.removeUserFromRole(List.of(ids));
        return HttpResult.ok(result);
    }

    @PutMapping("/editUserRoleLink")
    @Operation(summary = "用户关联角色信息修改", description = "修改用户组和角色关联信息")
    @OperateLog(title = "用户角色关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Response<Boolean> editUserRoleLink(@Validated(RestGroup.EditGroup.class) @RequestBody UserRoleLinkDTO userRoleLinkDTO) {
        return HttpResult.ok(userRoleLinkService.updateOne(userRoleLinkDTO));
    }
}
