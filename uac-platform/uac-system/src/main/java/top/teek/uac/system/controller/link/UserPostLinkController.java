package top.teek.uac.system.controller.link;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.teek.uac.system.service.link.UserPostLinkService;

/**
 * @author Teeker
 * @date 2025/11/17 22:46:35
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/userPostLink")
public class UserPostLinkController {
    
    private final UserPostLinkService userPostLinkService;

}
