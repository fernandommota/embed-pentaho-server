package br.com.bovbi.embed.authenticated;

import org.springframework.security.core.GrantedAuthority;

/**
 * Wrapper da Permissao (Role) do usuário
 * Origem aai.permission#role

 */
public class EmbedGrantedAuthority implements GrantedAuthority {

    private final String permission;

    public EmbedGrantedAuthority(String permission) {
        this.permission = permission;
    }

    public String getAuthority() {
        return permission;
    }
}
