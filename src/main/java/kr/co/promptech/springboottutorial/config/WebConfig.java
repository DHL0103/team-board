package kr.co.promptech.springboottutorial.config;

import kr.co.promptech.springboottutorial.interceptor.BoardAuthInterceptor;
import kr.co.promptech.springboottutorial.interceptor.BoardManagerAuthInterceptor;
import kr.co.promptech.springboottutorial.interceptor.PostAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final BoardAuthInterceptor boardAuthInterceptor;
    private final BoardManagerAuthInterceptor boardManagerAuthInterceptor;
    private final PostAuthInterceptor postAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(boardAuthInterceptor)
                .addPathPatterns("/board/{boardId}", "/board/{boardId}/**")
                .excludePathPatterns("/board/{boardId}/request", "/board/create");

        registry.addInterceptor(boardManagerAuthInterceptor)
                .addPathPatterns("/board/{boardId}/manager/**");

        registry.addInterceptor(postAuthInterceptor)
                .addPathPatterns("/board/{boardId}/post/{postId}", "/board/{boardId}/post/{postId}/**");
    }
}
