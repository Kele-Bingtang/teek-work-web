package top.teek.uac.system.service.link;

import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.dto.UserGroupUserLinkDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkUsersDTO;
import top.teek.uac.system.model.dto.link.UserLinkInfoDTO;
import top.teek.uac.system.model.dto.link.UserLinkUserGroupsDTO;
import top.teek.uac.system.model.po.UserGroupUserLink;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_user_group_user_link(用户关联用户组表)】的数据库操作Service
 */
public interface UserGroupUserLinkService extends IService<UserGroupUserLink> {

    /**
     * 检查用户是否在某些用户组中（多个用户组）
     *
     * @return 是否在用户组中
     */
    boolean checkUserExistUserGroups(UserLinkUserGroupsDTO userLinkUserGroupsDTO);

    /**
     * 检查用户是否在某些用户组中（多个用户）
     *
     * @return 是否在用户组中
     */
    boolean checkUsersExistUserGroup(UserGroupLinkUsersDTO userGroupLinkUsersDTO);

    /**
     * 添加用户到用户组（多个用户组）
     *
     * @param userLinkUserGroupsDTO 用户和用户组数据
     * @return 是否添加成功
     */
    boolean addUserGroupsToUser(UserLinkUserGroupsDTO userLinkUserGroupsDTO);

    /**
     * 添加用户到用户组（多个用户）
     *
     * @param userGroupLinkUsersDTO 用户和用户组数据
     * @return 是否添加成功
     */
    boolean addUsersToUserGroup(UserGroupLinkUsersDTO userGroupLinkUsersDTO);

    /**
     * 将用户移出项目组
     *
     * @param ids 关联 ID
     * @return 是否移出成功
     */
    boolean removeUserFromUserGroup(List<Long> ids);

    /**
     * 通过用户 ID 查询用户组列表
     *
     * @param userId 用户 ID
     * @return 用户组列表
     */
    List<UserGroupLinkVO> listUserGroupByUserId(String userId, SysUserGroupDTO sysUserGroupDTO);

    /**
     * 查询用户组下的用户列表
     *
     * @param userGroupId 用户组 ID
     * @return 用户列表
     */
    TablePage<UserLinkVO> listUserLinkByGroupId(String userGroupId, UserLinkInfoDTO userLinkInfoDTO, PageQuery pageQuery);

    /**
     * 下拉查询用户列表，如果用户组绑定了用户，则 disabled 属性为 true
     *
     * @param userId 用户 ID
     * @return 用户组列表
     */
    List<UserGroupBindSelectVO> listWithSelectedByUserId(String userId);

    /**
     * 下拉查询用户列表，如果用户绑定了用户组，则 disabled 属性为 true
     *
     * @param userGroupId 用户组 ID
     * @return 用户列表
     */
    List<UserBindSelectVO> listWithSelectedByGroupId(String userGroupId);

    /**
     * 修改用户组和用户的关联信息
     *
     * @param userGroupUserLinkDTO 用户组信息
     * @return 是否修改成功
     */
    boolean updateOne(UserGroupUserLinkDTO userGroupUserLinkDTO);

}
