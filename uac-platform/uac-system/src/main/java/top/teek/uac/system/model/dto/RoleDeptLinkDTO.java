package top.teek.uac.system.model.dto;

import jakarta.validation.constraints.NotNull;
import top.teek.core.validate.RestGroup;
import top.teek.uac.system.model.po.RoleDeptLink;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2023/12/16 0:45
 * @note
 */
@Data
@AutoMapper(target = RoleDeptLink.class, reverseConvertGenerate = false)
public class RoleDeptLinkDTO {
    /**
     * 主键 ID
     */
    @NotBlank(message = "id 不能为空", groups = {RestGroup.EditGroup.class, RestGroup.DeleteGroup.class})
    private Long id;

    /**
     * 角色 ID
     */
    @NotBlank(message = "角色 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String roleId;

    /**
     * 部门 ID
     */
    @NotBlank(message = "部门 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String deptId;

    /**
     * 生效时间
     */
    @NotNull(message = "生效时间不能为空", groups = {RestGroup.AddGroup.class})
    private LocalDate validFrom;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空", groups = {RestGroup.AddGroup.class})
    private LocalDate expireOn;

    /**
     * 应用 ID
     */
    @NotBlank(message = "应用 ID 不能为空", groups = {RestGroup.QueryGroup.class, RestGroup.AddGroup.class})
    private String appId;

    /**
     * 状态 1 正常 0 异常
     */
    private Integer status;
}
