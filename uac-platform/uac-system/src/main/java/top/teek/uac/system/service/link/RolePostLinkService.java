package top.teek.uac.system.service.link;

import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.model.dto.RolePostLinkDTO;
import top.teek.uac.system.model.dto.SysPostDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.link.PostLinkRoleListDTO;
import top.teek.uac.system.model.dto.link.RoleLinkPostListDTO;
import top.teek.uac.system.model.po.RolePostLink;
import top.teek.uac.system.model.vo.link.PostLinkVO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/12/18 21:21:35
 * @since 1.0.0
 */
public interface RolePostLinkService extends IService<RolePostLink> {

    // ------- 岗位关联角色相关 API（以岗位为主）-------

    /**
     * 通过岗位 ID 查询角色列表（分页）
     *
     * @param appId  应用 ID
     * @param postId 岗位 ID
     * @return 角色列表
     */
    TablePage<RoleLinkVO> listRoleLinkByPostId(String appId, String postId, SysRoleDTO sysPostDTO, PageQuery pageQuery);

    /**
     * 查询某个角色绑定的岗位列表
     *
     * @param appId  应用 ID
     * @param postId 岗位 ID
     * @return 岗位列表
     */
    List<RoleBindSelectVO> listWithSelectedByPostId(String appId, String postId);

    /**
     * 添加角色到岗位
     *
     * @param postLinkRoleListDTO 岗位关联角色信息
     * @return 是否添加成功岗位关联角色信息
     */
    Boolean addRoleListToPost(PostLinkRoleListDTO postLinkRoleListDTO);

    /**
     * 检查角色是否已存在岗位
     *
     * @return 是否存在
     */
    boolean checkRoleListExistPost(PostLinkRoleListDTO postLinkRoleListDTO);

    // ------- 角色关联岗位相关 API（以角色为主） -------

    /**
     * 根据角色 ID 获取岗位列表
     *
     * @param appId      应用 ID
     * @param roleId     角色 ID
     * @param sysPostDTO 岗位信息
     * @return 岗位列表
     */
    TablePage<PostLinkVO> listPostListByRoleId(String appId, String roleId, SysPostDTO sysPostDTO, PageQuery pageQuery);

    /**
     * 添加岗位到角色
     *
     * @param roleLinkPostListDTO 角色关联岗位信息
     * @param removeLink          是否移除角色关联的岗位
     * @return 是否添加成功角色关联岗位信息
     */
    Boolean addPostListToRole(RoleLinkPostListDTO roleLinkPostListDTO, boolean removeLink);

    // ------- 公共 API -------

    /**
     * 修改岗位和角色的关联信息
     *
     * @param rolePostLinkDTO 岗位和角色信息
     * @return 是否修改成功
     */
    Boolean editRolePostLink(RolePostLinkDTO rolePostLinkDTO);

    /**
     * 将岗位移出角色
     *
     * @param ids 岗位与角色关联 id 集合
     * @return 是否成功
     */
    boolean removeRolePostLink(List<Long> ids);

}
