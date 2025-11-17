package top.teek.uac.system.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
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
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.vo.SysUserGroupVO;
import top.teek.uac.system.model.vo.extra.UserGroupTreeVO;
import top.teek.uac.system.service.system.SysUserGroupService;

import java.util.List;

/**
 * @author Teeker
 * @date 2024/3/12 23:59
 * @note
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userGroup")
public class SysUserGroupController {

    private final SysUserGroupService sysUserGroupService;

    @GetMapping("/list")
    @Operation(summary = "用户组列表查询", description = "通过主键查询用户组列表")
    @PreAuthorize("hasAuthority('system:userGroup:list')")
    public Response<List<SysUserGroupVO>> list(SysUserGroupDTO sysUserGroupDTO) {
        List<SysUserGroupVO> sysUserGroupVOList = sysUserGroupService.listAll(sysUserGroupDTO);
        return HttpResult.ok(sysUserGroupVOList);
    }

    @GetMapping("/listPage")
    @Operation(summary = "用户组列表查询", description = "通过主键查询用户组列表（分页）")
    @PreAuthorize("hasAuthority('system:userGroup:list')")
    public Response<TablePage<SysUserGroupVO>> listPage(SysUserGroupDTO sysUserGroupDTO, PageQuery pageQuery) {
        TablePage<SysUserGroupVO> tablePage = sysUserGroupService.listPage(sysUserGroupDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @GetMapping("/treeList")
    @Operation(summary = "用户组树形列表查询", description = "查询用户组树形列表")
    @PreAuthorize("hasAuthority('system:userGroup:query')")
    public Response<List<UserGroupTreeVO>> listTreeList() {
        List<UserGroupTreeVO> userGroupTreeVOList = sysUserGroupService.listTreeList();
        return HttpResult.ok(userGroupTreeVOList);
    }

    @PostMapping
    @Operation(summary = "用户组列表新增", description = "新增用户组")
    @OperateLog(title = "用户组管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:userGroup:add')")
    @PreventRepeatSubmit
    public Response<Boolean> addUserGroup(@Validated(RestGroup.AddGroup.class) @RequestBody SysUserGroupDTO sysUserGroupDTO) {
        if (sysUserGroupService.checkUserGroupNameUnique(sysUserGroupDTO)) {
            return HttpResult.failMessage("新增用户组「" + sysUserGroupDTO.getGroupName() + "」失败，用户组名称已存在");
        }

        return HttpResult.ok(sysUserGroupService.addUserGroup(sysUserGroupDTO));
    }

    @PutMapping
    @Operation(summary = "用户组列表修改", description = "修改用户组")
    @OperateLog(title = "用户组管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:userGroup:edit')")
    @PreventRepeatSubmit
    public Response<Boolean> editOne(@Validated(RestGroup.EditGroup.class) @RequestBody SysUserGroupDTO sysUserGroupDTO) {
        if (sysUserGroupService.checkUserGroupNameUnique(sysUserGroupDTO)) {
            return HttpResult.failMessage("修改用户组「" + sysUserGroupDTO.getGroupName() + "」失败，用户组名称已存在");
        }
        return HttpResult.ok(sysUserGroupService.editOne(sysUserGroupDTO));
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "用户组列表删除", description = "通过主键批量删除用户组列表")
    @OperateLog(title = "用户组管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:userGroup:remove')")
    @PreventRepeatSubmit
    public Response<Boolean> removeBatch(@PathVariable Long[] ids, @RequestBody List<String> userGroupIds) {
        return HttpResult.ok(sysUserGroupService.removeBatch(List.of(ids), userGroupIds));
    }

    @PostMapping("/export")
    @Operation(summary = "用户组数据导出", description = "导出用户组数据")
    @OperateLog(title = "用户组数据管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:user:export')")
    public void export(@RequestBody SysUserGroupDTO sysUserGroupDTO, HttpServletResponse response) {
        List<SysUserGroupVO> sysUserGroupVOList = sysUserGroupService.listAll(sysUserGroupDTO);
        ExcelHelper.exportExcel(sysUserGroupVOList, "用户组数据", SysUserGroupVO.class, response);
    }
}
