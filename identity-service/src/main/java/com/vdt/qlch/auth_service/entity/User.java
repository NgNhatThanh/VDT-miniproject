//package com.vdt.qlch.auth_service.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.Instant;
//import java.util.LinkedHashSet;
//import java.util.Set;
//
//@Entity
//@Table(name = "users")
//@Getter
//@Setter
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(name = "username", nullable = false)
//    private String username;
//
//    @Column(name = "password", nullable = false)
//    private String password;
//
//    @Column(name = "full_name", nullable = false)
//    private String fullName;
//
//    @Column(name = "email", nullable = false, length = 50)
//    private String email;
//
//    @Column(name = "avatar_url", nullable = false)
//    private String avatarUrl;
//
//    @Column(name = "created_at", nullable = false)
//    private Instant createdAt;
//
//    @Column(name = "updated_at")
//    private Instant updatedAt;
//
//    @ManyToMany(mappedBy = "users")
//    private Set<Role> roles = new LinkedHashSet<>();
//
//}
