package top.teek.uac.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import top.teek.uac.system.model.po.RolePostLink;
import top.teek.uac.system.model.po.SysPost;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.vo.link.PostLinkVO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/12/18 21:29:29
 * @since 1.0.0
 */
public interface RolePostLinkMapper extends BaseMapper<RolePostLink> {
    // ------- 岗位关联角色相关 API（以岗位为主）-------

    IPage<RoleLinkVO> listRoleLinkByPostId(@Param("page") Page<RoleLinkVO> page, @Param(Constants.WRAPPER) Wrapper<SysRole> queryWrapper);

    List<RoleBindSelectVO> listWithSelectedByPostId(@Param("appId") String appId, @Param("postId") String postId);

    // ------- 角色关联岗位相关 API（以角色为主） -------

    IPage<PostLinkVO> listPostListByRoleId(@Param("page") Page<PostLinkVO> page, @Param(Constants.WRAPPER) Wrapper<SysPost> queryWrapper);
}
