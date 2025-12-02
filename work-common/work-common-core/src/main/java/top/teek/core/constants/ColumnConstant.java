package top.teek.core.constants;

/**
 * @author Teeker
 * @date 2023/11/13 23:09
 * @note 数据库字段常量
 */
public interface ColumnConstant {

    Integer STATUS_NORMAL = 1;
    Integer STATUS_EXCEPTION = 0;
    Integer NON_ISOLATE_AUTH = 0;
    Integer IS_ISOLATE_AUTH = 1;
    Integer NON_DELETED = 0;
    Integer DELETED = 1;
    String PARENT_ID = "0";
    /**
     * 资源类型（目录）
     */
    String RESOURCE_TYPE_DIR = "M";

    /**
     * 资源类型（资源）
     */
    String RESOURCE_TYPE_MENU = "C";

    /**
     * 资源类型（按钮）
     */
    String RESOURCE_TYPE_BUTTON = "F";
}
