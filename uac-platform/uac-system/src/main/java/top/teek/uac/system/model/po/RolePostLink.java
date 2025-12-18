package top.teek.uac.system.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.teek.mp.base.BaseDO;
import top.teek.uac.system.model.vo.RolePostLinkVO;

import java.time.LocalDate;



/**
 * @author Teeker
 * @date 2025/12/18 21:05:02
 * @since 1.0.0
 */
@TableName("t_role_post_link")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AutoMapper(target = RolePostLinkVO.class, reverseConvertGenerate = false)
public class RolePostLink extends BaseDO {
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
     * 租户编号
     */
    private String tenantId;

    /**
     * 应用 ID
     */
    private String appId;
}
