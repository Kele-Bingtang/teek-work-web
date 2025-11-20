package top.teek.uac.system.model.vo.link;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2024/3/16 2:16
 * @note 角色被关联数据，如被用户关联、被用户组关联
 */
@Data
public class RoleLinkVO {

    /**
     * 角色码
     */
    private String roleCode;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 负责人 ID
     */
    private String ownerId;

    /**
     * 关联 ID
     */
    private Long linkId;

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

    /**
     * 状态（0 异用 1 正常）
     */
    private Integer status;
}
