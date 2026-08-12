package com.apexbank.auth.security;

import com.apexbank.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final String userLoginId;
    private final String passwordHash;
    private final String role;
    private final Long accountId;
    private final String accountNumber;
    private final boolean locked;

    public CustomUserDetails(User user) {
        this.userLoginId = user.getUserId();
        this.passwordHash = user.getLoginPasswordHash();
        this.role = user.getRole();
        this.accountId = user.getAccountId();
        this.accountNumber = user.getAccountNumber();
        this.locked = Boolean.TRUE.equals(user.getAccountLocked());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return userLoginId;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return !locked; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
