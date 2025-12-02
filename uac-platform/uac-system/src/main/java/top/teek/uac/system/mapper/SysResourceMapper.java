package top.teek.uac.system.mapper;

import top.teek.uac.system.model.po.SysResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-24-12 02:24:13
 * @note 针对表 t_sys_resource(资源表)的数据库操作 Mapper
 */
public interface SysResourceMapper extends BaseMapper<SysResource> {

    /**
     * 通过用户 ID 获取有权限的资源列表
     * @param appId 应用 ID
     * @param userId 用户 ID
     * @param onlyGetResource 是否只获取资源
     * @return 资源列表
     */
    List<SysResource> listResourceListByUserId(@Param("appId") String appId, @Param("userId") String userId, @Param("onlyGetResource") boolean onlyGetResource);

}




