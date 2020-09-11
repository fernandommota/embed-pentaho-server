package br.com.bovbi.embed.authentication;

import br.com.bovbi.embed.authenticated.EmbedGrantedAuthority;
import br.com.bovbi.embed.authenticated.EmbedUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Classe de autenticação do AAI
 * <p>
 * Instanciado em {@link br.com.bovbi.embed.filter.AaiLoginFilter}
 * Autenticado em {@link AaiAuthenticationProvider}
 * Caso sessão for autenticada com sucesso,
 * os atributos name e permissoes seram setadas internamente
 * na sessao do pentaho {@link org.pentaho.platform.api.engine.IPentahoSession}
 * em {@link org.pentaho.platform.engine.security.event.PentahoAuthenticationSuccessListener}
 * <p>
 * pentahoSession.setAuthenticated( authentication.getName() );
 * pentahoSession.setAttribute( IPentahoSession.SESSION_ROLES, authentication.getAuthorities() );
 */
public class EmbedSessionAuthentication implements Authentication {

    private Boolean authenticated = Boolean.FALSE;

    /**
     * Id do aai.usuario
     */
    
    private String name;
    private String session;
    private List<EmbedGrantedAuthority> authorities;
    private EmbedUserDetail details;

    public EmbedSessionAuthentication(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Object getCredentials() {
        return session;
    }

    public EmbedUserDetail getDetails() {
        return details;
    }

    public Object getPrincipal() {
        return name;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthorities(List<EmbedGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setDetails(EmbedUserDetail details) {
        this.details = details;
    }
}
