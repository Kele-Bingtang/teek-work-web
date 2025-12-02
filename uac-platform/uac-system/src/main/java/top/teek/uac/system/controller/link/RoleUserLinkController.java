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
import top.teek.uac.system.model.dto.RoleUserLinkDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserListDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkRoleListDTO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.RoleUserLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:49:02
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/roleUserLink")
public class RoleUserLinkController {

    private final RoleUserLinkService roleUserLinkService;

    // ------- 用户关联角色相关 API（以用户为主）-------

    @GetMapping("/listRoleLinkByUserId/{appId}/{userId}")
    @Operation(summary = "角色列表查询", description = "通过用户 ID 查询角色列表")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<List<RoleLinkVO>> listRoleListByUserId(@PathVariable String appId, @PathVariable String userId, SysRoleDTO sysRoleDTO) {
        List<RoleLinkVO> roleLinkVOS = roleUserLinkService.listRoleLinkByUserId(appId, userId, sysRoleDTO);
        return HttpResult.ok(roleLinkVOS);
    }

    @GetMapping("listWithSelectedByUserId/{appId}/{userId}")
    @Operation(summary = "角色列表查询", description = "查询所有角色列表，如果角色绑定了用户，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Response<List<RoleBindSelectVO>> listWithSelectedByUserId(@PathVariable String appId, @PathVariable String userId) {
        List<RoleBindSelectVO> roleBindSelectVOList = roleUserLinkService.listWithSelectedByUserId(appId, userId);
        return HttpResult.ok(roleBindSelectVOList);
    }

    @PostMapping("/addRoleListToUser")
    @Operation(summary = "添加角色到用户", description = "添加角色到用户（多个角色）")
    @OperateLog(title = "用户角色管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    public Response<Boolean> addRoleListToUser(@Validated(RestGroup.AddGroup.class) @RequestBody UserLinkRoleListDTO userLinkRoleListDTO) {
        if (roleUserLinkService.checkRoleListExistUser(userLinkRoleListDTO)) {
            return HttpResult.failMessage("添加用户到角色失败，用户已存在于角色中");
        }
        boolean result = roleUserLinkService.addRoleListToUser(userLinkRoleListDTO);
        return HttpResult.ok(result);
    }

    // ------- 角色关联用户相关 API（以角色为主）-------

    @GetMapping("listUserLinkByRoleId/{roleId}")
    @Operation(summary = "用户列表查询", description = "通过角色 ID 查询用户列表（分页）")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<TablePage<UserLinkVO>> listUserLinkByRoleId(@PathVariable String roleId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        TablePage<UserLinkVO> userLinkVOList = roleUserLinkService.listUserLinkByRoleId(roleId, userLinkInfoDTO, pageQuery);
        return HttpResult.ok(userLinkVOList);
    }

    @GetMapping("/listWithSelectedByRoleId/{roleId}")
    @Operation(summary = "用户列表查询", description = "下拉查询用户列表，如果用户绑定了角色，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<List<UserBindSelectVO>> listWithSelectedByRoleId(@PathVariable String roleId) {
        List<UserBindSelectVO> userBindSelectVOList = roleUserLinkService.listWithSelectedByRoleId(roleId);
        return HttpResult.ok(userBindSelectVOList);
    }

    @PostMapping("/addUserListToRole")
    @Operation(summary = "添加用户到角色", description = "添加用户到角色（多个用户）")
    @OperateLog(title = "用户角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addUserListToRole(@Validated(RestGroup.AddGroup.class) @RequestBody RoleLinkUserListDTO roleLinkUserListDTO) {
        if (roleUserLinkService.checkUserListExistRole(roleLinkUserListDTO)) {
            return HttpResult.failMessage("添加用户到角色失败，用户已存在于角色中");
        }
        boolean result = roleUserLinkService.addUserListToRole(roleLinkUserListDTO);
        return HttpResult.ok(result);
    }
    
    // ------- 公共 API -------

    @PutMapping("/editRoleUserLink")
    @Operation(summary = "角色用户关联信息修改", description = "修改角色用户关联信息")
    @OperateLog(title = "角色用户关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Response<Boolean> editRoleUserLink(@Validated(RestGroup.EditGroup.class) @RequestBody RoleUserLinkDTO roleUserLinkDTO) {
        return HttpResult.ok(roleUserLinkService.editRoleUserLink(roleUserLinkDTO));
    }

    @DeleteMapping("/removeRoleUserLink/{ids}")
    @Operation(summary = "角色用户解除关联关系", description = "解除角色用户关联关系")
    @OperateLog(title = "角色用户关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeRoleUserLink(@PathVariable Long[] ids) {
        boolean result = roleUserLinkService.removeRoleUserLink(List.of(ids));
        return HttpResult.ok(result);
    }
}
