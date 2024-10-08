package university.market.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DBCPConfig {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Bean
    public DataSource dataSource() {
        log.info("DBCPConfig dataSource {}", url);
        log.info("DBCPConfig dataSource {}", username);
        log.info("DBCPConfig dataSource {}", password);
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setLeakDetectionThreshold(30000);
        hikariConfig.setPoolName("Market-HikariCP");

        return new HikariDataSource(hikariConfig);
    }
}
