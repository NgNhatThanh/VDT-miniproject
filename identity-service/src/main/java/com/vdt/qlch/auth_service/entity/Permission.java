//package com.vdt.qlch.auth_service.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.LinkedHashSet;
//import java.util.Set;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "permissions")
//public class Permission {
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
//    @ManyToMany(mappedBy = "permissions")
//    private Set<Role> roles = new LinkedHashSet<>();
//
//}