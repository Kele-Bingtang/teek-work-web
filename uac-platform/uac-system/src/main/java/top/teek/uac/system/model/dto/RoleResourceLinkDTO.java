package top.teek.uac.system.model.dto;

import top.teek.core.validate.RestGroup;
import top.teek.uac.system.model.po.RoleResourceLink;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Teeker
 * @date 2023/12/16 0:17
 * @note
 */
@Data
@AutoMapper(target = RoleResourceLink.class, reverseConvertGenerate = false)
public class RoleResourceLinkDTO {
    @NotBlank(message = "id 不能为空", groups = {RestGroup.EditGroup.class, RestGroup.DeleteGroup.class})
    private Long id;
    
    @NotBlank(message = "角色 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String roleId;

    @NotBlank(message = "资源 ID 不能为空", groups = {RestGroup.AddGroup.class})
    private String resourceId;

    @NotBlank(message = "应用 ID 不能为空", groups = {RestGroup.QueryGroup.class, RestGroup.AddGroup.class})
    private String appId;
}
