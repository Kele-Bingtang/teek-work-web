package top.teek.uac.system.service.link.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.teek.core.constants.ColumnConstant;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.mapper.UserGroupUserLinkMapper;
import top.teek.uac.system.model.dto.SysUserGroupDTO;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_user_group_user_link(用户关联用户组表)】的数据库操作Service实现
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
    public List<UserGroupLinkUserVO> listUserGroupByUserId(String userId, SysUserGroupDTO sysUserGroupDTO) {
        QueryWrapper<SysUserGroup> wrapper = Wrappers.query();
        wrapper.eq("tugul.is_deleted", ColumnConstant.NON_DELETED)
                .eq("tugul.user_id", userId)
                .eq(StringUtil.hasText(sysUserGroupDTO.getGroupName()), "tsug.group_id", sysUserGroupDTO.getGroupName())
                .like(StringUtil.hasText(sysUserGroupDTO.getGroupId()), "tsug.group_id", sysUserGroupDTO.getGroupId())
                .eq(StringUtil.hasText(sysUserGroupDTO.getGroupType()), "tsug.group_type", sysUserGroupDTO.getGroupType())
                .eq(Objects.nonNull(sysUserGroupDTO.getStatus()), "tugul.status", sysUserGroupDTO.getStatus())
                .orderByAsc("tugul.create_time");
        ;

        return baseMapper.listUserGroupByUserId(wrapper);
    }

    @Override
    public TablePage<UserLinkVO> listUserLinkByGroupId(String userGroupId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery) {
        QueryWrapper<UserGroupUserLink> queryWrapper = Wrappers.query();
        queryWrapper.eq("tsu.is_deleted", 0)
                .eq("tugul.user_group_id", userGroupId)
                .like(StringUtil.hasText(userLinkInfoDTO.getUsername()), "tsu.username", userLinkInfoDTO.getUsername())
                .like(StringUtil.hasText(userLinkInfoDTO.getNickname()), "tsu.nickname", userLinkInfoDTO.getNickname());
        IPage<UserLinkVO> userLinkVOIPage = baseMapper.listUserLinkByGroupId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userLinkVOIPage);
    }

    @Override
    public List<UserGroupBindSelectVO> listWithSelectedByUserId(String userId) {
        return baseMapper.selectWithSelectedByUserId(userId);
    }

    @Override
    public List<UserBindSelectVO> listWithSelectedByGroupId(String userGroupId) {
        return baseMapper.listWithSelectedByGroupId(userGroupId);
    }


    @Override
    public boolean updateOne(UserGroupUserLinkDTO userGroupUserLinkDTO) {
        UserGroupUserLink userGroupUserLink = MapstructUtil.convert(userGroupUserLinkDTO, UserGroupUserLink.class);
        if (Objects.isNull(userGroupUserLink.getValidFrom())) {
            // 默认为当前时间
            userGroupUserLink.setValidFrom(LocalDate.now());
        }
        if (Objects.isNull(userGroupUserLink.getExpireOn())) {
            // 默认为 3 年
            userGroupUserLink.setExpireOn(LocalDate.now().plusYears(3));
        }
        return baseMapper.updateById(userGroupUserLink) > 0;
    }
}




