package top.teek.uac.system.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.teek.core.http.HttpResult;
import top.teek.core.http.Response;
import top.teek.core.validate.RestGroup;
import top.teek.excel.helper.ExcelHelper;
import top.teek.idempotent.annotation.PreventRepeatSubmit;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.security.domain.LoginUser;
import top.teek.uac.core.helper.UacHelper;
import top.teek.uac.core.log.annotation.OperateLog;
import top.teek.uac.core.log.enums.BusinessType;
import top.teek.uac.system.model.dto.SysUserDTO;
import top.teek.uac.system.model.vo.SysUserVO;
import top.teek.uac.system.service.system.SysUserService;
import top.teek.utils.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Teeker
 * @date 2023/12/4 19:58
 * @note
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final PasswordEncoder passwordEncoder;
    private final SysUserService sysUserService;

    @GetMapping("/list")
    @Operation(summary = "用户列表查询", description = "通过条件查询用户列表）")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Response<List<SysUserVO>> list(SysUserDTO sysUserDTO) {
        List<SysUserVO> sysUserVOList = sysUserService.listAll(sysUserDTO);
        return HttpResult.ok(sysUserVOList);
    }

    @GetMapping("/listPage")
    @Operation(summary = "用户列表查询", description = "通过条件查询用户列表（分页）")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Response<TablePage<SysUserVO>> listPage(SysUserDTO sysUserDTO, PageQuery pageQuery) {
        TablePage<SysUserVO> tablePage = sysUserService.listPage(sysUserDTO, pageQuery);
        return HttpResult.ok(tablePage);
    }

    @PostMapping
    @Operation(summary = "用户列表新增", description = "新增用户列表")
    @OperateLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    @PreventRepeatSubmit
    public Response<Boolean> addUser(@Validated(RestGroup.AddGroup.class) @RequestBody SysUserDTO sysUserDTO) {
        if (sysUserService.checkUserNameUnique(sysUserDTO)) {
            return HttpResult.failMessage("新增用户「" + sysUserDTO.getUsername() + "」失败，登录账号已存在");
        }
        if (StringUtil.hasText(sysUserDTO.getPhone()) && sysUserService.checkPhoneUnique(sysUserDTO)) {
            return HttpResult.failMessage("新增用户「" + sysUserDTO.getUsername() + "」失败，手机号「" + sysUserDTO.getPhone() + "」已存在");
        }
        if (StringUtil.hasText(sysUserDTO.getEmail()) && sysUserService.checkEmailUnique(sysUserDTO)) {
            return HttpResult.failMessage("新增用户「" + sysUserDTO.getUsername() + "」失败，邮箱账号「" + sysUserDTO.getEmail() + "」已存在");
        }

        return HttpResult.ok(sysUserService.addUser(sysUserDTO));
    }

    @PutMapping
    @Operation(summary = "用户列表修改", description = "修改用户列表")
    @OperateLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PreventRepeatSubmit
    public Response<Boolean> editUser(@Validated(RestGroup.EditGroup.class) @RequestBody SysUserDTO sysUserDTO) {
        if (sysUserService.checkUserNameUnique(sysUserDTO)) {
            return HttpResult.failMessage("修改用户「" + sysUserDTO.getUsername() + "」失败，登录账号已存在");
        }
        if (StringUtil.hasText(sysUserDTO.getPhone()) && sysUserService.checkPhoneUnique(sysUserDTO)) {
            return HttpResult.failMessage("修改用户「" + sysUserDTO.getUsername() + "」失败，手机号「" + sysUserDTO.getPhone() + "」已存在");
        }
        if (StringUtil.hasText(sysUserDTO.getEmail()) && sysUserService.checkEmailUnique(sysUserDTO)) {
            return HttpResult.failMessage("修改用户「" + sysUserDTO.getUsername() + "」失败，邮箱账号「" + sysUserDTO.getEmail() + "」已存在");
        }

        return HttpResult.ok(sysUserService.editUser(sysUserDTO));
    }

    @PutMapping("/resetPassword")
    @OperateLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreventRepeatSubmit
    public Response<Boolean> resetPassword(@RequestBody SysUserDTO sysUserDTO) {
        sysUserDTO.setPassword(passwordEncoder.encode(sysUserDTO.getPassword()));
        boolean result = sysUserService.updatePassword(sysUserDTO.getUserId(), sysUserDTO.getPassword());
        return HttpResult.ok(result);
    }

    @DeleteMapping("/{ids}")
    @Operation(summary = "用户列表删除", description = "通过主键批量删除用户列表")
    @OperateLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:user:remove')")
    @PreventRepeatSubmit
    public Response<Boolean> removeBatch(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids, @RequestBody List<String> userIds) {
        LoginUser loginUser = UacHelper.getLoginUser();
        if (Objects.nonNull(loginUser) && userIds.contains(loginUser.getUserId())) {
            return HttpResult.failMessage("当前用户不能删除");
        }
        return HttpResult.ok(sysUserService.removeBatch(List.of(ids), userIds));
    }

    @PostMapping("/export")
    @Operation(summary = "用户数据导出", description = "导出用户数据")
    @OperateLog(title = "用户数据管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:user:export')")
    public void export(@RequestBody SysUserDTO sysUserDTO, HttpServletResponse response) {
        List<SysUserVO> sysUserVOList = sysUserService.listAll(sysUserDTO);
        ExcelHelper.exportExcel(sysUserVOList, "用户数据", SysUserVO.class, response);
    }
}
