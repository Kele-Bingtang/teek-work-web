package top.teek.uac.system.service.link.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import top.teek.core.constants.ColumnConstant;
import top.teek.uac.system.mapper.RoleMenuLinkMapper;
import top.teek.uac.system.model.dto.link.RoleLinkMenuDTO;
import top.teek.uac.system.model.po.RoleMenuLink;
import top.teek.uac.system.model.po.SysMenu;
import top.teek.uac.system.service.link.RoleMenuLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.StringUtil;
import top.teek.utils.TreeBuildUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_menu_link(角色关联菜单表)】的数据库操作Service实现
 */
@Service
public class RoleMenuLinkServiceImpl extends ServiceImpl<RoleMenuLinkMapper, RoleMenuLink> implements RoleMenuLinkService {


    @Override
    public List<String> listMenuIdsByRoleId(String roleId, String appId, String tenantId) {

        List<RoleMenuLink> roleMenuLinks = baseMapper.selectList(Wrappers.<RoleMenuLink>lambdaQuery()
                .eq(RoleMenuLink::getRoleId, roleId)
                .eq(StringUtil.hasText(appId), RoleMenuLink::getAppId, appId)
                .eq(StringUtil.hasText(tenantId), RoleMenuLink::getTenantId, tenantId)
        );

        if (ListUtil.isNotEmpty(roleMenuLinks)) {
            return roleMenuLinks.stream().map(RoleMenuLink::getMenuId).toList();
        }

        return List.of();
    }

    @Override
    public List<Tree<String>> listMenuListByRoleId(String roleId, String appId) {
        List<SysMenu> sysMenuList = baseMapper.listMenuListByRoleId(roleId, appId);
        return buildMenuTree(sysMenuList);
    }

    @Override
    public boolean addMenusToRole(RoleLinkMenuDTO roleLinkMenuDTO, boolean removeLink) {
        if (removeLink) {
            // 删除角色与菜单关联
            baseMapper.delete(Wrappers.<RoleMenuLink>lambdaQuery()
                    .eq(RoleMenuLink::getRoleId, roleLinkMenuDTO.getRoleId()));
        }

        List<String> selectedMenuIds = roleLinkMenuDTO.getSelectedMenuIds();

        List<RoleMenuLink> roleMenuLinkList = ListUtil.newArrayList(selectedMenuIds, menuId ->
                        new RoleMenuLink().setMenuId(menuId)
                                .setRoleId(roleLinkMenuDTO.getRoleId())
                                .setAppId(roleLinkMenuDTO.getAppId())
                , RoleMenuLink.class);

        return Db.saveBatch(roleMenuLinkList);
    }

    /**
     * 构建前端所需要下拉树结构
     */
    private List<Tree<String>> buildMenuTree(List<SysMenu> sysMenuList) {
        if (CollUtil.isEmpty(sysMenuList)) {
            return Collections.emptyList();
        }

        return TreeBuildUtil.build(sysMenuList, ColumnConstant.PARENT_ID, TreeNodeConfig.DEFAULT_CONFIG.setIdKey("value").setNameKey("label"), (treeNode, tree) -> {
                    tree.setId(treeNode.getMenuId())
                            .setParentId(treeNode.getParentId())
                            .setName(treeNode.getMenuName())
                            .setWeight(treeNode.getOrderNum())
                            .putExtra("icon", treeNode.getIcon());

                    // 如果节点是选中状态，则设置选中样式
                    tree.putExtra("class", treeNode.isSelected() ? "selected" : "");
                }
        );
    }
}




