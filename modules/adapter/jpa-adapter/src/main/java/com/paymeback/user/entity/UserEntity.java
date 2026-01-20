package com.paymeback.user.entity;

import com.paymeback.user.domain.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean verified;

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @Builder
    private UserEntity(Long id, String email, String password, String name, UserRole role, boolean verified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role != null ? role : UserRole.USER;
        this.verified = verified;
    }

    public static UserEntity createUser(String email, String rawPassword, String name, PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
            .email(email)
            .password(passwordEncoder.encode(rawPassword))
            .name(name)
            .role(UserRole.USER)
            .verified(false)
            .build();
    }

    public void verifyEmail() {
        this.verified = true;
    }

    public void updateFromDomain(String email, String password, String name, UserRole role, boolean verified) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.verified = verified;
    }

    public boolean isPasswordValid(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

    public void changePassword(String newRawPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newRawPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}