package com.example.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String username;
    @Setter
    private String password;
    @Setter
    private String nickname;
    @Setter
    private String firstName;
    @Setter
    private String lastName;
    @Setter
    private Integer age;
    @Setter
    private String email;
    @Setter
    private String phone;
    @Setter
    private String profileImagePath;
    @Setter
    private String role;
    @Setter
    private String businessNum;

    public static UserEntity fromUserDetails(CustomUserDetails userDetails) {
        return UserEntity.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .password(userDetails.getPassword())
                .nickname(userDetails.getNickname())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .age(userDetails.getAge())
                .email(userDetails.getEmail())
                .phone(userDetails.getPhone())
                .profileImagePath(userDetails.getProfileImagePath())
                .role(userDetails.getRole())
                .businessNum(userDetails.getBusinessNum())
                .build();
    }
}
