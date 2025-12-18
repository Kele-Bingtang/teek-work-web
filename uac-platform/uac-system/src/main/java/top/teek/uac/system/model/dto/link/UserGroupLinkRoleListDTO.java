package top.teek.uac.system.model.dto.link;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.teek.core.validate.RestGroup;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Teeker
 * @date 2024/3/18 20:42
 * @note
 */
@Data
public class UserGroupLinkRoleListDTO {
    /**
     * 用户组 ID
     */
    @NotBlank(message = "用户组 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String userGroupId;
    
    /**
     * 用户 ID
     */
    @NotNull(message = "角色 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private List<String> roleIds;

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
    @NotBlank(message = "应用 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String appId;
}
