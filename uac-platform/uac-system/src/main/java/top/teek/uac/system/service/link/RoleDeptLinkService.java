package top.teek.uac.system.service.link;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.model.dto.RoleDeptLinkDTO;
import top.teek.uac.system.model.dto.SysDeptDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.link.DeptLinkRoleListDTO;
import top.teek.uac.system.model.dto.link.RoleLinkDeptListDTO;
import top.teek.uac.system.model.po.RoleDeptLink;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_role_dept_link(角色关联部门表)】的数据库操作Service
 */
public interface RoleDeptLinkService extends IService<RoleDeptLink> {

    // ------- 部门关联角色相关 API（以部门为主）-------

    /**
     * 通过部门 ID 查询角色列表（分页）
     *
     * @param appId  应用 ID
     * @param deptId 部门 ID
     * @return 角色列表
     */
    TablePage<RoleLinkVO> listRoleLinkByDeptId(String appId, String deptId, SysRoleDTO sysRoleDTO, PageQuery pageQuery);

    /**
     * 查询某个角色绑定的部门列表
     *
     * @param appId  应用 ID
     * @param deptId 部门 ID
     * @return 部门列表
     */
    List<RoleBindSelectVO> listWithSelectedByDeptId(String appId, String deptId);

    /**
     * 添加角色到部门
     *
     * @param deptLinkRoleListDTO 部门关联角色信息
     * @return 是否添加成功部门关联角色信息
     */
    Boolean addRoleListToDept(DeptLinkRoleListDTO deptLinkRoleListDTO);

    /**
     * 检查角色是否已存在部门
     *
     * @return 是否存在
     */
    boolean checkRoleListExistDept(DeptLinkRoleListDTO deptLinkRoleListDTO);

    // ------- 角色关联部门相关 API（以角色为主） -------

    /**
     * 根据角色 ID 获取部门列表
     *
     * @param appId  应用 ID
     * @param roleId 角色 ID
     * @return 部门列表
     */
    List<Tree<String>> listDeptListByRoleId(String appId, String roleId, SysDeptDTO sysDeptDTO);

    /**
     * 根据角色 ID 查询部门 ID 列表
     *
     * @param appId  应用 ID
     * @param roleId 角色 ID
     * @return 部门 ID 列表
     */
    List<String> listDeptIdsByRoleId(String appId, String roleId);

    /**
     * 添加部门到角色
     *
     * @param roleLinkDeptListDTO 角色关联部门信息
     * @param removeLink          是否移除角色关联的部门
     * @return 是否添加成功角色关联部门信息
     */
    Boolean addDeptListToRole(RoleLinkDeptListDTO roleLinkDeptListDTO, boolean removeLink);

    // ------- 公共 API -------

    /**
     * 修改部门和角色的关联信息
     *
     * @param roleDeptLinkDTO 部门和角色信息
     * @return 是否修改成功
     */
    Boolean editRoleDeptLink(RoleDeptLinkDTO roleDeptLinkDTO);

    /**
     * 将部门移出角色
     *
     * @param ids 部门与角色关联 id 集合
     * @return 是否成功
     */
    boolean removeRoleDeptLink(List<Long> ids);

}
