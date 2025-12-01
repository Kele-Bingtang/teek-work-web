package top.teek.uac.system.service.link.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import top.teek.core.constants.ColumnConstant;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.mapper.RoleDeptLinkMapper;
import top.teek.uac.system.model.dto.RoleDeptLinkDTO;
import top.teek.uac.system.model.dto.link.RoleLinkDeptsDTO;
import top.teek.uac.system.model.dto.link.RoleLinkInfoDTO;
import top.teek.uac.system.model.po.RoleDeptLink;
import top.teek.uac.system.model.po.SysDept;
import top.teek.uac.system.model.po.UserGroupUserLink;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.service.link.RoleDeptLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;
import top.teek.utils.TreeBuildUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    public TablePage<RoleLinkVO> listRoleLinkByDeptId(String deptId, String appId, RoleLinkInfoDTO roleLinkInfoDTO, PageQuery pageQuery) {
        QueryWrapper<UserGroupUserLink> queryWrapper = Wrappers.query();
        queryWrapper.eq("tsr.is_deleted", 0)
                .eq("trdl.dept_id", deptId)
                .like(StringUtil.hasText(roleLinkInfoDTO.getRoleCode()), "tsr.role_code", roleLinkInfoDTO.getRoleCode())
                .like(StringUtil.hasText(roleLinkInfoDTO.getRoleName()), "tsr.role_name", roleLinkInfoDTO.getRoleName());

        IPage<RoleLinkVO> deptLinkRoleVOIPage = baseMapper.listRoleLinkByDeptId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(deptLinkRoleVOIPage);
    }

    @Override
    public List<RoleBindSelectVO> listWithSelectedByDeptId(String deptId) {
        return baseMapper.listWithSelectedByDeptId(deptId);
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

    @Override
    public Boolean updateOne(RoleDeptLinkDTO roleDeptLinkDTO) {
        RoleDeptLink roleDeptLink = MapstructUtil.convert(roleDeptLinkDTO, RoleDeptLink.class);
        if (Objects.isNull(roleDeptLink.getValidFrom())) {
            // 默认为当前时间
            roleDeptLink.setValidFrom(LocalDate.now());
        }
        if (Objects.isNull(roleDeptLink.getExpireOn())) {
            // 默认为 3 年
            roleDeptLink.setExpireOn(LocalDate.now().plusYears(3));
        }
        return baseMapper.updateById(roleDeptLink) > 0;
    }

    @Override
    public boolean removeDeptFromRole(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }
}




