package top.teek.uac.system.service.link.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.core.constant.CommonConstant;
import top.teek.uac.system.mapper.RolePostLinkMapper;
import top.teek.uac.system.model.dto.RolePostLinkDTO;
import top.teek.uac.system.model.dto.SysPostDTO;
import top.teek.uac.system.model.dto.SysRoleDTO;
import top.teek.uac.system.model.dto.link.PostLinkRoleListDTO;
import top.teek.uac.system.model.dto.link.RoleLinkPostListDTO;
import top.teek.uac.system.model.po.RolePostLink;
import top.teek.uac.system.model.po.SysPost;
import top.teek.uac.system.model.po.SysRole;
import top.teek.uac.system.model.vo.link.PostLinkVO;
import top.teek.uac.system.model.vo.link.RoleBindSelectVO;
import top.teek.uac.system.model.vo.link.RoleLinkVO;
import top.teek.uac.system.service.link.RolePostLinkService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Teeker
 * @date 2025/12/18 21:21:56
 * @since 1.0.0
 */
@Service
public class RolePostLinkServiceImpl extends ServiceImpl<RolePostLinkMapper, RolePostLink> implements RolePostLinkService {
    // ------- 岗位关联角色相关 API（以岗位为主）-------

    @Override
    public TablePage<RoleLinkVO> listRoleLinkByPostId(String appId, String postId, SysRoleDTO sysRoleDTO, PageQuery pageQuery) {
        QueryWrapper<SysRole> queryWrapper = Wrappers.query();
        
        queryWrapper.eq("tsr.is_deleted", 0)
                .eq("trpl.app_id", appId)
                .eq("trpl.post_id", postId)
                .like(StringUtil.hasText(sysRoleDTO.getRoleCode()), "tsr.role_code", sysRoleDTO.getRoleCode())
                .like(StringUtil.hasText(sysRoleDTO.getRoleName()), "tsr.role_name", sysRoleDTO.getRoleName());

        IPage<RoleLinkVO> postLinkRoleVOIPage = baseMapper.listRoleLinkByPostId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(postLinkRoleVOIPage);
    }

    @Override
    public List<RoleBindSelectVO> listWithSelectedByPostId(String appId, String postId) {
        return baseMapper.listWithSelectedByPostId(appId, postId);
    }

    @Override
    public Boolean addRoleListToPost(PostLinkRoleListDTO postLinkRoleListDTO) {
        List<String> roleIds = postLinkRoleListDTO.getRoleIds();

        List<RolePostLink> rolePostLinkList = ListUtil.newArrayList(roleIds, roleId ->
                        new RolePostLink().setRoleId(roleId)
                                .setPostId(postLinkRoleListDTO.getPostId())
                                .setValidFrom(Optional.ofNullable(postLinkRoleListDTO.getValidFrom()).orElse(LocalDate.now()))
                                .setExpireOn(Optional.ofNullable(postLinkRoleListDTO.getExpireOn()).orElse(LocalDate.now().plusYears(CommonConstant.expireOnNum)))
                                .setAppId(postLinkRoleListDTO.getAppId())
                , RolePostLink.class);

        return Db.saveBatch(rolePostLinkList);
    }

    @Override
    public boolean checkRoleListExistPost(PostLinkRoleListDTO postLinkRoleListDTO) {
        return baseMapper.exists(Wrappers.<RolePostLink>lambdaQuery()
                .in(RolePostLink::getRoleId, postLinkRoleListDTO.getRoleIds())
                .eq(RolePostLink::getPostId, postLinkRoleListDTO.getPostId())
                .eq(StringUtil.hasText(postLinkRoleListDTO.getAppId()), RolePostLink::getAppId, postLinkRoleListDTO.getAppId()));
    }

    // ------- 角色关联岗位相关 API（以角色为主） -------

    @Override
    public TablePage<PostLinkVO> listPostListByRoleId(String appId, String roleId, SysPostDTO sysPostDTO, PageQuery pageQuery) {
        QueryWrapper<SysPost> queryWrapper = Wrappers.query();

        queryWrapper.eq("trpl.is_deleted", 0)
                .eq("trpl.app_id", appId)
                .eq("trpl.role_id", roleId)
                .eq(StringUtil.hasText(sysPostDTO.getPostCode()), "tsp.post_code", sysPostDTO.getPostCode())
                .like(StringUtil.hasText(sysPostDTO.getPostName()), "tsp.group_name", sysPostDTO.getPostName());
        IPage<PostLinkVO> rolePostLinkVOIPage = baseMapper.listPostListByRoleId(pageQuery.buildPage(), queryWrapper);

        return TablePage.build(rolePostLinkVOIPage);
    }

    @Override
    public Boolean addPostListToRole(RoleLinkPostListDTO roleLinkPostListDTO, boolean removeLink) {
        if (removeLink) {
            // 删除角色与岗位关联
            baseMapper.delete(Wrappers.<RolePostLink>lambdaQuery()
                    .eq(RolePostLink::getRoleId, roleLinkPostListDTO.getRoleId()));
        }
        List<String> postIds = roleLinkPostListDTO.getPostIds();

        List<RolePostLink> rolePostLinkList = ListUtil.newArrayList(postIds, postId ->
                        new RolePostLink().setPostId(postId)
                                .setRoleId(roleLinkPostListDTO.getRoleId())
                                .setValidFrom(Optional.ofNullable(roleLinkPostListDTO.getValidFrom()).orElse(LocalDate.now()))
                                .setExpireOn(Optional.ofNullable(roleLinkPostListDTO.getExpireOn()).orElse(LocalDate.now().plusYears(CommonConstant.expireOnNum)))
                                .setAppId(roleLinkPostListDTO.getAppId())
                , RolePostLink.class);

        return Db.saveBatch(rolePostLinkList);
    }

    // ------- 公共 API -------

    @Override
    public Boolean editRolePostLink(RolePostLinkDTO rolePostLinkDTO) {
        RolePostLink rolePostLink = MapstructUtil.convert(rolePostLinkDTO, RolePostLink.class);
        if (Objects.isNull(rolePostLink.getValidFrom())) {
            // 默认为当前时间
            rolePostLink.setValidFrom(LocalDate.now());
        }
        if (Objects.isNull(rolePostLink.getExpireOn())) {
            // 默认为 3 年
            rolePostLink.setExpireOn(LocalDate.now().plusYears(CommonConstant.expireOnNum));
        }
        return baseMapper.updateById(rolePostLink) > 0;
    }

    @Override
    public boolean removeRolePostLink(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }
}
