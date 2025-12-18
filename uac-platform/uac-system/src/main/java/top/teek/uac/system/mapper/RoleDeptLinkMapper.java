package top.teek.uac.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.teek.uac.system.model.dto.SysDeptDTO;
import top.teek.uac.system.model.po.RoleDeptLink;
import top.teek.uac.system.model.po.SysDept;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.po.UserGroupUserLink;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-22-12 02:22:04
 * @note 针对表 t_role_dept_link(角色关联部门表)的数据库操作 Mapper
 */
public interface RoleDeptLinkMapper extends BaseMapper<RoleDeptLink> {

    // ------- 部门关联角色相关 API（以部门为主）-------

    IPage<RoleLinkVO> listRoleLinkByDeptId(@Param("page") Page<UserGroupUserLink> page, @Param(Constants.WRAPPER) Wrapper<SysRole> queryWrapper);

    List<RoleBindSelectVO> listWithSelectedByDeptId(@Param("appId") String appId, @Param("deptId") String deptId);

    // ------- 角色关联部门相关 API（以角色为主） -------
    
    List<SysDept> listDeptListByRoleId(@Param("appId") String appId, @Param("roleId") String roleId, @Param("sysDeptDTO") SysDeptDTO sysDeptDTO);

}




