package com.example.demo.entity;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@Entity
@NoArgsConstructor
public class User extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    private String firstName;

    @Setter
    private String lastName;

    @Setter
    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @Setter
    @NaturalId
    private String email;

    @Setter
    @Column(nullable = false)
    private boolean enabled;

    @Setter
    @Transient
    private Double rating;

    @Setter
    @ManyToOne
    @JoinColumn(nullable = false)
    private Role role;

    @Setter
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private VerificationToken token;

    @Setter
    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private ResetCode resetCode;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Post> posts;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Comment> comments;

    public User(String firstName, String lastName, String password,
                String email, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<Role> list = new ArrayList<Role>();
        list.add(role);
        return list;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
