package project.kafka.cpf_validator.integrationTest.steps;

import io.cucumber.java.pt.Dado;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InverTextoStep {

    @Value("${url.invertexto}")
    private String invertextoUrl;

    @Value("${token.invertexto}")
    private String token;

    @Dado("Que a API do invertexto retorna sucesso")
    public void queAApiDaInverTextoRetornaSucesso() {
        stubFor(get(urlEqualTo("/validator?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) + "&type=cpf&value=12345678900"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseBodySucesso())));
    }

    public String getResponseBodySucesso() {
        return "{\n" +
                "  \"valid\": true,\n" +
                "  \"formatted\": \"123.456.789-00\"\n" +
                "}";
    }
}