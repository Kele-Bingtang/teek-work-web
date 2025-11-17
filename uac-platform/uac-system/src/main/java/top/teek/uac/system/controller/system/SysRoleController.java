package top.teek.uac.system.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.core.validate.RestGroup;
import top.teek.excel.helper.ExcelHelper;
import top.teek.idempotent.annotation.PreventRepeatSubmit;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.core.log.annotation.OperateLog;
import top.teek.uac.core.log.enums.BusinessType;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.vo.SysRoleVO;
import top.teek.uac.system.service.system.SysRoleService;

import java.util.List;

/**
 * @author Teeker
 * @date 2023/12/4 17:39
 * @note
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @Operation(summary = "角色列表查询", description = "通过条件查询角色列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Response<List<SysRoleVO>> list(SysRoleDTO sysRoleDTO) {
        List<SysRoleVO> sysRoleVOList = sysRoleService.listAll(sysRoleDTO);
        return HttpResult.ok(sysRoleVOList);
    }

    @GetMapping("/listPage")
    @Operation(summary = "角色列表查询", description = "通过条件查询角色列表（支持分页）")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Response<TablePage<SysRoleVO>> listPage(SysRoleDTO sysRoleDTO, PageQuery pageQuery) {
        TablePage<SysRoleVO> tablePage = sysRoleService.listPage(sysRoleDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @PostMapping
    @Operation(summary = "角色列表新增", description = "新增角色")
    @OperateLog(title = "角色管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    @PreventRepeatSubmit
    public Response<Boolean> addRole(@Validated(RestGroup.AddGroup.class) @RequestBody SysRoleDTO sysRoleDTO) {
        if (sysRoleService.checkRoleCodeUnique(sysRoleDTO)) {
            return HttpResult.failMessage("新增角色「" + sysRoleDTO.getRoleName() + "」失败，角色编码「" + sysRoleDTO.getRoleCode() + "」已存在");
        }
        if (sysRoleService.checkRoleNameUnique(sysRoleDTO)) {
            return HttpResult.failMessage("新增角色「" + sysRoleDTO.getRoleName() + "」失败，角色名称已存在");
        }

        return HttpResult.ok(sysRoleService.addRole(sysRoleDTO));
    }

    @PutMapping
    @Operation(summary = "角色列表修改", description = "修改角色")
    @OperateLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PreventRepeatSubmit
    public Response<Boolean> editRole(@Validated(RestGroup.EditGroup.class) @RequestBody SysRoleDTO sysRoleDTO) {
        if (sysRoleService.checkRoleCodeUnique(sysRoleDTO)) {
            return HttpResult.failMessage("修改角色「" + sysRoleDTO.getRoleName() + "」失败，角色编码「" + sysRoleDTO.getRoleCode() + "」已存在");
        }
        if (sysRoleService.checkRoleNameUnique(sysRoleDTO)) {
            return HttpResult.failMessage("修改角色「" + sysRoleDTO.getRoleName() + "」失败，角色名称已存在");
        }

        return HttpResult.ok(sysRoleService.editRole(sysRoleDTO));
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "角色列表删除", description = "通过主键批量删除角色列表")
    @OperateLog(title = "角色管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    @PreventRepeatSubmit
    public Response<Boolean> removeBatch(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids, @RequestBody List<String> roleIds) {
        return HttpResult.ok(sysRoleService.removeBatch(List.of(ids), roleIds));
    }

    @PostMapping("/export")
    @Operation(summary = "角色数据导出", description = "导出角色数据")
    @OperateLog(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:role:export')")
    public void export(@RequestBody SysRoleDTO sysRoleDTO, HttpServletResponse response) {
        List<SysRoleVO> sysRoleVOList = sysRoleService.listAll(sysRoleDTO);
        ExcelHelper.exportExcel(sysRoleVOList, "角色数据", SysRoleVO.class, response);
    }
}
