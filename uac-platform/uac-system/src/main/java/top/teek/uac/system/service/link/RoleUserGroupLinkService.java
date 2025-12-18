package top.teek.uac.system.service.link;

import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.model.dto.RoleUserGroupLinkDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.SysUserGroupDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserGroupListDTO;
import top.teek.uac.system.model.dto.link.UserGroupLinkRoleListDTO;
import top.teek.uac.system.model.po.RoleUserGroupLink;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_user_group_link(用户组关联角色表)】的数据库操作Service
 */
public interface RoleUserGroupLinkService extends IService<RoleUserGroupLink> {
    // ------- 用户组关联角色相关 API（以用户组为主）-------

    /**
     * 通过用户组 ID 查询角色列表（分页）
     *
     * @param appId 应用 ID
     * @param userGroupId 用户组 ID
     * @return 角色列表
     */
    TablePage<RoleLinkVO> listRoleLinkByGroupId(String appId, String userGroupId, SysRoleDTO sysRoleDTO, PageQuery pageQuery);

    /**
     * 查询角色列表（已选的被禁用）
     *
     * @param appId 应用 ID
     * @param userGroupId 用户组 ID
     * @return 角色列表
     */
    List<RoleBindSelectVO> listWithSelectedByGroupId(String appId, String userGroupId);

    /**
     * 添加角色到用户组（多个角色）
     *
     * @param userGroupLinkRoleListDTO 用户组角色关联实体
     * @return 是否成功
     */
    boolean addRoleListToUserGroup(UserGroupLinkRoleListDTO userGroupLinkRoleListDTO);

    /**
     * 检查角色是否已存在用户组
     *
     * @return 是否存在
     */
    boolean checkRoleListExistUserGroup(UserGroupLinkRoleListDTO userGroupLinkRoleListDTO);

    // ------- 角色关联用户组相关 API（以角色为主）-------

    /**
     * 查询某个角色绑定的用户组列表
     *
     * @param appId 应用 ID
     * @param roleId 角色 ID
     * @return 用户组列表
     */
    TablePage<UserGroupLinkVO> listUserGroupByRoleId(String appId, String roleId, SysUserGroupDTO sysUserGroupDTO, PageQuery pageQuery);

    /**
     * 查询所有用户组列表，如果用户组绑定角色，则 disabled 属性为 true
     *
     * @param appId 应用 ID
     * @param roleId 角色 ID
     * @return 用户组列表
     */
    List<UserGroupBindSelectVO> listWithSelectedByRoleId(String appId, String roleId);

    /**
     * 检查用户组是否已存在角色
     *
     * @return 是否存在
     */
    boolean checkUserGroupListExistRole(RoleLinkUserGroupListDTO roleLinkUserGroupListDTO);

    /**
     * 添加用户到用户组（多个用户组）
     *
     * @param roleLinkUserGroupListDTO 角色用户组关联实体
     * @return 是否成功
     */
    boolean addUserGroupListToRole(RoleLinkUserGroupListDTO roleLinkUserGroupListDTO);

    // ------- 公共 API -------

    /**
     * 修改用户组和角色的关联信息
     *
     * @param roleUserGroupLinkDTO 用户组和角色信息
     * @return 是否修改成功
     */
    Boolean editRoleUserGroupLink(RoleUserGroupLinkDTO roleUserGroupLinkDTO);

    /**
     * 将用户组移出角色
     *
     * @param ids 用户组个角色关联 id 集合
     * @return 是否成功
     */
    boolean removeRoleUserGroupLink(List<Long> ids);
}
