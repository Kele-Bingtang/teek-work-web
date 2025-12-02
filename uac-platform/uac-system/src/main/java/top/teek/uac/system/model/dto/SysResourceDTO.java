package top.teek.uac.system.model.dto;

import top.teek.core.validate.RestGroup;
import top.teek.uac.system.model.po.SysResource;
import top.teek.uac.system.model.vo.router.Meta;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Teeker
 * @date 2023-23-12 00:23:08
 * @note 资源
 */
@Data
@AutoMapper(target = SysResource.class, reverseConvertGenerate = false)
public class SysResourceDTO {
    /**
     * 主键
     */
    @NotNull(message = "id 不能为空", groups = {RestGroup.EditGroup.class, RestGroup.DeleteGroup.class})
    private String id;
    /**
     * 资源 ID
     */
    private String resourceId;

    /**
     * 父级资源 ID
     */
    private String parentId;

    /**
     * 资源编码
     */
    @NotBlank(message = "资源编码不能为空", groups = {RestGroup.AddGroup.class})
    private String resourceCode;

    /**
     * 资源名
     */
    @NotBlank(message = "资源名称不能为空", groups = {RestGroup.AddGroup.class})
    @Size(min = 0, max = 50, message = "资源名称长度不能超过 {max} 个字符", groups = {RestGroup.AddGroup.class, RestGroup.EditGroup.class})
    private String resourceName;

    /**
     * 资源地址
     */
    @Size(min = 0, max = 200, message = "路由地址不能超过 {max} 个字符", groups = {RestGroup.AddGroup.class, RestGroup.EditGroup.class})
    private String path;

    /**
     * 图标
     */
    private String icon;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空", groups = {RestGroup.AddGroup.class})
    private Integer orderNum;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 资源类型（C目录 M资源 F按钮）
     */
    private String resourceType;

    /**
     * 组件路径
     */
    @Size(min = 0, max = 200, message = "组件路径不能超过 {max} 个字符", groups = {RestGroup.AddGroup.class, RestGroup.EditGroup.class})
    private String component;

    /**
     * 资源前端额外配置
     */
    private Meta meta;

    /**
     * 资源介绍
     */
    private String intro;

    /**
     * 应用 ID
     */
    @NotBlank(message = "应用 ID 不能为空", groups = {RestGroup.AddGroup.class, RestGroup.QueryGroup.class})
    private String appId;

    /**
     * 状态（0 异用 1 正常）
     */
    private Integer status;


}