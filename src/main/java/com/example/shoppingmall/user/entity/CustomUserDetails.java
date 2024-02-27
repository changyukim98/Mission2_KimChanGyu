package com.example.shoppingmall.user.entity;

import com.example.shoppingmall.user.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;

    private String username;
    private String password;

    @Getter
    @Setter
    private String nickname;
    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    @Getter
    @Setter
    private Integer age;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String phone;
    @Getter
    @Setter
    private String profileImagePath;
    @Getter
    @Setter
    private UserRole role;
    @Getter
    @Setter
    private String businessNum;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.name()));
        System.out.println(authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public static CustomUserDetails fromUserEntity(UserEntity userEntity) {
        return CustomUserDetails.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .nickname(userEntity.getNickname())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .age(userEntity.getAge())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .profileImagePath(userEntity.getProfileImagePath())
                .role(userEntity.getRole())
                .businessNum(userEntity.getBusinessNum())
                .build();
    }
}
