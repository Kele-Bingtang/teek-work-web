package top.teek.wfp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * @author Teeker
 * @date 2026/1/16 23:56:27
 * @since 1.0.0
 */
@SpringBootApplication
public class WebFlowPulseApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(WebFlowPulseApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ Work Web Flow Pulse 系统启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
