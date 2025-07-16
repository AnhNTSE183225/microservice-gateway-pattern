package com.mss301.msaccount_se183225.authentication;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
// CHANGE: table name, entity name
@Table(name = "SystemAccounts")
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    Integer id;

    @Column(name = "Username", columnDefinition = "VARCHAR(100)", nullable = false)
    String username;

    @Column(name = "Email", columnDefinition = "VARCHAR(255)", nullable = false)
    String email;

    @Column(name = "Password", columnDefinition = "VARCHAR(255)", nullable = false)
    String password;

    @Column(name = "Role", columnDefinition = "INT")
    Integer role;

    @Column(name = "IsActive")
    @Builder.Default
    boolean active = true;

    // CHANGE
    public String getRoleName() {
        String roleName = "ROLE_";
        if (role == 1) {
            roleName += "ADMIN";
        } else if (role == 2) {
            roleName += "MODERATOR";
        } else if (role == 3) {
            roleName += "DEVELOPER";
        } else {
            roleName += "MEMBER";
        }
        return roleName;
    }

    // CHANGE
    @Override
    public String getUsername() {
        return this.email;
    }

    // CHANGE
    @Override
    public boolean isEnabled() {
        return this.active;
    }

    // CHANGE
    @Override
    public String getName() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRoleName()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}