package university.market.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import university.market.common.CachingFilter;

@Configuration
public class FilterConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Bean
    public FilterRegistrationBean<CachingFilter> contentCachingFilter() {
        FilterRegistrationBean<CachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingFilter());
        registrationBean.addUrlPatterns("/api/member/*", "/api/dibs/*", "/api/item/*", "/api/offer/*", "/api/email/*",
                "/api/notification/*", "/api/message/*", "/api/chat/*");
        registrationBean.setOrder(1);
        registrationBean.setName("CachingFilter");
        return registrationBean;
    }
}

