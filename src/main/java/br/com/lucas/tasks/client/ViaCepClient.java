package br.com.lucas.tasks.client;

import br.com.lucas.tasks.exception.CepNotFoundException;
import br.com.lucas.tasks.model.Address;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ViaCepClient {

    private final WebClient viaCep;

    private static final String VIA_CEP_URI = "/{cep}/json";

    public ViaCepClient(WebClient viaCep) {
        this.viaCep = viaCep;
    }

    public Mono<Address> getAddress(String cep) {
        return viaCep
                .get()
                .uri(VIA_CEP_URI, cep)
                .retrieve()
                .bodyToMono(Address.class)
                .onErrorResume(error -> Mono.error(CepNotFoundException::new));
    }

}
