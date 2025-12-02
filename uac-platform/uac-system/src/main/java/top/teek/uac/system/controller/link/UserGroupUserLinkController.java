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
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.dto.UserGroupUserLinkDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkUserListDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkUserGroupListDTO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.UserGroupUserLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:40:39
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userGroupUserLink")
public class UserGroupUserLinkController {

    private final UserGroupUserLinkService userGroupUserLinkService;

    // ------- 用户组关联用户相关 API（以用户组为主）-------

    @GetMapping("listUserLinkByGroupId/{userGroupId}")
    @Operation(summary = "用户列表查询", description = "通过用户组 ID 查询用户列表（分页）")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<TablePage<UserLinkVO>> listUserLinkByGroupId(@PathVariable String userGroupId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        TablePage<UserLinkVO> tablePage = userGroupUserLinkService.listUserLinkByGroupId(userGroupId, userLinkInfoDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("/listWithSelectedByGroupId/{userGroupId}")
    @Operation(summary = "用户列表查询", description = "下拉查询用户列表，如果用户绑定了用户组，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<List<UserBindSelectVO>> listWithSelectedByGroupId(@PathVariable String userGroupId) {
        List<UserBindSelectVO> userBindSelectVOList = userGroupUserLinkService.listWithSelectedByGroupId(userGroupId);
        return HttpResult.ok(userBindSelectVOList);
    }

    @PostMapping("/addUserListToGroup")
    @Operation(summary = "添加用户到用户组", description = "添加用户到用户组（多个用户）")
    @OperateLog(title = "用户用户组关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:userGroup:add')")
    public Response<Boolean> addUserListToGroup(@Validated(RestGroup.AddGroup.class) @RequestBody UserGroupLinkUserListDTO userGroupLinkUserListDTO) {
        if (userGroupUserLinkService.checkUserListExistUserGroup(userGroupLinkUserListDTO)) {
            return HttpResult.failMessage("添加用户到用户组失败，用户已存在于用户组中");
        }
        boolean result = userGroupUserLinkService.addUserListToGroup(userGroupLinkUserListDTO);
        return HttpResult.ok(result);
    }

    // ------- 用户关联用户组相关 API（以用户为主）-------

    @GetMapping("/listUserGroupByUserId/{userId}")
    @Operation(summary = "用户组列表查询", description = "查询某个用户所在的用户组列表")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupLinkVO>> listUserGroupByUserId(@PathVariable String userId, SysUserGroupDTO sysUserGroupDTO) {
        List<UserGroupLinkVO> tablePage = userGroupUserLinkService.listUserGroupByUserId(userId, sysUserGroupDTO);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("listWithSelectedByUserId/{userId}")
    @Operation(summary = "用户组列表查询", description = "查询所有用户组列表，如果用户组存在用户，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupBindSelectVO>> listWithSelectedByUserId(@PathVariable String userId) {
        List<UserGroupBindSelectVO> sysUserGroupVOList = userGroupUserLinkService.listWithSelectedByUserId(userId);
        return HttpResult.ok(sysUserGroupVOList);
    }

    @PostMapping("/addUserGroupListToUser")
    @Operation(summary = "添加用户组到用户", description = "添加用户组到用户（多个用户组）")
    @OperateLog(title = "用户用户组管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    public Response<Boolean> addUserGroupListToUser(@Validated(RestGroup.AddGroup.class) @RequestBody UserLinkUserGroupListDTO userLinkUserGroupListDTO) {
        if (userGroupUserLinkService.checkUserGroupListExistUser(userLinkUserGroupListDTO)) {
            return HttpResult.failMessage("添加用户到用户组失败，用户已存在于用户组中");
        }
        boolean result = userGroupUserLinkService.addUserGroupListToUser(userLinkUserGroupListDTO);
        return HttpResult.ok(result);
    }

    // ------- 公共 API -------
    
    @PutMapping("/editUserGroupUserLink")
    @Operation(summary = "用户组用户关联信息修改", description = "修改用户组用户关联信息")
    @OperateLog(title = "用户组用户关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:userGroup:edit')")
    public Response<Boolean> editUserGroupUserLink(@Validated(RestGroup.EditGroup.class) @RequestBody UserGroupUserLinkDTO userGroupUserLinkDTO) {
        return HttpResult.ok(userGroupUserLinkService.editUserGroupUserLink(userGroupUserLinkDTO));
    }

    @DeleteMapping("/removeUserGroupUserLink/{ids}")
    @Operation(summary = "用户组用户解除关联关系", description = "解除用户组用户关联关系")
    @OperateLog(title = "用户组用户关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:userGroup:remove')")
    public Response<Boolean> removeUserGroupUserLink(@PathVariable Long[] ids) {
        boolean result = userGroupUserLinkService.removeUserGroupUserLink(List.of(ids));
        return HttpResult.ok(result);
    }

}
