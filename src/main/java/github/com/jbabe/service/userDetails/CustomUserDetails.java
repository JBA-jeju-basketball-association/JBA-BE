package github.com.jbabe.service.userDetails;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails {

    @Getter
    private Integer userId;

    private String email;

    private String password;

    private List<String> authorities;

    @Override // 권한 조회
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override // 계정이 만료됐으면 false
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정이 잠겨있으면 false
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 사용자 자격 증명 만료 : false
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정이 비활성화 : false
    public boolean isEnabled() {
        return true;
    }
}
