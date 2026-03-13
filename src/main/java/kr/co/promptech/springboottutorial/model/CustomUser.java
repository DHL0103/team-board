package kr.co.promptech.springboottutorial.model;

import kr.co.promptech.springboottutorial.model.enums.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {

    private final Long id;
    private final MemberRole role;

    public CustomUser(Long id, String username, String password, MemberRole role,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public MemberRole getRole() {
        return role;
    }

    @Override
    public boolean isAccountNonLocked() {
        return role != MemberRole.ROLE_SUSPENDED;
    }
}
