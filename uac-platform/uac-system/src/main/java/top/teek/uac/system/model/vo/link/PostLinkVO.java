package top.teek.uac.system.model.vo.link;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Teeker
 * @date 2025/12/18 21:48:39
 * @since 1.0.0
 */
@Data
public class PostLinkVO {

    /**
     * 岗位 ID
     */
    private String postId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

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
     * 状态 1 正常 0 异常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
