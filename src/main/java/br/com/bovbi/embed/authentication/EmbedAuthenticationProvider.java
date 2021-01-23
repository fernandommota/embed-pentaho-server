package br.com.bovbi.embed.authentication;

import br.com.bovbi.embed.Utils;
import br.com.bovbi.embed.authenticated.EmbedGrantedAuthority;
import br.com.bovbi.embed.logger.EmbedLogger;
import br.com.bovbi.embed.rest.EmbedRestTemplate;
import br.com.bovbi.embed.rest.response.UserLoggedResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbedAuthenticationProvider implements AuthenticationProvider {
    private final static EmbedLogger LOGGER = EmbedLogger.Get(EmbedAuthenticationProvider.class);

    private final EmbedRestTemplate embedRestTemplate;

    public EmbedAuthenticationProvider(EmbedRestTemplate embedRestTemplate) {
        LOGGER.info("on");
        this.embedRestTemplate = embedRestTemplate;
    }

    /**
     * Case {@link org.springframework.security.authentication.AuthenticationManager#authenticate(Authentication)}
     * For instance of {@link AaiSessionAuthentication}
     * Then the provider do Authentication
     *
     * @param aClass
     * @return
     */
    public boolean supports(Class<?> aClass) {
        return aClass.equals(EmbedSessionAuthentication.class);
    }

    /**
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmbedSessionAuthentication auth = (EmbedSessionAuthentication) authentication;
        LOGGER.info("Trying authentication by token " + auth.getSession());

        //call the user logged in embed application
        UserLoggedResponse response = embedRestTemplate.getLoggedUser(auth.getSession());
        LOGGER.info("response from REST API " + response.toString());

        if (response.isActive()) {
            LOGGER.error("Authentication success by token!");
            LOGGER.info("" + response);
            
            // set as a authenticated user
            auth.setAuthenticated(true);
            // set the username
            auth.setName(response.getUsername());

            // set the roles for the user
            List<EmbedGrantedAuthority> list = new ArrayList<EmbedGrantedAuthority>();
            list.add(new EmbedGrantedAuthority("Authenticated"));

            if (!Utils.isNull(response.getRoles()))
                for (String role : response.getRoles()) {
                    list.add(new EmbedGrantedAuthority(role));
                    LOGGER.info("User has granted the role: " + role);
                }

            auth.setAuthorities(list);
        }else{
            LOGGER.error("Authentication failed by token!");
            LOGGER.info("" + response);
        }

        return auth;
    }
}
