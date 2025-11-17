package top.teek.uac.system.service.link.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import top.teek.core.constants.ColumnConstant;
import top.teek.uac.system.mapper.RoleDeptLinkMapper;
import top.teek.uac.system.model.dto.link.RoleLinkDeptsDTO;
import top.teek.uac.system.model.po.RoleDeptLink;
import top.teek.uac.system.model.po.SysDept;
import top.teek.uac.system.service.link.RoleDeptLinkService;
import top.teek.utils.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import top.teek.utils.StringUtil;
import top.teek.utils.TreeBuildUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_dept_link(角色关联部门表)】的数据库操作Service实现
 */
@Service
public class RoleDeptLinkServiceImpl extends ServiceImpl<RoleDeptLinkMapper, RoleDeptLink> implements RoleDeptLinkService {

    @Override
    public List<Tree<String>> listDeptListByRoleId(String roleId, String appId) {
        List<SysDept> sysDeptList = baseMapper.listDeptListByRoleId(roleId, appId);
        return buildDeptTree(sysDeptList);
    }

    @Override
    public List<String> listDeptIdsByRoleId(String roleId, String appId) {
        List<RoleDeptLink> roleDeptLinkList = baseMapper.selectList(Wrappers.<RoleDeptLink>lambdaQuery()
                .eq(RoleDeptLink::getRoleId, roleId)
                .eq(StringUtil.hasText(appId), RoleDeptLink::getAppId, appId)
        );

        if (ListUtil.isNotEmpty(roleDeptLinkList)) {
            return roleDeptLinkList.stream().map(RoleDeptLink::getDeptId).toList();
        }

        return List.of();
    }

    @Override
    public Boolean addDeptsToRole(RoleLinkDeptsDTO roleLinkDeptsDTO, boolean removeLink) {
        
        if (removeLink) {
            // 删除角色与部门关联
            baseMapper.delete(Wrappers.<RoleDeptLink>lambdaQuery()
                    .eq(RoleDeptLink::getRoleId, roleLinkDeptsDTO.getRoleId()));
        }
        List<String> selectedDeptIds = roleLinkDeptsDTO.getSelectedDeptIds();

        List<RoleDeptLink> roleDeptLinkList = ListUtil.newArrayList(selectedDeptIds, deptId ->
                        new RoleDeptLink().setDeptId(deptId)
                                .setRoleId(roleLinkDeptsDTO.getRoleId())
                                .setAppId(roleLinkDeptsDTO.getAppId())
                , RoleDeptLink.class);

        return Db.saveBatch(roleDeptLinkList);
    }

    /**
     * 构建前端所需要下拉树结构
     */
    private List<Tree<String>> buildDeptTree(List<SysDept> sysDeptList) {
        if (CollUtil.isEmpty(sysDeptList)) {
            return Collections.emptyList();
        }

        return TreeBuildUtil.build(sysDeptList, ColumnConstant.PARENT_ID, TreeNodeConfig.DEFAULT_CONFIG.setIdKey("value").setNameKey("label"), (treeNode, tree) -> {
                    tree.setId(treeNode.getDeptId())
                            .setParentId(treeNode.getParentId())
                            .setName(treeNode.getDeptName())
                            .setWeight(treeNode.getOrderNum())
                            .putExtra("icon", treeNode.getIcon());
                    // 如果节点是选中状态，则设置选中样式
                    tree.putExtra("class", treeNode.isSelected() ? "selected" : "");
                }
        );
    }
}




