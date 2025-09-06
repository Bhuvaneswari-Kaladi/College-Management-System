package com.example.cmis.model;

import com.example.cmis.util.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // For future expansion - example roles (e.g. STUDENT, ADMIN, FACULTY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}