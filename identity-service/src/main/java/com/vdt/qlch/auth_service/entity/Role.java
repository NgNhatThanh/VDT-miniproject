//package com.vdt.qlch.auth_service.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.jpa.repository.Modifying;
//
//import java.util.LinkedHashSet;
//import java.util.Set;
//
//@Entity
//@Table(name = "roles")
//@Getter
//@Setter
//public class Role {
//
//    @Id
//    @Column(name = "id", nullable = false)
//    private Integer id;
//
//    @Column(name = "name", nullable = false, length = 50)
//    private String name;
//
//    @Column(name = "description")
//    private String description;
//
//    @ManyToMany
//    @JoinTable(name = "role_has_permissions",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id"))
//    private Set<Permission> permissions = new LinkedHashSet<>();
//
//    @ManyToMany
//    @JoinTable(name = "user_has_roles",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    private Set<User> users = new LinkedHashSet<>();
//
//}
