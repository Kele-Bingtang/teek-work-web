package top.teek.uac.system.mapper;

import top.teek.uac.system.model.po.SysUserGroup;
import top.teek.uac.system.model.po.UserGroupUserLink;
import top.teek.uac.system.model.vo.link.UserBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupBindSelectVO;
import top.teek.uac.system.model.vo.link.UserGroupLinkVO;
import top.teek.uac.system.model.vo.link.UserLinkVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-25-12 02:25:39
 * @note 针对表 t_user_group_user_link(用户关联用户组表)的数据库操作 Mapper
 */
public interface UserGroupUserLinkMapper extends BaseMapper<UserGroupUserLink> {

    // ------- 用户组关联用户相关 API（以用户组为主）-------

    IPage<UserLinkVO> listUserLinkByGroupId(@Param("page") Page<UserGroupUserLink> page, @Param(Constants.WRAPPER) Wrapper<UserGroupUserLink> queryWrapper);

    List<UserBindSelectVO> listWithSelectedByGroupId(@Param("userGroupId") String userGroupId);

    // ------- 用户关联用户组相关 API（以用户为主）-------

    List<UserGroupLinkVO> listUserGroupByUserId(@Param(Constants.WRAPPER) Wrapper<SysUserGroup> queryWrapper);

    List<UserGroupBindSelectVO> selectWithSelectedByUserId(@Param("userId") String userId);
}




