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
import top.teek.uac.system.model.dto.link.UserGroupLinkUsersDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkUserGroupsDTO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkUserVO;
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

    @GetMapping("listUserLinkByGroupId/{userGroupId}")
    @Operation(summary = "用户列表查询", description = "通过用户组 ID 查询用户列表（分页）")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<TablePage<UserLinkVO>> listUserLinkByGroupId(@PathVariable String userGroupId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        TablePage<UserLinkVO> tablePage = userGroupUserLinkService.listUserLinkByGroupId(userGroupId, userLinkInfoDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("/listUserGroupByUserId/{userId}")
    @Operation(summary = "用户组列表查询", description = "查询某个用户所在的用户组列表")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupLinkUserVO>> listUserGroupByUserId(@PathVariable String userId, SysUserGroupDTO sysUserGroupDTO) {
        List<UserGroupLinkUserVO> tablePage = userGroupUserLinkService.listUserGroupByUserId(userId, sysUserGroupDTO);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("/listWithSelectedByGroupId/{userGroupId}")
    @Operation(summary = "用户列表查询", description = "下拉查询用户列表，如果用户绑定了用户组，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Response<List<UserBindSelectVO>> listWithSelectedByGroupId(@PathVariable String userGroupId) {
        List<UserBindSelectVO> userBindSelectVOList = userGroupUserLinkService.listWithSelectedByGroupId(userGroupId);
        return HttpResult.ok(userBindSelectVOList);
    }

    @GetMapping("listWithSelectedByUserId/{userId}")
    @Operation(summary = "用户组列表查询", description = "查询所有用户组列表，如果用户组存在用户，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupBindSelectVO>> listWithSelectedByUserId(@PathVariable String userId) {
        List<UserGroupBindSelectVO> sysUserGroupVOList = userGroupUserLinkService.listWithSelectedByUserId(userId);
        return HttpResult.ok(sysUserGroupVOList);
    }

    @PostMapping("/addUserGroupsToUser")
    @Operation(summary = "添加用户组到用户", description = "添加用户组到用户（多个用户组）")
    @OperateLog(title = "用户用户组管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    public Response<Boolean> addUserGroupsToUser(@Validated(RestGroup.AddGroup.class) @RequestBody UserLinkUserGroupsDTO userLinkUserGroupsDTO) {
        if (userGroupUserLinkService.checkUserExistUserGroups(userLinkUserGroupsDTO)) {
            return HttpResult.failMessage("添加用户到用户组失败，用户已存在于用户组中");
        }
        boolean result = userGroupUserLinkService.addUserGroupsToUser(userLinkUserGroupsDTO);
        return HttpResult.ok(result);
    }

    @PostMapping("/addUsersToGroup")
    @Operation(summary = "添加用户到用户组", description = "添加用户到用户组（多个用户）")
    @OperateLog(title = "用户用户组关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:userGroup:add')")
    public Response<Boolean> addUsersToUserGroup(@Validated(RestGroup.AddGroup.class) @RequestBody UserGroupLinkUsersDTO userGroupLinkUsersDTO) {
        if (userGroupUserLinkService.checkUsersExistUserGroup(userGroupLinkUsersDTO)) {
            return HttpResult.failMessage("添加用户到用户组失败，用户已存在于用户组中");
        }
        boolean result = userGroupUserLinkService.addUsersToUserGroup(userGroupLinkUsersDTO);
        return HttpResult.ok(result);
    }

    @DeleteMapping("/removeUserFromUserGroup/{ids}")
    @Operation(summary = "移出用户组", description = "将用户移出项目组")
    @OperateLog(title = "用户用户组关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:userGroup:remove')")
    public Response<Boolean> removeUserFromUserGroup(@PathVariable Long[] ids) {
        boolean result = userGroupUserLinkService.removeUserFromUserGroup(List.of(ids));
        return HttpResult.ok(result);
    }

    @PutMapping("/editUserGroupUserLink")
    @Operation(summary = "用户关联用户信息修改", description = "修改用户组和用户䣌关联信息")
    @OperateLog(title = "用户用户组关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:userGroup:edit')")
    public Response<Boolean> editUserGroupUserLink(@Validated(RestGroup.EditGroup.class) @RequestBody UserGroupUserLinkDTO userGroupUserLinkDTO) {
        return HttpResult.ok(userGroupUserLinkService.updateOne(userGroupUserLinkDTO));
    }
}
