package br.com.cviana.config;

public interface TestConfigs {
    int SERVER_PORT = 8888; // LOCAL
    //int SERVER_PORT = 80; // DOCKER
    String HEADER_PARAM_AUTHORIZATION = "Authorization";
    String HEADER_PARAM_ORIGIN = "Origin";
    String ORIGIN_VALID = "http://localhost:8080";
    String ORIGIN_INVALID = "https://cors.error:443";
}
