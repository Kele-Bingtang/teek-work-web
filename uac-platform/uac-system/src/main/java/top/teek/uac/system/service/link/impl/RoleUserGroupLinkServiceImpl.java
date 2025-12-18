package top.teek.uac.system.service.link.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.core.constant.CommonConstant;
import top.teek.uac.system.mapper.RoleUserGroupLinkMapper;
import top.teek.uac.system.model.dto.RoleUserGroupLinkDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserGroupListDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkRoleListDTO;
import top.teek.uac.system.model.po.RoleUserGroupLink;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.po.SysUserGroup;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;
import top.teek.uac.system.service.link.RoleUserGroupLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_user_group_link(用户组关联角色表)】的数据库操作Service实现
 */
@Service
public class RoleUserGroupLinkServiceImpl extends ServiceImpl<RoleUserGroupLinkMapper, RoleUserGroupLink> implements RoleUserGroupLinkService {

    // ------- 用户组关联角色相关 API（以用户组为主）-------

    @Override
    public TablePage<RoleLinkVO> listRoleLinkByGroupId(String appId, String userGroupId, SysRoleDTO sysRoleDTO, PageQuery pageQuery) {
        QueryWrapper<SysRole> queryWrapper = Wrappers.query();
        
        queryWrapper.eq("tsr.is_deleted", 0)
                .eq("trugl.app_id", appId)
                .eq("trugl.user_group_id", userGroupId)
                .like(StringUtil.hasText(sysRoleDTO.getRoleCode()), "tsr.role_code", sysRoleDTO.getRoleCode())
                .like(StringUtil.hasText(sysRoleDTO.getRoleName()), "tsr.role_name", sysRoleDTO.getRoleName());

        IPage<RoleLinkVO> userGroupLinkRoleVOIPage = baseMapper.listRoleLinkByGroupId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userGroupLinkRoleVOIPage);
    }

    @Override
    public List<RoleBindSelectVO> listWithSelectedByGroupId(String appId, String userGroupId) {
        return baseMapper.listWithSelectedByGroupId(appId, userGroupId);
    }


    @Override
    public boolean addRoleListToUserGroup(UserGroupLinkRoleListDTO userGroupLinkRoleListDTO) {
        List<String> roleIds = userGroupLinkRoleListDTO.getRoleIds();

        List<RoleUserGroupLink> userGroupLinkList = ListUtil.newArrayList(roleIds, roleId ->
                        new RoleUserGroupLink().setRoleId(roleId)
                                .setUserGroupId(userGroupLinkRoleListDTO.getUserGroupId())
                                .setValidFrom(Optional.ofNullable(userGroupLinkRoleListDTO.getValidFrom()).orElse(LocalDate.now()))
                                .setExpireOn(Optional.ofNullable(userGroupLinkRoleListDTO.getExpireOn()).orElse(LocalDate.now().plusYears(CommonConstant.expireOnNum)))
                                .setAppId(userGroupLinkRoleListDTO.getAppId())
                , RoleUserGroupLink.class);

        return Db.saveBatch(userGroupLinkList);
    }

    @Override
    public boolean checkRoleListExistUserGroup(UserGroupLinkRoleListDTO userGroupLinkRoleListDTO) {
        return baseMapper.exists(Wrappers.<RoleUserGroupLink>lambdaQuery()
                .in(RoleUserGroupLink::getRoleId, userGroupLinkRoleListDTO.getRoleIds())
                .eq(RoleUserGroupLink::getUserGroupId, userGroupLinkRoleListDTO.getUserGroupId())
                .eq(StringUtil.hasText(userGroupLinkRoleListDTO.getAppId()), RoleUserGroupLink::getAppId, userGroupLinkRoleListDTO.getAppId()));
    }

    // ------- 角色关联用户组相关 API（以角色为主）-------

    @Override
    public TablePage<UserGroupLinkVO> listUserGroupByRoleId(String appId, String roleId, SysUserGroupDTO sysUserGroupDTO, PageQuery pageQuery) {
        QueryWrapper<SysUserGroup> queryWrapper = Wrappers.query();
        
        queryWrapper.eq("tsug.is_deleted", 0)
                .eq("trugl.app_id", appId)
                .eq("trugl.role_id", roleId)
                .like(StringUtil.hasText(sysUserGroupDTO.getGroupName()), "tsug.group_name", sysUserGroupDTO.getGroupName())
                .like(StringUtil.hasText(sysUserGroupDTO.getOwnerId()), "tsug.owner_id", sysUserGroupDTO.getOwnerId());
        IPage<UserGroupLinkVO> userGroupLinkUserVOIPage = baseMapper.listUserGroupByRoleId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userGroupLinkUserVOIPage);
    }


    @Override
    public List<UserGroupBindSelectVO> listWithSelectedByRoleId(String appId, String roleId) {
        return baseMapper.listWithSelectedByRoleId(appId, roleId);
    }

    @Override
    public boolean addUserGroupListToRole(RoleLinkUserGroupListDTO roleLinkUserGroupListDTO) {
        List<String> userGroupIds = roleLinkUserGroupListDTO.getUserGroupIds();

        List<RoleUserGroupLink> userGroupLinkList = ListUtil.newArrayList(userGroupIds, userGroupId ->
                        new RoleUserGroupLink().setUserGroupId(userGroupId)
                                .setRoleId(roleLinkUserGroupListDTO.getRoleId())
                                .setValidFrom(Optional.ofNullable(roleLinkUserGroupListDTO.getValidFrom()).orElse(LocalDate.now()))
                                .setExpireOn(Optional.ofNullable(roleLinkUserGroupListDTO.getExpireOn()).orElse(LocalDate.now().plusYears(CommonConstant.expireOnNum)))
                                .setAppId(roleLinkUserGroupListDTO.getAppId())
                , RoleUserGroupLink.class);

        return Db.saveBatch(userGroupLinkList);
    }

    @Override
    public boolean checkUserGroupListExistRole(RoleLinkUserGroupListDTO roleLinkUserGroupListDTO) {
        return baseMapper.exists(Wrappers.<RoleUserGroupLink>lambdaQuery()
                .eq(RoleUserGroupLink::getRoleId, roleLinkUserGroupListDTO.getRoleId())
                .in(RoleUserGroupLink::getUserGroupId, roleLinkUserGroupListDTO.getUserGroupIds())
                .eq(StringUtil.hasText(roleLinkUserGroupListDTO.getAppId()), RoleUserGroupLink::getAppId, roleLinkUserGroupListDTO.getAppId()));
    }

    // ------- 公共 API -------
    
    @Override
    public Boolean editRoleUserGroupLink(RoleUserGroupLinkDTO roleUserGroupLinkDTO) {
        RoleUserGroupLink roleUserGroupLink = MapstructUtil.convert(roleUserGroupLinkDTO, RoleUserGroupLink.class);
        if (Objects.isNull(roleUserGroupLink.getValidFrom())) {
            // 默认为当前时间
            roleUserGroupLink.setValidFrom(LocalDate.now());
        }
        if (Objects.isNull(roleUserGroupLink.getExpireOn())) {
            // 默认为 3 年
            roleUserGroupLink.setExpireOn(LocalDate.now().plusYears(CommonConstant.expireOnNum));
        }
        return baseMapper.updateById(roleUserGroupLink) > 0;
    }

    @Override
    public boolean removeRoleUserGroupLink(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }
}




