package top.teek.uac.system.model.vo.link;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2024/3/16 2:12
 * @note 用户组被关联数据，如被用户关联、被角色关联
 */
@Data
public class UserGroupLinkUserVO {
    /**
     * 用户组 ID
     */
    private String groupId;

    /**
     * 用户组名
     */
    private String groupName;

    /**
     * 群组类型
     */
    private String groupType;

    /**
     * 负责人 ID
     */
    private String ownerId;

    /**
     * 负责人 username
     */
    private String ownerName;

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
     * 状态（0 异用 1 正常）
     */
    private Integer status;
}
