package br.com.bovbi.embed.rest;

import br.com.bovbi.embed.rest.response.UserLoggedResponse;

public interface IEmbedRestTemplate {

    UserLoggedResponse getLoggedUser(String session);
}
