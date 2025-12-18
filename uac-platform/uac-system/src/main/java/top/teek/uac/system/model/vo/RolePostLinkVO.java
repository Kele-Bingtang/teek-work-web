package top.teek.uac.system.model.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2025/12/18 21:05:29
 * @since 1.0.0
 */
@Data
public class RolePostLinkVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 岗位 ID
     */
    private String postId;

    /**
     * 生效时间
     */
    private LocalDate validFrom;

    /**
     * 失效时间
     */
    private LocalDate expireOn;

    /**
     * 应用 ID
     */
    private String appId;
}
