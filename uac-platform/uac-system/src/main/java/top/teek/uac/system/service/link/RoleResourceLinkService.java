package top.teek.uac.system.service.link;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.uac.system.model.dto.SysResourceDTO;
import top.teek.uac.system.model.dto.link.RoleLinkResourceDTO;
import top.teek.uac.system.model.po.RoleResourceLink;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_resource_link(角色关联资源表)】的数据库操作Service
 */
public interface RoleResourceLinkService extends IService<RoleResourceLink> {

    // ------- 资源关联角色相关 API（以资源为主）-------

    // ------- 角色关联资源相关 API（以角色为主）-------

    List<Tree<String>> listResourceListByRoleId(String appId, String roleId, SysResourceDTO sysResourceDTO);
    
    List<String> listResourceIdsByRoleId(String appId, String roleId, String tenantId);

    boolean addResourceListToRole(RoleLinkResourceDTO roleLinkResourceDTO, boolean removeLink);

    // ------- 公共 API -------
    
    boolean removeRoleResourceLink(List<Long> ids);
}
