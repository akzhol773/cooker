package org.example.cookercorner.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String email;

    String name;

    @Column(nullable = false)
    String password;

    String biography;

    String photoUrl;

    @ElementCollection
    @CollectionTable(name = "user_followings", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "followings")
    List<Long> followings = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "user_followers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "followers")
    List<Long> followers = new ArrayList<>();

    @CreationTimestamp
    LocalDateTime createdDate;

    @UpdateTimestamp
    LocalDateTime updatedDate;


    @Column(nullable = false)
    String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return this.email;
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
