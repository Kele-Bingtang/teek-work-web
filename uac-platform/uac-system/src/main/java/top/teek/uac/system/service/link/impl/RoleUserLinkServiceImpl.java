package top.teek.uac.system.service.link.impl;

import top.teek.core.constants.ColumnConstant;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.mapper.RoleUserLinkMapper;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.RoleUserLinkDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserListDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkRoleListDTO;
import top.teek.uac.system.model.po.RoleUserLink;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.RoleUserLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_user_link(用户关联角色表)】的数据库操作Service实现
 */
@Service
public class RoleUserLinkServiceImpl extends ServiceImpl<RoleUserLinkMapper, RoleUserLink> implements RoleUserLinkService {

    // ------- 用户关联角色相关 API（以用户为主）-------

    @Override
    public List<RoleLinkVO> listRoleLinkByUserId(String appId, String userId, SysRoleDTO sysRoleDTO) {
        QueryWrapper<SysRole> wrapper = Wrappers.query();
        wrapper.eq("trul.is_deleted", ColumnConstant.NON_DELETED)
                .eq("tsr.app_id", appId)
                .eq("trul.user_id", userId)
                .eq(StringUtil.hasText(sysRoleDTO.getRoleCode()), "tsr.role_code", sysRoleDTO.getRoleCode())
                .eq(StringUtil.hasText(sysRoleDTO.getRoleName()), "tsr.role_name", sysRoleDTO.getRoleName())
                .eq(Objects.nonNull(sysRoleDTO.getStatus()), "tsr.status", sysRoleDTO.getStatus())
                .orderByAsc("trul.create_time");
        return baseMapper.listRoleLinkByUserId(wrapper);
    }

    @Override
    public List<RoleBindSelectVO> listWithSelectedByUserId(String appId, String userId) {
        return baseMapper.selectWithDisabledByUserId(appId, userId);
    }

    @Override
    public boolean addRoleListToUser(UserLinkRoleListDTO userLinkRoleListDTO) {
        List<String> roleIds = userLinkRoleListDTO.getRoleIds();

        List<RoleUserLink> roleUserLinkList = ListUtil.newArrayList(roleIds, roleId ->
                        new RoleUserLink().setRoleId(roleId)
                                .setUserId(userLinkRoleListDTO.getUserId())
                                .setValidFrom(userLinkRoleListDTO.getValidFrom())
                                .setExpireOn(userLinkRoleListDTO.getExpireOn())
                                .setAppId(userLinkRoleListDTO.getAppId())
                , RoleUserLink.class);

        return Db.saveBatch(roleUserLinkList);
    }

    @Override
    public boolean checkRoleListExistUser(UserLinkRoleListDTO userLinkRoleListDTO) {
        return baseMapper.exists(Wrappers.<RoleUserLink>lambdaQuery()
                .eq(RoleUserLink::getUserId, userLinkRoleListDTO.getUserId())
                .in(RoleUserLink::getRoleId, userLinkRoleListDTO.getRoleIds())
                .eq(StringUtil.hasText(userLinkRoleListDTO.getAppId()), RoleUserLink::getAppId, userLinkRoleListDTO.getAppId()));
    }

    // ------- 角色关联用户相关 API（以角色为主）-------

    @Override
    public TablePage<UserLinkVO> listUserLinkByRoleId(String roleId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        QueryWrapper<RoleUserLink> queryWrapper = Wrappers.query();
        queryWrapper.eq("tsu.is_deleted", 0)
                .eq("trul.role_id", roleId)
                .like(StringUtil.hasText(userLinkInfoDTO.getUsername()), "tsu.username", userLinkInfoDTO.getUsername())
                .like(StringUtil.hasText(userLinkInfoDTO.getNickname()), "tsu.nickname", userLinkInfoDTO.getNickname());
        IPage<UserLinkVO> userLinkVOIPage = baseMapper.listUserLinkByRoleId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userLinkVOIPage);
    }


    @Override
    public List<UserBindSelectVO> listWithSelectedByRoleId(String roleId) {
        return baseMapper.listWithSelectedByRoleId(roleId);
    }

    @Override
    public boolean addUserListToRole(RoleLinkUserListDTO roleLinkUserListDTO) {
        List<String> userIds = roleLinkUserListDTO.getUserIds();

        List<RoleUserLink> roleUserLinkList = ListUtil.newArrayList(userIds, userId ->
                        new RoleUserLink().setUserId(userId)
                                .setRoleId(roleLinkUserListDTO.getRoleId())
                                .setValidFrom(roleLinkUserListDTO.getValidFrom())
                                .setExpireOn(roleLinkUserListDTO.getExpireOn())
                                .setAppId(roleLinkUserListDTO.getAppId())
                , RoleUserLink.class);

        return Db.saveBatch(roleUserLinkList);
    }

    @Override
    public boolean checkUserListExistRole(RoleLinkUserListDTO roleLinkUserListDTO) {
        return baseMapper.exists(Wrappers.<RoleUserLink>lambdaQuery()
                .eq(RoleUserLink::getRoleId, roleLinkUserListDTO.getRoleId())
                .in(RoleUserLink::getUserId, roleLinkUserListDTO.getUserIds())
                .eq(StringUtil.hasText(roleLinkUserListDTO.getAppId()), RoleUserLink::getAppId, roleLinkUserListDTO.getAppId()));
    }

    // ------- 公共 API -------

    @Override
    public boolean editRoleUserLink(RoleUserLinkDTO roleUserLinkDTO) {
        RoleUserLink roleUserLink = MapstructUtil.convert(roleUserLinkDTO, RoleUserLink.class);
        if (Objects.isNull(roleUserLink.getValidFrom())) {
            // 默认为当前时间
            roleUserLink.setValidFrom(LocalDate.now());
        }
        if (Objects.isNull(roleUserLink.getExpireOn())) {
            // 默认为 3 年
            roleUserLink.setExpireOn(LocalDate.now().plusYears(3));
        }
        return baseMapper.updateById(roleUserLink) > 0;
    }

    @Override
    public boolean removeRoleUserLink(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

}




