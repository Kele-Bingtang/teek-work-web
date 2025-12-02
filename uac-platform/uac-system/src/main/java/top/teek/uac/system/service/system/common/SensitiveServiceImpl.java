package top.teek.uac.system.service.system.common;

import top.teek.sensitive.service.SensitiveService;
import top.teek.tenant.helper.TenantHelper;
import top.teek.uac.core.helper.UacHelper;
import top.teek.utils.StringUtil;
import org.springframework.stereotype.Service;

/**
 * @author Teeker
 * @date 2024/6/8 01:54:41
 * @note
 */
@Service
public class SensitiveServiceImpl implements SensitiveService {

    @Override
    public boolean isSensitive(String roleCode, String perms) {
        if (!UacHelper.isLogin()) {
            return true;
        }

        if (StringUtil.hasText(roleCode, perms)) {
            // 判断是否存在角色码或者资源权限
            if (UacHelper.hasRole(roleCode) && UacHelper.hasResourcePermission(perms)) {
                return false;
            }
        } else if (UacHelper.hasRole(roleCode)) {
            return true;
        } else if (UacHelper.hasResourcePermission(perms)) {
            return true;
        }

        if (TenantHelper.isEnable()) {
            // 管理员不需要脱敏
            return !UacHelper.isAdmin() && !UacHelper.isTenantAdmin();
        }

        return !UacHelper.isAdmin();
    }
}
