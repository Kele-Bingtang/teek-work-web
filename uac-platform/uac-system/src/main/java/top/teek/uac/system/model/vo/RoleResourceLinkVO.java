package top.teek.uac.system.model.vo;

import lombok.Data;

/**
 * @author Teeker
 * @date 2023/12/16 0:13
 * @note
 */
@Data
public class RoleResourceLinkVO {
    /**
     * ID
     */
    private Long id;
    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 资源 ID
     */
    private String resourceId;

    /**
     * 资源名
     */
    private String resourceName;
}
