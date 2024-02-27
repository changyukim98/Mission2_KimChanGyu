package com.example.shoppingmall.user.entity;

import com.example.shoppingmall.user.UserRole;
import jakarta.persistence.*;
import lombok.*;

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
    @Enumerated(EnumType.STRING)
    private UserRole role;
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
