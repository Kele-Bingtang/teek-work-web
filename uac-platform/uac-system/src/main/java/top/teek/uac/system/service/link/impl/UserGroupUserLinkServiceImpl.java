package top.teek.uac.system.service.link.impl;

import top.teek.core.constants.ColumnConstant;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.mapper.UserGroupUserLinkMapper;
import top.teek.uac.system.model.dto.UserGroupUserLinkDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkUsersDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkUserGroupsDTO;
import top.teek.uac.system.model.po.SysUserGroup;
import top.teek.uac.system.model.po.UserGroupUserLink;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkUserVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.UserGroupUserLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_user_group_link(用户关联用户组表)】的数据库操作Service实现
 */
@Service
@RequiredArgsConstructor
public class UserGroupUserLinkServiceImpl extends ServiceImpl<UserGroupUserLinkMapper, UserGroupUserLink> implements UserGroupUserLinkService {

    @Override
    public boolean checkUserExistUserGroups(UserLinkUserGroupsDTO userLinkUserGroupsDTO) {
        return baseMapper.exists(Wrappers.<UserGroupUserLink>lambdaQuery()
                .eq(UserGroupUserLink::getUserId, userLinkUserGroupsDTO.getUserId())
                .in(UserGroupUserLink::getUserGroupId, userLinkUserGroupsDTO.getUserGroupIds()));
    }

    @Override
    public boolean checkUsersExistUserGroup(UserGroupLinkUsersDTO userGroupLinkUsersDTO) {
        return baseMapper.exists(Wrappers.<UserGroupUserLink>lambdaQuery()
                .in(UserGroupUserLink::getUserId, userGroupLinkUsersDTO.getUserIds())
                .eq(UserGroupUserLink::getUserGroupId, userGroupLinkUsersDTO.getUserGroupId())
        );
    }

    @Override
    public boolean addUserGroupsToUser(UserLinkUserGroupsDTO userLinkUserGroupsDTO) {
        List<String> userGroupIds = userLinkUserGroupsDTO.getUserGroupIds();

        List<UserGroupUserLink> userGroupUserLinkList = ListUtil.newArrayList(userGroupIds, userGroupId ->
                        new UserGroupUserLink().setUserGroupId(userGroupId)
                                .setUserId(userLinkUserGroupsDTO.getUserId())
                                .setValidFrom(userLinkUserGroupsDTO.getValidFrom())
                                .setExpireOn(userLinkUserGroupsDTO.getExpireOn())
                , UserGroupUserLink.class);

        return Db.saveBatch(userGroupUserLinkList);
    }

    @Override
    public boolean addUsersToUserGroup(UserGroupLinkUsersDTO userGroupLinkUsersDTO) {
        List<String> userIds = userGroupLinkUsersDTO.getUserIds();

        List<UserGroupUserLink> userGroupUserLinkList = ListUtil.newArrayList(userIds, userId ->
                        new UserGroupUserLink().setUserId(userId)
                                .setUserGroupId(userGroupLinkUsersDTO.getUserGroupId())
                                .setValidFrom(userGroupLinkUsersDTO.getValidFrom())
                                .setExpireOn(userGroupLinkUsersDTO.getExpireOn())
                , UserGroupUserLink.class);

        return Db.saveBatch(userGroupUserLinkList);
    }

    @Override
    public boolean removeUserFromUserGroup(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public List<UserGroupLinkUserVO> listUserGroupByUserId(String userId) {
        QueryWrapper<SysUserGroup> wrapper = Wrappers.query();
        wrapper.eq("tugl.is_deleted", ColumnConstant.NON_DELETED)
                .eq("tugl.user_id", userId);

        return baseMapper.listUserGroupByUserId(wrapper);
    }

    @Override
    public TablePage<UserLinkVO> listUserLinkByGroupId(String userGroupId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        QueryWrapper<UserGroupUserLink> queryWrapper = Wrappers.query();
        queryWrapper.eq("tsu.is_deleted", 0)
                .eq("tugl.user_group_id", userGroupId)
                .like(StringUtil.hasText(userLinkInfoDTO.getUsername()), "tsu.username", userLinkInfoDTO.getUsername())
                .like(StringUtil.hasText(userLinkInfoDTO.getNickname()), "tsu.nickname", userLinkInfoDTO.getNickname());
        IPage<UserLinkVO> userLinkVOIPage = baseMapper.listUserLinkByGroupId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userLinkVOIPage);
    }

    @Override
    public List<UserGroupBindSelectVO> listWithDisabledByUserId(String appId, String userId) {
        return baseMapper.selectWithDisabledByUserId(appId, userId);
    }

    @Override
    public List<UserBindSelectVO> listWithDisabledByGroupId(String userGroupId) {
        return baseMapper.listWithDisabledByGroupId(userGroupId);
    }


    @Override
    public boolean updateOne(UserGroupUserLinkDTO userGroupUserLinkDTO) {
        UserGroupUserLink userGroupUserLink = MapstructUtil.convert(userGroupUserLinkDTO, UserGroupUserLink.class);
        return baseMapper.updateById(userGroupUserLink) > 0;
    }
}




