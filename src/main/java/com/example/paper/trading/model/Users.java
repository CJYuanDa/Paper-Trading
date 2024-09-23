package com.example.paper.trading.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    @Email(message = "Invalid Email") // dependency: validation
    private String email;

    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Never send the value from backend to UI app
    private String password;

    @Column(name = "date_of_birth", nullable = false)
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles;

}
