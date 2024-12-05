package project.kafka.cpf_validator.integrationTest.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;

@Slf4j
@Testcontainers
@Configuration
@Profile("test")
public class WireMockTestContainer {

    @Container
    public static WireMockContainer wireMockContainer;

    static {
        wireMockContainer = new WireMockContainer("wiremock/wiremock:3.9.2")
                .withExposedPorts(37319);

        log.info("\n################################################"
                + "\n######### INICIANDO CONTAINER WIREMOCK #########"
                + "\n################################################"
        );
        wireMockContainer.setPortBindings(List.of("37319:8080"));
        wireMockContainer.start();
        WireMock.configureFor("localhost", 37319);
    }
}