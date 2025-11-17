package top.teek.uac.system.service.link;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.uac.system.model.dto.link.RoleLinkMenuDTO;
import top.teek.uac.system.model.po.RoleMenuLink;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_menu_link(角色关联菜单表)】的数据库操作Service
 */
public interface RoleMenuLinkService extends IService<RoleMenuLink> {

    List<String>  listMenuIdsByRoleId(String roleId, String appId, String tenantId);

    List<Tree<String>> listMenuListByRoleId(String roleId, String appId);

    boolean addMenusToRole(RoleLinkMenuDTO roleLinkMenuDTO, boolean removeLink);

}
