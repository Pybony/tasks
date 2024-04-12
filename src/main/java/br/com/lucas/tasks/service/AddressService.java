package br.com.lucas.tasks.service;

import br.com.lucas.tasks.client.ViaCepClient;
import br.com.lucas.tasks.exception.CepNotFoundException;
import br.com.lucas.tasks.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class AddressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressService.class);

    private final ViaCepClient viaCepClient;

    public AddressService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public Mono<Address> getAddress(String zipCode) {
        return Mono.just(zipCode)
                .doOnNext(it -> LOGGER.info("Getting address to zipcode {}", zipCode))
                .flatMap(viaCepClient::getAddress)
                .doOnError(it -> Mono.error(CepNotFoundException::new));
    }

}
