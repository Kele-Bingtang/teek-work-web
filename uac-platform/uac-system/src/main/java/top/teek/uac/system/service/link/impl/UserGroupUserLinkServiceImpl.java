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
import top.teek.uac.core.constant.CommonConstant;
import top.teek.uac.system.mapper.UserGroupUserLinkMapper;
import top.teek.uac.system.model.dto.SysUserDTO;
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.dto.UserGroupUserLinkDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkUserListDTO;
import top.teek.uac.system.model.dto.link.UserLinkUserGroupListDTO;
import top.teek.uac.system.model.po.SysUserGroup;
import top.teek.uac.system.model.po.UserGroupUserLink;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import top.teek.uac.system.service.link.UserGroupUserLinkService;
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
 * @note 针对表【t_user_group_user_link(用户关联用户组表)】的数据库操作Service实现
 */
@Service
@RequiredArgsConstructor
public class UserGroupUserLinkServiceImpl extends ServiceImpl<UserGroupUserLinkMapper, UserGroupUserLink> implements UserGroupUserLinkService {

    // ------- 用户组关联用户相关 API（以用户组为主）-------

    @Override
    public TablePage<UserLinkVO> listUserLinkByGroupId(String userGroupId, SysUserDTO sysUserDTO, PageQuery pageQuery) {
        QueryWrapper<UserGroupUserLink> queryWrapper = Wrappers.query();
        queryWrapper.eq("tsu.is_deleted", 0)
                .eq("tugul.user_group_id", userGroupId)
                .like(StringUtil.hasText(sysUserDTO.getUsername()), "tsu.username", sysUserDTO.getUsername())
                .like(StringUtil.hasText(sysUserDTO.getNickname()), "tsu.nickname", sysUserDTO.getNickname());
        IPage<UserLinkVO> userLinkVOIPage = baseMapper.listUserLinkByGroupId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(userLinkVOIPage);
    }

    @Override
    public List<UserBindSelectVO> listWithSelectedByGroupId(String userGroupId) {
        return baseMapper.listWithSelectedByGroupId(userGroupId);
    }

    @Override
    public boolean addUserGroupListToUser(UserLinkUserGroupListDTO userLinkUserGroupListDTO) {
        List<String> userGroupIds = userLinkUserGroupListDTO.getUserGroupIds();

        List<UserGroupUserLink> userGroupUserLinkList = ListUtil.newArrayList(userGroupIds, userGroupId ->
                        new UserGroupUserLink().setUserGroupId(userGroupId)
                                .setUserId(userLinkUserGroupListDTO.getUserId())
                                .setValidFrom(Optional.ofNullable(userLinkUserGroupListDTO.getValidFrom()).orElse(LocalDate.now()))
                                .setExpireOn(Optional.ofNullable(userLinkUserGroupListDTO.getExpireOn()).orElse(LocalDate.now().plusYears(CommonConstant.expireOnNum)))
                , UserGroupUserLink.class);

        return Db.saveBatch(userGroupUserLinkList);
    }

    @Override
    public boolean checkUserGroupListExistUser(UserLinkUserGroupListDTO userLinkUserGroupListDTO) {
        return baseMapper.exists(Wrappers.<UserGroupUserLink>lambdaQuery()
                .eq(UserGroupUserLink::getUserId, userLinkUserGroupListDTO.getUserId())
                .in(UserGroupUserLink::getUserGroupId, userLinkUserGroupListDTO.getUserGroupIds()));
    }

    // ------- 用户关联用户组相关 API（以用户为主）-------

    @Override
    public List<UserGroupLinkVO> listUserGroupByUserId(String userId, SysUserGroupDTO sysUserGroupDTO) {
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
    public List<UserGroupBindSelectVO> listWithSelectedByUserId(String userId) {
        return baseMapper.selectWithSelectedByUserId(userId);
    }


    @Override
    public boolean addUserListToGroup(UserGroupLinkUserListDTO userGroupLinkUserListDTO) {
        List<String> userIds = userGroupLinkUserListDTO.getUserIds();

        List<UserGroupUserLink> userGroupUserLinkList = ListUtil.newArrayList(userIds, userId ->
                        new UserGroupUserLink().setUserId(userId)
                                .setUserGroupId(userGroupLinkUserListDTO.getUserGroupId())
                                .setValidFrom(Optional.ofNullable(userGroupLinkUserListDTO.getValidFrom()).orElse(LocalDate.now()))
                                .setExpireOn(Optional.ofNullable(userGroupLinkUserListDTO.getExpireOn()).orElse(LocalDate.now().plusYears(CommonConstant.expireOnNum)))
                , UserGroupUserLink.class);

        return Db.saveBatch(userGroupUserLinkList);
    }

    @Override
    public boolean checkUserListExistUserGroup(UserGroupLinkUserListDTO userGroupLinkUserListDTO) {
        return baseMapper.exists(Wrappers.<UserGroupUserLink>lambdaQuery()
                .in(UserGroupUserLink::getUserId, userGroupLinkUserListDTO.getUserIds())
                .eq(UserGroupUserLink::getUserGroupId, userGroupLinkUserListDTO.getUserGroupId())
        );
    }

    // ------- 公共 API -------

    @Override
    public boolean editUserGroupUserLink(UserGroupUserLinkDTO userGroupUserLinkDTO) {
        UserGroupUserLink userGroupUserLink = MapstructUtil.convert(userGroupUserLinkDTO, UserGroupUserLink.class);
        if (Objects.isNull(userGroupUserLink.getValidFrom())) {
            // 默认为当前时间
            userGroupUserLink.setValidFrom(LocalDate.now());
        }
        if (Objects.isNull(userGroupUserLink.getExpireOn())) {
            // 默认为 3 年
            userGroupUserLink.setExpireOn(LocalDate.now().plusYears(CommonConstant.expireOnNum));
        }
        return baseMapper.updateById(userGroupUserLink) > 0;
    }

    @Override
    public boolean removeUserGroupUserLink(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }
   
}




