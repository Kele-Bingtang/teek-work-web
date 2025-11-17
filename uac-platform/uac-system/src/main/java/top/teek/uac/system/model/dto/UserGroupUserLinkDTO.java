package top.teek.uac.system.model.dto;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import top.teek.core.validate.RestGroup;
import top.teek.uac.system.model.po.UserGroupUserLink;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2023/12/27 23:50
 * @note
 */
@Data
@AutoMapper(target = UserGroupUserLink.class, reverseConvertGenerate = false)
public class UserGroupUserLinkDTO {
    /**
     * 主键
     */
    @NotNull(message = "id 不能为空", groups = {RestGroup.EditGroup.class, RestGroup.DeleteGroup.class})
    private String id;
    
    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 用户组 UID
     */
    private String userGroupId;

    /**
     * 生效时间
     */
    @NotNull(message = "生效时间不能为空", groups = {RestGroup.AddGroup.class, RestGroup.EditGroup.class})
    private LocalDate validFrom;

    /**
     * 失效时间
     */
    @NotNull(message = "过期时间不能为空", groups = {RestGroup.AddGroup.class, RestGroup.EditGroup.class})
    private LocalDate expireOn;

    /**
     * 状态 1 正常 0 异常
     */
    private Integer status;
}
