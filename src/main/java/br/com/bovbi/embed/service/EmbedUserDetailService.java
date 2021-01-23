package br.com.bovbi.embed.service;

import br.com.bovbi.embed.Utils;
import br.com.bovbi.embed.authenticated.EmbedGrantedAuthority;
import br.com.bovbi.embed.authenticated.EmbedUserDetail;
import br.com.bovbi.embed.logger.EmbedLogger;
import br.com.bovbi.embed.rest.EmbedRestTemplate;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

public class EmbedUserDetailService implements UserDetailsService {
    private final static EmbedLogger LOGGER = EmbedLogger.Get(EmbedUserDetailService.class);

    public EmbedUserDetailService() {
        LOGGER.info("on");
    }

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("LOAD USER BY USERNAME " + username);

        int index = username.indexOf("-/pentaho/tenant0");
        if (-1 != index) {
            username = username.substring(0, index);
        } else {
            return null;
        }

        IPentahoSession session = PentahoSessionHolder.getSession();
        if (!Utils.isNull(session)) {
            LOGGER.info("SESSION toString: " + session.toString());
            LOGGER.info("SESSION " + session.getName() + ": " + session.getId());
            if (session.getName().equals(username)) {
                EmbedUserDetail userDetail = new EmbedUserDetail();
                userDetail.setName(username);
                userDetail.setActive(Boolean.TRUE);
                userDetail.setAuthorities(Arrays.asList(new EmbedGrantedAuthority("Authenticated")));
                return userDetail;
            }
        }
        return null;
    }
}
