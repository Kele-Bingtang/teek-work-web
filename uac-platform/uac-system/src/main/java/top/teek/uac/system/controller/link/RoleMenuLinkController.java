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
import top.teek.uac.system.model.dto.link.RoleLinkMenuDTO;
import top.teek.uac.system.service.link.RoleMenuLinkService;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 22:20:22
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/roleMenuLink")
public class RoleMenuLinkController {

    private final RoleMenuLinkService roleMenuLinkService;

    @GetMapping("/listMenuListByRoleId/{appId}/{roleId}")
    @Operation(summary = "菜单列表查询", description = "通过角色 ID 查询菜单列表（树型结构）")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Response<List<Tree<String>>> listMenuListByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<Tree<String>> sysMenuVOList = roleMenuLinkService.listMenuListByRoleId(roleId, appId);
        return HttpResult.ok(sysMenuVOList);
    }

    @GetMapping("/listMenuIdsByRoleId/{appId}/{roleId}")
    @Operation(summary = "菜单 ID 列表查询", description = "通过角色 ID 查询菜单 ID 列表")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Response<List<String>> listMenuIdsByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<String> menuIds = roleMenuLinkService.listMenuIdsByRoleId(roleId, appId, null);
        return HttpResult.ok(menuIds);
    }

    @PostMapping("/addMenusToRole")
    @Operation(summary = "添加菜单到角色", description = "添加菜单到角色")
    @OperateLog(title = "角色菜单关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addMenusToRole(@RequestBody RoleLinkMenuDTO roleLinkMenuDTO) {
        return HttpResult.ok(roleMenuLinkService.addMenusToRole(roleLinkMenuDTO, true));
    }
}
