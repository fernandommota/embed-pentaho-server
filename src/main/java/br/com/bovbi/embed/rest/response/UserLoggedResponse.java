package br.com.bovbi.embed.rest.response;
import java.util.ArrayList;
import java.util.Date;

public class UserLoggedResponse {

    private String token;
    private String username;
    private ArrayList<String> roles;
    private Boolean active;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ArrayList<String> getRoles() {
        return this.roles;
    }

    public void setLogin(ArrayList<String> roles) {
        this.roles = roles;
    }

   
    @Override
    public String toString() {
        return "UserLoggedResponse{" +
                "username=\'" + this.username + "\'" +
                ", token=\'" + this.token + "\'" +
                ", active=\'" + this.active + "\'" +
                ", roles=\'" + roles.toString() + "\'" +
                '}';
    }
}
