package project.kafka.cpf_validator.integrationTest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Slf4j
@Testcontainers
@Configuration
@Profile("test")
public class MySqlTestContainer {

    @Container
    private static final MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>("mysql:9.1.0")
                .withExposedPorts(3306)
                .withDatabaseName("mydb")
                .withUsername("admin")
                .withPassword("123");

        log.info("\n################################################"
                + "\n######### INICIANDO CONTAINER MYSQL ###########"
                + "\n################################################"
        );
        mysqlContainer.setPortBindings(List.of("3306:3306"));
        mysqlContainer.start();
    }
}
