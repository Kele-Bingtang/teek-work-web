package top.teek.uac.system.mapper;

import top.teek.uac.system.model.dto.SysResourceDTO;
import top.teek.uac.system.model.po.RoleResourceLink;
import top.teek.uac.system.model.po.SysResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-22-12 02:22:59
 * @note 针对表 t_role_resource_link(角色关联资源表)的数据库操作 Mapper
 */
public interface RoleResourceLinkMapper extends BaseMapper<RoleResourceLink> {

    // ------- 角色关联资源相关 API（以角色为主）-------
    
    List<SysResource> listResourceListByRoleId(@Param("roleId") String roleId, @Param("appId") String appId, @Param("sysResourceDTO") SysResourceDTO sysResourceDTO);
}




