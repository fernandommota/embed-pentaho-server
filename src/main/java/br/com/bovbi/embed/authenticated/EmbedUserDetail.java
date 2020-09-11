package br.com.bovbi.embed.authenticated;


import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Class to user logged
 */
public class EmbedUserDetail implements UserDetails {

    private String name;
    private List<EmbedGrantedAuthority> permissions;
    private Boolean active = Boolean.TRUE;

    public List<EmbedGrantedAuthority> getAuthorities() {
        return this.permissions;
    }

    public String getPassword() {
        return null;
    }

    public String getUsername() {
        return this.name;
    }

    public boolean isAccountNonExpired() {
        return this.active;
    }

    public boolean isAccountNonLocked() {
        return this.active;
    }

    public boolean isCredentialsNonExpired() {
        return this.active;
    }

    public boolean isEnabled() {
        return this.active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthorities(List<EmbedGrantedAuthority> authorities) {
        this.permissions = authorities;
    }

    public Boolean isActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
