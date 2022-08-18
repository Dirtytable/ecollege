package com.example.ecollege.application.playoad.responce;

import com.example.ecollege.api.security.services.UserDetailsImpl;
import lombok.Getter;
import java.util.List;

@Getter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private String realName;
    private String group;
    private List<String> roles;
    public JwtResponse(String token, UserDetailsImpl userDetails, List<String> roles) {
        this.token = token;
        this.id = userDetails.getId();
        this.username = userDetails.getUsername();
        this.email =   userDetails.getEmail();
        this.realName =  userDetails.getRealName();
        this.group =  userDetails.getGroup();
        this.roles = roles;
    }
}
