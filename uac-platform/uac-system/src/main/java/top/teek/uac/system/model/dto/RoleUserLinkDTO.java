package top.teek.uac.system.model.dto;

import top.teek.core.validate.RestGroup;
import top.teek.uac.system.model.po.RoleUserLink;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2023/12/28 0:15
 * @note
 */
@Data
@AutoMapper(target = RoleUserLink.class, reverseConvertGenerate = false)
public class RoleUserLinkDTO {
    /**
     * 主键 ID
     */
    @NotNull(message = "主键 ID 不能为空", groups = {RestGroup.EditGroup.class, RestGroup.DeleteGroup.class})
    private Long id;

    /**
     * 角色 ID
     */
    @NotBlank(message = "角色 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String roleId;

    /**
     * 用户 ID
     */
    @NotBlank(message = "用户 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String userId;
    
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
    @NotNull(message = "应用 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String appId;

    /**
     * 状态（0 异用 1 正常）
     */
    private Integer status;
}
