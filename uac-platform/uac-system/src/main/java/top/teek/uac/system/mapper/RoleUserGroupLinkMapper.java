package top.teek.uac.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.teek.uac.system.model.po.RoleUserGroupLink;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.po.SysUserGroup;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-26-12 02:26:00
 * @note 针对表 t_role_user_group_link(用户组关联角色表)的数据库操作 Mapper
 */
public interface RoleUserGroupLinkMapper extends BaseMapper<RoleUserGroupLink> {

    // ------- 用户组关联角色相关 API（以用户组为主）-------

    IPage<RoleLinkVO> listRoleLinkByGroupId(@Param("page") Page<RoleLinkVO> page, @Param(Constants.WRAPPER) Wrapper<SysRole> queryWrapper);

    List<RoleBindSelectVO> listWithSelectedByGroupId(@Param("appId") String appId, @Param("userGroupId") String userGroupId);

    // ------- 角色关联用户组相关 API（以角色为主）-------

    IPage<UserGroupLinkVO> listUserGroupByRoleId(@Param("page") Page<UserGroupLinkVO> page, @Param(Constants.WRAPPER) Wrapper<SysUserGroup> queryWrapper);

    List<UserGroupBindSelectVO> listWithSelectedByRoleId(@Param("appId") String appId, @Param("roleId") String roleId);
}




