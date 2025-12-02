package top.teek.uac.system.model.po;

import top.teek.mp.base.BaseDO;
import top.teek.uac.system.model.vo.RoleResourceLinkVO;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Teeker
 * @date 2023-19-12 00:19:27
 * @note 角色关联资源
*/
@TableName("t_role_resource_link")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RoleResourceLinkVO.class, reverseConvertGenerate = false)
public class RoleResourceLink extends BaseDO {
    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 资源 ID
     */
    private String resourceId;

    /**
     * 租户编号
     */
    private String tenantId;
    
    /**
     * 应用 ID
     */
    private String appId;

}