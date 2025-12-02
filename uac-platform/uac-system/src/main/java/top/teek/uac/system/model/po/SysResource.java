package top.teek.uac.system.model.po;

import top.teek.mp.annotation.FieldValueFill;
import top.teek.mp.annotation.ValueStrategy;
import top.teek.mp.base.BaseDO;
import top.teek.uac.system.config.MetaTypeHandler;
import top.teek.uac.system.model.vo.SysResourceVO;
import top.teek.uac.system.model.vo.router.Meta;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author Teeker
 * @date 2023-23-12 00:23:08
 * @note 资源
 */
@TableName(value = "t_sys_resource", autoResultMap = true)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysResourceVO.class, reverseConvertGenerate = false)
public class SysResource extends BaseDO {
    /**
     * 资源 ID
     */
    @TableField(fill = FieldFill.INSERT)
    @FieldValueFill(ValueStrategy.SNOWFLAKE)
    private String resourceId;

    /**
     * 父级资源 ID
     */
    private String parentId;

    /**
     * 资源编码
     */
    private String resourceCode;

    /**
     * 资源名
     */
    private String resourceName;

    /**
     * 资源地址
     */
    private String path;

    /**
     * 图标
     */
    private String icon;

    /**
     * 显示顺序
     */
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
    private String component;

    /**
     * 资源前端额外配置
     */
    @TableField(typeHandler = MetaTypeHandler.class)
    private Meta meta;

    /**
     * 资源介绍
     */
    private String intro;

    /**
     * 租户 ID
     */
    private String tenantId;

    /**
     * 应用 ID
     */
    private String appId;


    /**
     * 是否被选中
     */
    @TableField(exist = false)
    private boolean selected;

}