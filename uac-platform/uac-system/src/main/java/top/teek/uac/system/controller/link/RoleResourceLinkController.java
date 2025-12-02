package top.teek.uac.system.controller.link;

/**
 * @author Teeker
 * @date 2025/11/17 22:32:51
 * @since 1.0.0
 */

import cn.hutool.core.lang.tree.Tree;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.uac.core.log.annotation.OperateLog;
import top.teek.uac.core.log.enums.BusinessType;
import top.teek.uac.system.model.dto.link.RoleLinkResourceDTO;
import top.teek.uac.system.service.link.RoleResourceLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:20:22
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/roleResourceLink")
public class RoleResourceLinkController {

    private final RoleResourceLinkService roleResourceLinkService;

    // ------- 资源关联角色相关 API（以资源为主）-------

    // ------- 角色关联资源相关 API（以角色为主）-------

    @GetMapping("/listResourceListByRoleId/{appId}/{roleId}")
    @Operation(summary = "资源列表查询", description = "通过角色 ID 查询资源列表（树型结构）")
    @PreAuthorize("hasAuthority('system:resource:query')")
    public Response<List<Tree<String>>> listResourceListByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<Tree<String>> roleResourceLinkList = roleResourceLinkService.listResourceListByRoleId(roleId, appId);
        return HttpResult.ok(roleResourceLinkList);
    }

    @GetMapping("/listResourceIdsByRoleId/{appId}/{roleId}")
    @Operation(summary = "资源 ID 列表查询", description = "通过角色 ID 查询资源 ID 列表")
    @PreAuthorize("hasAuthority('system:resource:query')")
    public Response<List<String>> listResourceIdsByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<String> resourceIds = roleResourceLinkService.listResourceIdsByRoleId(roleId, appId, null);
        return HttpResult.ok(resourceIds);
    }

    @PostMapping("/addResourceListToRole")
    @Operation(summary = "添加资源到角色", description = "添加资源到角色")
    @OperateLog(title = "角色资源关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addResourceListToRole(@RequestBody RoleLinkResourceDTO roleLinkResourceDTO) {
        return HttpResult.ok(roleResourceLinkService.addResourceListToRole(roleLinkResourceDTO, true));
    }

    // ------- 公共 API -------

    @DeleteMapping("/removeRoleResourceLink/{ids}")
    @Operation(summary = "角色资源解除关联关系", description = "解除角色资源关联关系")
    @OperateLog(title = "角色资源关联管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Response<Boolean> removeRoleResourceLink(@PathVariable Long[] ids) {
        boolean result = roleResourceLinkService.removeRoleResourceLink(List.of(ids));
        return HttpResult.ok(result);
    }
}
