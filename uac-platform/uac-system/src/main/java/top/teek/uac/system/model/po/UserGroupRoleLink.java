package top.teek.uac.system.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.teek.mp.base.BaseDO;
import top.teek.uac.system.model.vo.UserGroupRoleLinkVO;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2023-30-12 00:30:42
 * @note 用户组关联角色
*/
@TableName("t_user_group_role_link")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AutoMapper(target = UserGroupRoleLinkVO.class, reverseConvertGenerate = false)
public class UserGroupRoleLink extends BaseDO {
    /**
     * 用户组 ID
     */
    private String userGroupId;

    /**
     * 角色 ID
     */
    private String roleId;

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