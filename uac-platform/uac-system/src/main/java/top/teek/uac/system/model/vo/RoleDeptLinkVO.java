package top.teek.uac.system.model.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Teeker
 * @date 2023/12/16 0:44
 * @note
 */
@Data
public class RoleDeptLinkVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 部门 ID
     */
    private String deptId;

    /**
     * 生效时间
     */
    private LocalDate validFrom;

    /**
     * 失效时间
     */
    private LocalDate expireOn;

    /**
     * 应用 ID
     */
    private String appId;
}
