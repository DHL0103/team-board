package kr.co.promptech.springboottutorial.config;

import kr.co.promptech.springboottutorial.interceptor.BoardAuthInterceptor;
import kr.co.promptech.springboottutorial.interceptor.BoardManagerAuthInterceptor;
import kr.co.promptech.springboottutorial.interceptor.PostAuthInterceptor;
import kr.co.promptech.springboottutorial.interceptor.PostMemberAuthInterceptor;
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
    private final PostMemberAuthInterceptor postMemberAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(boardAuthInterceptor)
                .addPathPatterns("/boards/{boardId}", "/boards/{boardId}/**", "/api/boards/{boardId}/**")
                .excludePathPatterns("/boards/{boardId}/request", "/boards/{boardId}/inactive", "/boards/create", "/boards/search", "/posts/image", "/api/boards/search");

        registry.addInterceptor(boardManagerAuthInterceptor)
                .addPathPatterns("/boards/{boardId}/manager/**");

        registry.addInterceptor(postAuthInterceptor)
                .addPathPatterns("/boards/{boardId}/posts/{postId}", "/boards/{boardId}/posts/{postId}/**");

        registry.addInterceptor(postMemberAuthInterceptor)
                .addPathPatterns(
                        "/boards/{boardId}/posts/delete/{id}",
                        "/boards/{boardId}/posts/update/{id}",
                        "/boards/{boardId}/posts/request/{id}"
                );
    }
}
