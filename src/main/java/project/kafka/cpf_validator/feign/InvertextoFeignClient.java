package project.kafka.cpf_validator.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.kafka.cpf_validator.feign.response.ResponseInverTexto;

@FeignClient(name = "invertextoApi", url = "${url.invertexto}")
@Component
public interface InvertextoFeignClient {

    @GetMapping("/validator?token=${token.invertexto}&type=cpf")
    ResponseInverTexto validateValue(
            @RequestParam("value") String value
    );
}
