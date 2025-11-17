package top.teek.uac.system.controller.link;

import cn.hutool.core.lang.tree.Tree;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.uac.core.log.annotation.OperateLog;
import top.teek.uac.core.log.enums.BusinessType;
import top.teek.uac.system.model.dto.link.RoleLinkDeptsDTO;
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

    @GetMapping("/listDeptListByRoleId/{appId}/{roleId}")
    @Operation(summary = "部门列表查询", description = "通过角色 ID 查询部门列表（树形结构）")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<Tree<String>>> listDeptListByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<Tree<String>> sysMenuVOList = roleDeptLinkService.listDeptListByRoleId(roleId, appId);
        return HttpResult.ok(sysMenuVOList);
    }

    @GetMapping("/listDeptIdsByRoleId/{appId}/{roleId}")
    @Operation(summary = "部门 ID 列表查询", description = "通过角色 ID 查询部门 ID 列表")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Response<List<String>> listDeptIdsByRoleId(@PathVariable String appId, @PathVariable String roleId) {
        List<String> deptIds = roleDeptLinkService.listDeptIdsByRoleId(roleId, appId);
        return HttpResult.ok(deptIds);
    }

    @PostMapping("/addDeptsToRole")
    @Operation(summary = "添加部门到角色", description = "添加部门到角色")
    @OperateLog(title = "角色部门关联管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    public Response<Boolean> addDeptsToRole(@RequestBody RoleLinkDeptsDTO roleLinkDeptsDTO) {
        return HttpResult.ok(roleDeptLinkService.addDeptsToRole(roleLinkDeptsDTO, true));
    }
}
