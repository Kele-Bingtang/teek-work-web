package top.teek.uac.system.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import top.teek.mp.annotation.FieldValueFill;
import top.teek.mp.annotation.ValueStrategy;
import top.teek.mp.base.BaseDO;
import top.teek.mp.type.ListStringTypeHandler;
import top.teek.uac.system.model.vo.SysRoleVO;

import java.util.List;

/**
 * @author Teeker
 * @date 2023-24-12 00:24:45
 * @note 应用角色信息
*/
@TableName(value = "t_sys_role", autoResultMap = true)
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysRoleVO.class, reverseConvertGenerate = false)
public class SysRole extends BaseDO {
    /**
     * 角色 ID
     */
    @TableField(fill = FieldFill.INSERT)
    @FieldValueFill(ValueStrategy.SNOWFLAKE)
    private String roleId;

    /**
     * 角色码
     */
    private String roleCode;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 责任人
     */
    @TableField(typeHandler = ListStringTypeHandler.class)
    private List<String> ownerId;

    /**
     * 角色介绍
     */
    private String intro;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 应用 ID
     */
    private String appId;

}