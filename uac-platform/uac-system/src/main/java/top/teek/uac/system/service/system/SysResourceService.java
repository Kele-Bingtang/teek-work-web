package top.teek.uac.system.service.system;

import cn.hutool.core.lang.tree.Tree;
import top.teek.mp.base.PageQuery;
import top.teek.mp.base.TablePage;
import top.teek.uac.system.model.dto.SysResourceDTO;
import top.teek.uac.system.model.po.SysResource;
import top.teek.uac.system.model.vo.SysResourceVO;
import top.teek.uac.system.model.vo.router.RouterVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author Teeker
 * @date 2023-11-11 23:40:21
 * @note 针对表【t_sys_resource(资源表)】的数据库操作Service
 */
public interface SysResourceService extends IService<SysResource> {

    /**
     * 查询前端需要的路由列表
     *
     * @param appId 应用 ID
     * @return 路由列表
     */
    List<RouterVO> listRoutes(String appId);

    /**
     * 通过条件查询资源列表
     *
     * @param sysResourceDTO 查询条件
     * @return 资源列表
     */
    List<SysResourceVO> listAll(SysResourceDTO sysResourceDTO);

    /**
     * 通过条件查询资源列表（支持分页）
     *
     * @param sysResourceDTO 查询条件
     * @param pageQuery  分页参数
     * @return 资源列表
     */
    TablePage<SysResourceVO> listPage(SysResourceDTO sysResourceDTO, PageQuery pageQuery);

    /**
     * 查询资源下拉框列表
     *
     * @param sysResourceDTO 查询条件
     * @return 资源下拉框列表
     */
    List<Tree<String>> listResourceTreeSelect(SysResourceDTO sysResourceDTO);

    /**
     * 查询资源树形表格列表
     *
     * @param sysResourceDTO 查询条件
     * @return 资源树形表格列表
     */
    List<SysResourceVO> listResourceTreeTable(SysResourceDTO sysResourceDTO);
    
    /**
     * 获取用户的资源权限
     *
     * @param appId 应用 ID
     * @param userId 用户 ID
     * @return 权限列表
     */
    List<SysResourceVO> listResourceListByUserId(String appId, String userId);

    /**
     * 检查资源名称是否唯一
     *
     * @param sysResourceDTO 查询条件
     * @return 是否唯一
     */
    boolean checkResourceNameUnique(SysResourceDTO sysResourceDTO);

    /**
     * 检查资源是否存在子资源
     *
     * @param resourceId 资源id
     * @return 是否存在子资源
     */
    boolean hasChild(String resourceId);

    /**
     * 校验资源编码是否唯一
     *
     * @param sysResourceDTO 查询条件
     * @return 是否唯一
     */
    boolean checkResourceCodeUnique(SysResourceDTO sysResourceDTO);
    
    /**
     * 检查资源是否存在角色关联
     *
     * @param resourceId 资源 ID
     * @return 是否存在角色关联
     */
    boolean checkResourceExistRole(String resourceId);

    /**
     * 新增资源
     *
     * @param sysResourceDTO 新增对象
     * @return 是否成功
     */
    boolean addResource(SysResourceDTO sysResourceDTO);

    /**
     * 修改资源
     *
     * @param sysResourceDTO 修改对象
     * @return 是否成功
     */
    boolean editResource(SysResourceDTO sysResourceDTO);

    /**
     * 删除资源
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean removeResource(Long id);

    /**
     * 校验 App 下是否存在资源
     *
     * @param appIds 应用 ID 列表
     * @return 是否存在
     */
    boolean checkAppExitResource(List<String> appIds);

    /**
     * 根据用户 ID 查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> listResourcePermissionByUserId(String userId);
}
