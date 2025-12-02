package top.teek.uac.system.mapper;

import top.teek.uac.system.model.po.RoleUserLink;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-26-12 02:26:30
 * @note 针对表 t_role_user_link(用户关联角色表)的数据库操作 Mapper
 */
public interface RoleUserLinkMapper extends BaseMapper<RoleUserLink> {

    // ------- 用户关联角色相关 API（以用户为主）-------

    List<RoleLinkVO> listRoleLinkByUserId(@Param(Constants.WRAPPER) QueryWrapper<SysRole> wrapper);

    List<RoleBindSelectVO> selectWithDisabledByUserId(@Param("appId") String appId, @Param("userId") String userId);

    // ------- 角色关联用户相关 API（以角色为主）-------

    IPage<UserLinkVO> listUserLinkByRoleId(@Param("page") Page<RoleUserLink> page, @Param(Constants.WRAPPER) Wrapper<RoleUserLink> queryWrapper);

    List<UserBindSelectVO> listWithSelectedByRoleId(@Param("roleId") String roleId);
}




