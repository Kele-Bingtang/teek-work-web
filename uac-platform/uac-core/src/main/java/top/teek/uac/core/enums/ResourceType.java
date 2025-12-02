package top.teek.uac.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Teeker
 * @date 2024/6/14 23:41:00
 * @note
 */
@Getter
@AllArgsConstructor
public enum ResourceType {
    CATALOG("C", "目录"),
    MENU("M", "资源"),
    FUNCTION("F", "按钮");

    private final String value;
    private final String label;

}
