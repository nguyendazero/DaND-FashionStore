package com.manager_account.entities;


import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name")
    private String fullName;;

    @Column(name = "haibazo_account_id", nullable = false)
    private long haibazoAccountId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "role")
    private String role;

    @Column(name = "status")
    private String status;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
    
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_expires_at")
    private LocalDateTime refreshExpiresAt;

}
