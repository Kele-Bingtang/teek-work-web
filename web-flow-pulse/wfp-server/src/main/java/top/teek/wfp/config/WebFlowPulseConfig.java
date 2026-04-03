package top.teek.wfp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Teeker
 * @date 2026/1/17 00:09:13
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "wfp")
public class WebFlowPulseConfig {
    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;
}
