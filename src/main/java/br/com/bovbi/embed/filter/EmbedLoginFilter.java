package br.com.bovbi.embed.filter;

import br.com.bovbi.embed.EmbedSecurityConstants;
import br.com.bovbi.embed.Utils;
import br.com.bovbi.embed.authentication.EmbedSessionAuthentication;
import br.com.bovbi.embed.logger.EmbedLogger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EmbedLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final static EmbedLogger LOGGER = EmbedLogger.Get(EmbedLoginFilter.class);

    public EmbedLoginFilter(AuthenticationManager authenticationManager) {
        super(
            new AntPathRequestMatcher(
                "/embed-login*"
            )
        );
        setAuthenticationManager(authenticationManager);
        LOGGER.info("on");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse rsp) throws AuthenticationException, IOException {
        LOGGER.info("Try authentication by embed token");
        String token = request.getParameter(EmbedSecurityConstants.Parameter.TOKEN);
        
        if (!Utils.isNullOrEmpty(token)) {    
            LOGGER.info("token parameter: " + token);
            EmbedSessionAuthentication a = new EmbedSessionAuthentication(token);
            return getAuthenticationManager().authenticate(a);
        } else {
            LOGGER.warn("token not found!");
        }
        return null;
    }
}