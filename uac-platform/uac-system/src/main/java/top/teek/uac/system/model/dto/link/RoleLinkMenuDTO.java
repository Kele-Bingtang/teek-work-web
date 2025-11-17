package top.teek.uac.system.model.dto.link;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Teeker
 * @date 2025/11/17 23:13:11
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class RoleLinkMenuDTO {
    private String roleId;
    private String appId;
    private List<String> selectedMenuIds;
}
