package br.com.bovbi.embed.rest;

import br.com.bovbi.embed.logger.EmbedLogger;
import br.com.bovbi.embed.rest.response.UserLoggedResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


public class EmbedRestTemplate implements IEmbedRestTemplate {
    private final static EmbedLogger LOGGER = EmbedLogger.Get(EmbedRestTemplate.class);

    private final static String endpoint = "/authentication/detail";
    private final String embedServiceUrl;

    private final RestTemplate restTemplate;
    private final String client;

    public EmbedRestTemplate(String embedUrl, String client) {
        LOGGER.info("on");
        this.restTemplate = new RestTemplate();
        this.embedServiceUrl = embedUrl;
        this.client = client;
    }

    public UserLoggedResponse getLoggedUser(String token) {
        try {
            ResponseEntity<UserLoggedResponse> response = this.restTemplate.exchange(getUrl(endpoint), HttpMethod.GET, httpEntity(token), UserLoggedResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException httpClientErrorException) {
            LOGGER.error("Fail on search the user logged!", httpClientErrorException);
        } catch (Exception restException) {
            LOGGER.error("Exception on search the user logged!", restException);
            restException.printStackTrace();
        }
        return null;
    }

    private <T> HttpEntity<T> httpEntity(String token) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("token", token);
        return new HttpEntity<T>(headers);
    }


    private String getUrl(String uri) {
        return this.embedServiceUrl + uri;
    }
}
