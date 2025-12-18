package top.teek.uac.system.model.dto.link;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.teek.core.validate.RestGroup;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Teeker
 * @date 2024/4/24 23:48:28
 * @note
 */
@Data
public class RoleLinkDeptListDTO {
    /**
     * 角色 ID
     */
    @NotBlank(message = "角色 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String roleId;
    
    /**
     * 部门 ID
     */
    @NotNull(message = "部门 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private List<String> deptIds;

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
