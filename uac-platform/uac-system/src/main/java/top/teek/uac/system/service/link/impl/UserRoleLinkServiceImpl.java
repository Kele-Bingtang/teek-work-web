package top.teek.uac.system.service.link.impl;

import top.teek.core.constants.ColumnConstant;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.mapper.UserRoleLinkMapper;
import top.teek.uac.system.model.dto.UserRoleLinkDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUsersDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkRolesDTO;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.po.UserRoleLink;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.UserRoleLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_user_role_link(用户关联角色表)】的数据库操作Service实现
 */
@Service
public class UserRoleLinkServiceImpl extends ServiceImpl<UserRoleLinkMapper, UserRoleLink> implements UserRoleLinkService {

    @Override
    public boolean checkRoleExistUser(RoleLinkUsersDTO roleLinkUsersDTO) {
        return baseMapper.exists(Wrappers.<UserRoleLink>lambdaQuery()
                .eq(UserRoleLink::getRoleId, roleLinkUsersDTO.getRoleId())
                .in(UserRoleLink::getUserId, roleLinkUsersDTO.getUserIds())
                .eq(StringUtil.hasText(roleLinkUsersDTO.getAppId()), UserRoleLink::getAppId, roleLinkUsersDTO.getAppId()));
    }

    @Override
    public boolean checkUserExistRoles(UserLinkRolesDTO userLinkRolesDTO) {
        return baseMapper.exists(Wrappers.<UserRoleLink>lambdaQuery()
                .eq(UserRoleLink::getUserId, userLinkRolesDTO.getUserId())
                .in(UserRoleLink::getRoleId, userLinkRolesDTO.getRoleIds())
                .eq(StringUtil.hasText(userLinkRolesDTO.getAppId()), UserRoleLink::getAppId, userLinkRolesDTO.getAppId()));
    }

    @Override
    public boolean addRolesToUser(UserLinkRolesDTO userLinkRolesDTO) {
        List<String> roleIds = userLinkRolesDTO.getRoleIds();

        List<UserRoleLink> userRoleLinkList = ListUtil.newArrayList(roleIds, roleId ->
                        new UserRoleLink().setRoleId(roleId)
                                .setUserId(userLinkRolesDTO.getUserId())
                                .setValidFrom(userLinkRolesDTO.getValidFrom())
                                .setExpireOn(userLinkRolesDTO.getExpireOn())
                                .setAppId(userLinkRolesDTO.getAppId())
                , UserRoleLink.class);

        return Db.saveBatch(userRoleLinkList);
    }

    @Override
    public boolean addUsersToRole(RoleLinkUsersDTO roleLinkUsersDTO) {
        List<String> userIds = roleLinkUsersDTO.getUserIds();

        List<UserRoleLink> userRoleLinkList = ListUtil.newArrayList(userIds, userId ->
                        new UserRoleLink().setUserId(userId)
                                .setRoleId(roleLinkUsersDTO.getRoleId())
                                .setValidFrom(roleLinkUsersDTO.getValidFrom())
                                .setExpireOn(roleLinkUsersDTO.getExpireOn())
                                .setAppId(roleLinkUsersDTO.getAppId())
                , UserRoleLink.class);

        return Db.saveBatch(userRoleLinkList);
    }

    @Override
    public boolean updateOne(UserRoleLinkDTO userRoleLinkDTO) {
        UserRoleLink userRoleLink = MapstructUtil.convert(userRoleLinkDTO, UserRoleLink.class);
        return baseMapper.updateById(userRoleLink) > 0;
    }

    @Override
    public boolean removeUserFromRole(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public TablePage<UserLinkVO> listUserLinkByRoleId(String roleId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        QueryWrapper<UserRoleLink> queryWrapper = Wrappers.query();
        queryWrapper.eq("tsu.is_deleted", 0)
                .eq("turl.role_id", roleId)
                .like(StringUtil.hasText(userLinkInfoDTO.getUsername()), "tsu.username", userLinkInfoDTO.getUsername())
                .like(StringUtil.hasText(userLinkInfoDTO.getNickname()), "tsu.nickname", userLinkInfoDTO.getNickname());
        IPage<UserLinkVO> userLinkVOIPage = baseMapper.listUserLinkByRoleId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userLinkVOIPage);
    }

    @Override
    public List<RoleLinkVO> listRoleLinkByUserId(String appId, String userId) {
        QueryWrapper<SysRole> wrapper = Wrappers.query();
        wrapper.eq("turl.is_deleted", ColumnConstant.NON_DELETED)
                .eq("tsr.app_id", appId)
                .eq("turl.user_id", userId);
        return baseMapper.listRoleLinkByUserId(wrapper);
    }

    @Override
    public List<RoleBindSelectVO> listWithDisabledByUserId(String appId, String userId) {
        return baseMapper.selectWithDisabledByUserId(appId, userId);
    }

    @Override
    public List<UserBindSelectVO> listWithDisabledByRoleId(String roleId) {
        return baseMapper.listWithDisabledByRoleId(roleId);
    }
}




