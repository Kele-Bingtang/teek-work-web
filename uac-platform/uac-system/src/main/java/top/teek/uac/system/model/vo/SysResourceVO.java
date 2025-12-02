package top.teek.uac.system.model.vo;

import top.teek.excel.annotation.ExcelDictFormat;
import top.teek.excel.convert.ExcelClassConvert;
import top.teek.excel.convert.ExcelDictConvert;
import top.teek.uac.system.export.NormalStatusHandler;
import top.teek.uac.system.model.vo.router.Meta;
import top.teek.utils.TreeBuildUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Teeker
 * @date 2023-12-12 00:23:08
 * @note 资源
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysResourceVO extends TreeBuildUtil.TreeBO<SysResourceVO> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty("序号")
    private Long id;

    /**
     * 资源 ID
     */
    @ExcelProperty("资源 ID")
    private String resourceId;

    /**
     * 父级资源 ID
     */
    @ExcelProperty("父级资源 ID")
    private String parentId;

    /**
     * 资源编码
     */
    @ExcelProperty("资源编码")
    private String resourceCode;

    /**
     * 资源名
     */
    @ExcelProperty("资源名")
    private String resourceName;

    /**
     * 资源地址前缀
     */
    @ExcelProperty("资源地址前缀")
    private String pathPrefix;

    /**
     * 资源地址
     */
    @ExcelProperty("资源地址")
    private String path;

    /**
     * 图标
     */
    @ExcelProperty("图标")
    private String icon;

    /**
     * 显示顺序
     */
    @ExcelProperty("显示顺序")
    private Integer orderNum;

    /**
     * 权限标识
     */
    @ExcelProperty("权限标识")
    private String permission;

    /**
     * 资源类型（C目录 M资源 F按钮）
     */
    @ExcelProperty(value = "资源类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readExp = "C:目录, M:资源, F:按钮")
    private String resourceType;

    /**
     * 组件路径
     */
    @ExcelProperty("组件路径")
    private String component;

    /**
     * 资源前端额外配置
     */
    @ExcelProperty(value = "资源前端额外配置", converter = ExcelClassConvert.class)
    private Meta meta;

    /**
     * 资源介绍
     */
    @ExcelProperty("资源介绍")
    private String intro;

    /**
     * 应用 ID
     */
    @ExcelProperty("应用 ID")
    private String appId;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(handler = NormalStatusHandler.class)
    private Integer status;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;


}