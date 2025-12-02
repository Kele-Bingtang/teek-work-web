package top.teek.uac.system.service.link.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import top.teek.core.constants.ColumnConstant;
import top.teek.uac.system.mapper.RoleResourceLinkMapper;
import top.teek.uac.system.model.dto.link.RoleLinkResourceDTO;
import top.teek.uac.system.model.po.RoleResourceLink;
import top.teek.uac.system.model.po.SysResource;
import top.teek.uac.system.service.link.RoleResourceLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.StringUtil;
import top.teek.utils.TreeBuildUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_resource_link(角色关联资源表)】的数据库操作Service实现
 */
@Service
public class RoleResourceLinkServiceImpl extends ServiceImpl<RoleResourceLinkMapper, RoleResourceLink> implements RoleResourceLinkService {

    // ------- 资源关联角色相关 API（以资源为主）-------

    // ------- 角色关联资源相关 API（以角色为主）-------

    @Override
    public List<String> listResourceIdsByRoleId(String roleId, String appId, String tenantId) {

        List<RoleResourceLink> roleResourceLinks = baseMapper.selectList(Wrappers.<RoleResourceLink>lambdaQuery()
                .eq(RoleResourceLink::getRoleId, roleId)
                .eq(StringUtil.hasText(appId), RoleResourceLink::getAppId, appId)
                .eq(StringUtil.hasText(tenantId), RoleResourceLink::getTenantId, tenantId)
        );

        if (ListUtil.isNotEmpty(roleResourceLinks)) {
            return roleResourceLinks.stream().map(RoleResourceLink::getResourceId).toList();
        }

        return List.of();
    }

    @Override
    public List<Tree<String>> listResourceListByRoleId(String roleId, String appId) {
        List<SysResource> sysResourceList = baseMapper.listResourceListByRoleId(roleId, appId);
        return buildResourceTree(sysResourceList);
    }

    @Override
    public boolean addResourceListToRole(RoleLinkResourceDTO roleLinkResourceDTO, boolean removeLink) {
        if (removeLink) {
            // 删除角色与资源关联
            baseMapper.delete(Wrappers.<RoleResourceLink>lambdaQuery()
                    .eq(RoleResourceLink::getRoleId, roleLinkResourceDTO.getRoleId()));
        }

        List<String> resourceIds = roleLinkResourceDTO.getResourceIds();

        List<RoleResourceLink> roleResourceLinkList = ListUtil.newArrayList(resourceIds, resourceId ->
                        new RoleResourceLink().setResourceId(resourceId)
                                .setRoleId(roleLinkResourceDTO.getRoleId())
                                .setAppId(roleLinkResourceDTO.getAppId())
                , RoleResourceLink.class);

        return Db.saveBatch(roleResourceLinkList);
    }

    /**
     * 构建前端所需要下拉树结构
     */
    private List<Tree<String>> buildResourceTree(List<SysResource> sysResourceList) {
        if (CollUtil.isEmpty(sysResourceList)) {
            return Collections.emptyList();
        }

        return TreeBuildUtil.build(sysResourceList, ColumnConstant.PARENT_ID, TreeNodeConfig.DEFAULT_CONFIG.setIdKey("value").setNameKey("label"), (treeNode, tree) -> {
                    tree.setId(treeNode.getResourceId())
                            .setParentId(treeNode.getParentId())
                            .setName(treeNode.getResourceName())
                            .setWeight(treeNode.getOrderNum())
                            .putExtra("icon", treeNode.getIcon());

                    // 如果节点是选中状态，则设置选中样式
                    tree.putExtra("class", treeNode.isSelected() ? "selected" : "");
                }
        );
    }

    @Override
    public boolean removeRoleResourceLink(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }
}




