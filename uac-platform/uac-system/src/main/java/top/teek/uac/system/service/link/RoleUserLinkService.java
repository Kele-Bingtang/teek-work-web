package top.teek.uac.system.service.link;

import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.model.dto.RoleUserLinkDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.SysUserDTO;
import top.teek.uac.system.model.dto.link.RoleLinkUserListDTO;
import top.teek.uac.system.model.dto.link.UserLinkRoleListDTO;
import top.teek.uac.system.model.po.RoleUserLink;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_user_link(用户关联角色表)】的数据库操作Service
 */
public interface RoleUserLinkService extends IService<RoleUserLink> {

    // ------- 用户关联角色相关 API（以用户为主）-------

    /**
     * 根据应用 ID、用户 ID 查询角色列表
     *
     * @param appId      应用 ID
     * @param userId     用户 ID
     * @param sysRoleDTO 角色信息
     * @return 角色列表
     */
    TablePage<RoleLinkVO> listRoleLinkByUserId(String appId, String userId, SysRoleDTO sysRoleDTO, PageQuery pageQuery);

    /**
     * 根据应用 ID、用户 ID 查询角色列表，如果角色绑定了用户，则 disabled 属性为 false
     *
     * @param appId  应用 ID
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<RoleBindSelectVO> listWithSelectedByUserId(String appId, String userId);

    /**
     * 添加用户到角色
     *
     * @param userLinkRoleListDTO 用户绑定角色信息
     * @return 是否成功
     */
    boolean addRoleListToUser(UserLinkRoleListDTO userLinkRoleListDTO);

    /**
     * 检查角色是否已存在用户
     *
     * @return 是否存在
     */
    boolean checkRoleListExistUser(UserLinkRoleListDTO userLinkRoleListDTO);

    // ------- 角色关联用户相关 API（以角色为主）-------

    /**
     * 通过角色 ID 查询用户列表
     *
     * @param appId  应用 ID
     * @param roleId 角色 ID
     * @return 用户列表
     */
    TablePage<UserLinkVO> listUserLinkByRoleId(String appId, String roleId, SysUserDTO sysUserDTO, PageQuery pageQuery);

    /**
     * 下拉查询用户列表，如果用户绑定了角色，则 disabled 属性为 true
     *
     * @param @param appId  应用 ID
     * @param roleId 角色 ID
     * @return 用户列表
     */
    List<UserBindSelectVO> listWithSelectedByRoleId(String appId, String roleId);

    /**
     * 添加用户到角色
     *
     * @param roleLinkUserListDTO 用户绑定角色信息
     * @return 是否成功
     */
    boolean addUserListToRole(RoleLinkUserListDTO roleLinkUserListDTO);

    /**
     * 检查用户是否已存在角色
     *
     * @return 是否存在
     */
    boolean checkUserListExistRole(RoleLinkUserListDTO roleLinkUserListDTO);

    // ------- 公共 API -------

    /**
     * 更新记录
     *
     * @param roleUserLinkDTO 记录
     * @return 是否成功
     */
    boolean editRoleUserLink(RoleUserLinkDTO roleUserLinkDTO);

    /**
     * 将用户移出角色
     *
     * @param ids 关联 ID
     * @return 是否成功
     */
    boolean removeRoleUserLink(List<Long> ids);

}
