package kr.co.promptech.springboottutorial.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final BoardAuthInterceptor boardAuthInterceptor;
    private final PostAuthInterceptor postAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(boardAuthInterceptor)
                .addPathPatterns("/board/{boardId}", "/board/{boardId}/**")
                .excludePathPatterns("/board/{boardId}/request", "/board/create");

        registry.addInterceptor(postAuthInterceptor)
                .addPathPatterns("/board/{boardId}/post/{postId}", "/board/{boardId}/post/{postId}/**");
    }
}
