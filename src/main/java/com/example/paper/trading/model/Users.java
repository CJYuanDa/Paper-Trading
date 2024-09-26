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

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Invalid Email") // dependency: validation
    private String email;

    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Never send the value from backend to UI app
    private String password;

    @Column(name = "date_of_birth", nullable = false)
    @Past(message = "Date of birth must be in the past")
    private Date dateOfBirth;

    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles;

    public Users() {
    }

    public Users(String firstName, String lastName, String email, String password, Date dateOfBirth, Date createdDate, boolean locked, Set<Roles> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.createdDate = createdDate;
        this.locked = locked;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public @Email(message = "Invalid Email") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Invalid Email") String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @Past(message = "Date of birth must be in the past") Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@Past(message = "Date of birth must be in the past") Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Set<Roles> getRoles() {
        return roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }
}
