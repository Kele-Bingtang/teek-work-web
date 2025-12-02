package top.teek.uac.system.service.system.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import top.teek.core.constants.ColumnConstant;
import top.teek.core.exception.ServiceException;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.security.domain.LoginUser;
import top.teek.uac.core.constant.TenantConstant;
import top.teek.uac.core.enums.ResourceType;
import top.teek.uac.core.helper.UacHelper;
import top.teek.uac.system.mapper.SysResourceMapper;
import top.teek.uac.system.model.dto.SysResourceDTO;
import top.teek.uac.system.model.po.RoleResourceLink;
import top.teek.uac.system.model.po.SysResource;
import top.teek.uac.system.model.vo.SysResourceVO;
import top.teek.uac.system.model.vo.router.Meta;
import top.teek.uac.system.model.vo.router.RouterVO;
import top.teek.uac.system.service.link.RoleResourceLinkService;
import top.teek.uac.system.service.system.SysResourceService;
import top.teek.utils.ListUtil;
import top.teek.utils.MapstructUtil;
import top.teek.utils.StringUtil;
import top.teek.utils.TreeBuildUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_sys_resource(资源表)】的数据库操作Service实现
 */
@Service
@RequiredArgsConstructor
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements SysResourceService {

    private final RoleResourceLinkService roleResourceLinkService;

    @Override
    public List<RouterVO> listRoutes(String appId) {
        // 查询用户拥有的资源列表
        LoginUser loginUser = UacHelper.getLoginUser();
        if (Objects.isNull(loginUser)) {
            throw new ServiceException("查询不到登录用户的信息");
        }

        List<SysResource> sysResourceList = baseMapper.listResourceListByUserId(appId, loginUser.getUserId(), false);
        List<SysResourceVO> sysResourceVOList = MapstructUtil.convert(sysResourceList, SysResourceVO.class);

        List<SysResourceVO> treeList = TreeBuildUtil.build(sysResourceVOList, "0", SysResourceVO::getResourceId);

        // 构建前端需要的路由列表
        return buildRoutes(treeList);

    }

    private List<RouterVO> buildRoutes(List<SysResourceVO> treeList) {
        List<RouterVO> routers = new ArrayList<>();
        for (SysResourceVO sysResourceVO : treeList) {
            // 按钮权限过滤掉
            if (ResourceType.FUNCTION.getValue().equals(sysResourceVO.getResourceType())) {
                continue;
            }
            RouterVO router = new RouterVO()
                    .setPath(sysResourceVO.getPath())
                    .setName(sysResourceVO.getResourceCode())
                    .setComponent(sysResourceVO.getComponent())
                    .setMeta(sysResourceVO.getMeta());

            List<SysResourceVO> childResources = sysResourceVO.getChildren();
            if (ListUtil.isNotEmpty(childResources)) {
                // 每个路由资源添加 Auths 按钮权限
                childResources.forEach(childResource -> {
                    if (ResourceType.FUNCTION.getValue().equals(childResource.getResourceType())) {
                        Meta meta = router.getMeta();
                        Set<String> auths = Optional.ofNullable(meta.getAuths()).orElse(new HashSet<>());
                        // 按钮权限可能是多个，所以需要拆分
                        String[] split = Optional.ofNullable(childResource.getPermission()).orElse("").split(",");
                        auths.addAll(Arrays.asList(split));
                        meta.setAuths(auths);
                        router.setMeta(meta);
                    }
                });
                router.setChildren(buildRoutes(childResources));
            }
            routers.add(router);
        }
        return routers;
    }

    @Override
    public List<SysResourceVO> listAll(SysResourceDTO sysResourceDTO) {
        LambdaQueryWrapper<SysResource> wrapper = buildQueryWrapper(sysResourceDTO);
        List<SysResource> sysResourceList = baseMapper.selectList(wrapper);

        return MapstructUtil.convert(sysResourceList, SysResourceVO.class);
    }

    @Override
    public TablePage<SysResourceVO> listPage(SysResourceDTO sysResourceDTO, PageQuery pageQuery) {
        LambdaQueryWrapper<SysResource> wrapper = buildQueryWrapper(sysResourceDTO);
        Page<SysResource> sysResourcePage = baseMapper.selectPage(pageQuery.buildPage(), wrapper);

        return TablePage.build(sysResourcePage, SysResourceVO.class);
    }

    /**
     * 构建前端需要的资源树形结构
     */
    @Override
    public List<Tree<String>> listResourceTreeSelect(SysResourceDTO sysResourceDTO) {
        // 查询正常状态的部门
        sysResourceDTO.setStatus(ColumnConstant.STATUS_NORMAL);
        LambdaQueryWrapper<SysResource> wrapper = buildQueryWrapper(sysResourceDTO);
        List<SysResource> sysResourceList = baseMapper.selectList(wrapper);
        return buildResourceTree(sysResourceList);
    }

    @Override
    public List<SysResourceVO> listResourceTreeTable(SysResourceDTO sysResourceDTO) {
        LambdaQueryWrapper<SysResource> wrapper = buildQueryWrapper(sysResourceDTO);
        List<SysResource> sysResourceList = baseMapper.selectList(wrapper);
        List<SysResourceVO> resourceTreeList = MapstructUtil.convert(sysResourceList, SysResourceVO.class);

        return TreeBuildUtil.build(resourceTreeList, "0", SysResourceVO::getResourceId);
    }

    private LambdaQueryWrapper<SysResource> buildQueryWrapper(SysResourceDTO sysResourceDTO) {
        return Wrappers.<SysResource>lambdaQuery()
                .eq(StringUtil.hasText(sysResourceDTO.getResourceCode()), SysResource::getResourceCode, sysResourceDTO.getResourceCode())
                .eq(StringUtil.hasText(sysResourceDTO.getResourceName()), SysResource::getResourceName, sysResourceDTO.getResourceName())
                .eq(StringUtil.hasText(sysResourceDTO.getAppId()), SysResource::getAppId, sysResourceDTO.getAppId())
                .eq(Objects.nonNull(sysResourceDTO.getStatus()), SysResource::getStatus, sysResourceDTO.getStatus())
                .orderByAsc(SysResource::getParentId)
                .orderByAsc(SysResource::getOrderNum);
    }

    @Override
    public List<SysResourceVO> listResourceListByUserId(String appId, String userId) {
        List<SysResource> sysResourceList = baseMapper.listResourceListByUserId(appId, userId, false);
        return MapstructUtil.convert(sysResourceList, SysResourceVO.class);
    }

    /**
     * 构建前端所需要下拉树结构
     */
    private List<Tree<String>> buildResourceTree(List<SysResource> sysResourceList) {
        if (CollUtil.isEmpty(sysResourceList)) {
            return Collections.emptyList();
        }

        return TreeBuildUtil.build(sysResourceList, ColumnConstant.PARENT_ID, TreeNodeConfig.DEFAULT_CONFIG.setIdKey("value").setNameKey("label"), (treeNode, tree) -> {
                    tree.setId(treeNode.getResourceId())
                            .setParentId(treeNode.getParentId())
                            .setName(treeNode.getResourceName())
                            .setWeight(treeNode.getOrderNum())
                            .putExtra("icon", treeNode.getIcon());

                    // 如果节点是选中状态，则设置选中样式
                    tree.putExtra("class", treeNode.isSelected() ? "selected" : "");
                }
        );
    }

    @Override
    public boolean checkResourceCodeUnique(SysResourceDTO sysResourceDTO) {
        return baseMapper.exists(Wrappers.<SysResource>lambdaQuery()
                .eq(SysResource::getResourceCode, sysResourceDTO.getResourceCode())
                .eq(SysResource::getParentId, sysResourceDTO.getParentId())
                .ne(Objects.nonNull(sysResourceDTO.getId()), SysResource::getId, sysResourceDTO.getId()));
    }

    @Override
    public boolean checkResourceNameUnique(SysResourceDTO sysResourceDTO) {
        return baseMapper.exists(Wrappers.<SysResource>lambdaQuery()
                .eq(SysResource::getResourceName, sysResourceDTO.getResourceName())
                .eq(SysResource::getParentId, sysResourceDTO.getParentId())
                .ne(Objects.nonNull(sysResourceDTO.getId()), SysResource::getId, sysResourceDTO.getId()));
    }

    @Override
    public boolean hasChild(String resourceId) {
        return baseMapper.exists(Wrappers.<SysResource>lambdaQuery()
                .eq(SysResource::getParentId, resourceId));
    }


    @Override
    public boolean checkResourceExistRole(String resourceId) {
        return roleResourceLinkService.exists(Wrappers.<RoleResourceLink>lambdaQuery()
                .eq(RoleResourceLink::getResourceId, resourceId));
    }

    @Override
    public boolean addResource(SysResourceDTO sysResourceDTO) {
        SysResource sysResource = MapstructUtil.convert(sysResourceDTO, SysResource.class);

        if (StringUtil.hasText(sysResource.getParentId())) {
            SysResource resource = baseMapper.selectOne(Wrappers.<SysResource>lambdaQuery()
                    .eq(SysResource::getResourceId, sysResource.getParentId()));

            // 如果父节点不为正常状态,则不允许新增子节点
            if (!ColumnConstant.STATUS_NORMAL.equals(resource.getStatus())) {
                throw new ServiceException("资源停用，不允许新增");
            }

            return baseMapper.insert(sysResource) > 0;
        }

        sysResource.setParentId(ColumnConstant.PARENT_ID);
        // 更新部分资源数据到 Meta
        if (!sysResource.getResourceType().equals(ColumnConstant.RESOURCE_TYPE_BUTTON)) {
            Meta meta = sysResource.getMeta();

            if (Objects.isNull(meta)) {
                meta = new Meta();
            }

            meta.setTitle(sysResource.getResourceName())
                    .setIcon(sysResource.getIcon())
                    .setRank(sysResource.getOrderNum());
            sysResource.setMeta(meta);
        }

        return baseMapper.insert(sysResource) > 0;
    }

    @Override
    public boolean editResource(SysResourceDTO sysResourceDTO) {
        SysResource sysResource = MapstructUtil.convert(sysResourceDTO, SysResource.class);
        // 更新部分资源数据到 Meta
        if (!sysResource.getResourceType().equals(ColumnConstant.RESOURCE_TYPE_BUTTON)) {
            Meta meta = sysResource.getMeta();

            if (Objects.isNull(meta)) {
                meta = new Meta();
            }

            meta.setTitle(sysResource.getResourceName())
                    .setIcon(sysResource.getIcon())
                    .setRank(sysResource.getOrderNum());
            sysResource.setMeta(meta);
        }


        return baseMapper.updateById(sysResource) > 0;
    }

    @Override
    public boolean removeResource(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean checkAppExitResource(List<String> appIds) {
        return baseMapper.exists(Wrappers.<SysResource>lambdaQuery()
                .in(SysResource::getAppId, appIds));
    }

    @Override
    public Set<String> listResourcePermissionByUserId(String userId) {
        List<SysResource> sysResourceList = baseMapper.listResourceListByUserId(TenantConstant.DEFAULT_UAC_APP_ID, userId, false);
        List<String> resourcePerms = sysResourceList.stream().map(SysResource::getPermission).toList();

        Set<String> permsSet = new HashSet<>();
        for (String perm : resourcePerms) {
            if (StringUtil.hasText(perm)) {
                permsSet.addAll(List.of(perm.trim().split(",")));
            }
        }
        return permsSet;
    }
}




