package br.com.lucas.tasks.exception;

public class CepNotFoundException extends RuntimeException {

    public CepNotFoundException() {
        super("CEP not found");
    }

}