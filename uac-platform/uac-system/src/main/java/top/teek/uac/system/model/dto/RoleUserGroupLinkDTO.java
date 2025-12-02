package top.teek.uac.system.model.dto;

import top.teek.core.validate.RestGroup;
import top.teek.uac.system.model.po.RoleUserGroupLink;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2023/12/28 0:03
 * @note
 */
@Data
@AutoMapper(target = RoleUserGroupLink.class, reverseConvertGenerate = false)
public class RoleUserGroupLinkDTO {
    /**
     * 主键
     */
    @NotNull(message = "id 不能为空", groups = {RestGroup.EditGroup.class, RestGroup.DeleteGroup.class})
    private String id;

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 用户组 ID
     */
    private String userGroupId;

    /**
     * 生效时间
     */
    @NotNull(message = "生效时间不能为空", groups = {RestGroup.AddGroup.class})
    private LocalDate validFrom;

    /**
     * 失效时间
     */
    @NotNull(message = "过期时间不能为空", groups = {RestGroup.AddGroup.class})
    private LocalDate expireOn;

    /**
     * 应用 ID
     */
    @NotBlank(message = "应用 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String appId;
    
    /**
     * 状态 1 正常 0 异常
     */
    private Integer status;
}
