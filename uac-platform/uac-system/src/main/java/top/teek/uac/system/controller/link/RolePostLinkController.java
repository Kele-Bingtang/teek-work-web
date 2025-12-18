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
import top.teek.uac.system.model.dto.RolePostLinkDTO;
import top.teek.uac.system.model.dto.SysPostDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.link.PostLinkRoleListDTO;
import top.teek.uac.system.model.dto.link.RoleLinkPostListDTO;
import top.teek.uac.system.model.vo.link.PostLinkVO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.service.link.RolePostLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/12/18 21:13:52
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/rolePostLink")
public class RolePostLinkController {

    private final RolePostLinkService rolePostLinkService;

    // ------- 岗位关联角色相关 API（以岗位为主）-------

    @GetMapping("/listRoleLinkByPostId/{appId}/{postId}")
    @Operation(summary = "角色列表查询", description = "通过岗位 ID 查询角色列表")
    @PreAuthorize("hasAuthority('system:post:query')")
    public Response<TablePage<RoleLinkVO>> listRoleLinkByPostId(@PathVariable String appId, @PathVariable String postId, SysRoleDTO sysRoleDTO, PageQuery pageQuery) {
        TablePage<RoleLinkVO> tablePageList = rolePostLinkService.listRoleLinkByPostId(appId, postId, sysRoleDTO, pageQuery);
        return HttpResult.ok(tablePageList);
    }

    @GetMapping("listWithSelectedByPostId/{appId}/{postId}")
    @Operation(summary = "岗位列表查询", description = "查询所有岗位列表，如果岗位绑定角色，则 disabled 属性为 true")
    @PreAuthorize("hasAuthority('system:post:query')")
    public Response<List<RoleBindSelectVO>> listWithSelectedByPostId(@PathVariable String appId, @PathVariable String postId) {
        List<RoleBindSelectVO> roleBindSelectVOList = rolePostLinkService.listWithSelectedByPostId(appId, postId);
        return HttpResult.ok(roleBindSelectVOList);
    }

    @PostMapping("/addRoleListToPost")
    @Operation(summary = "添加角色到岗位", description = "添加角色到岗位")
    @OperateLog(title = "岗位角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addRoleListToPost(@Validated(RestGroup.AddGroup.class) @RequestBody PostLinkRoleListDTO postLinkRoleListDTO) {
        if (rolePostLinkService.checkRoleListExistPost(postLinkRoleListDTO)) {
            return HttpResult.failMessage("添加角色到岗位失败，角色已存在于岗位中");
        }

        return HttpResult.ok(rolePostLinkService.addRoleListToPost(postLinkRoleListDTO));
    }

    // ------- 角色关联岗位相关 API（以角色为主） -------

    @GetMapping("/listPostListByRoleId/{appId}/{roleId}")
    @Operation(summary = "岗位列表查询", description = "通过角色 ID 查询岗位列表（树形结构）")
    @PreAuthorize("hasAuthority('system:post:query')")
    public Response<TablePage<PostLinkVO>> listPostListByRoleId(@PathVariable String appId, @PathVariable String roleId, SysPostDTO sysPostDTO, PageQuery pageQuery) {
        TablePage<PostLinkVO> rolePostLinkList = rolePostLinkService.listPostListByRoleId(appId, roleId, sysPostDTO, pageQuery);
        return HttpResult.ok(rolePostLinkList);
    }

    @PostMapping("/addPostListToRole")
    @Operation(summary = "添加岗位到角色", description = "添加岗位到角色")
    @OperateLog(title = "岗位角色关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addPostListToRole(@Validated(RestGroup.AddGroup.class) @RequestBody RoleLinkPostListDTO roleLinkPostListDTO) {
        return HttpResult.ok(rolePostLinkService.addPostListToRole(roleLinkPostListDTO, true));
    }

    // ------- 公共 API -------

    @PutMapping("/editRolePostLink")
    @Operation(summary = "角色岗位关联信息修改", description = "修改角色岗位关联信息")
    @OperateLog(title = "角色岗位关联管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:linkPost')")
    public Response<Boolean> editRolePostLink(@Validated(RestGroup.EditGroup.class) @RequestBody RolePostLinkDTO rolePostLinkDTO) {
        return HttpResult.ok(rolePostLinkService.editRolePostLink(rolePostLinkDTO));
    }

    @DeleteMapping("/removeRolePostLink/{ids}")
    @Operation(summary = "角色岗位解除关联关系", description = "解除角色岗位关联关系")
    @OperateLog(title = "角色岗位关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeRolePostLink(@PathVariable Long[] ids) {
        boolean result = rolePostLinkService.removeRolePostLink(List.of(ids));
        return HttpResult.ok(result);
    }
}
