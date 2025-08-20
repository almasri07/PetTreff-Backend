package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 60)
    @ToString.Include
    private String username;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String fullName;

    @Column(length = 3000)
    private String profilePictureUrl;

    @Version
    private Long version;

    @OneToMany(mappedBy = "owner", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Pet> pets = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Post> posts = new ArrayList<>();

    // Rollen lieber LAZY (nicht EAGER), sonst lädt jede User-Abfrage alle Rollen
    // mit
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> authorities = new HashSet<>();

    // users welche dieser User folgen
    @ManyToMany
    @JoinTable(name = "user_following", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "following_id"))
    private Set<User> following = new HashSet<>();

    // users, die von diesem User gefolgt werden
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    public int getFollowerCount() {
        return followers != null ? followers.size() : 0;
    }

    public int getFollowingCount() {
        return following != null ? following.size() : 0;
    }

    public void follow(User other) {
        if (other == null || Objects.equals(this.id, other.id))
            return;
        this.following.add(other);
        other.followers.add(this);
    }

    public void unfollow(User other) {
        if (other == null)
            return;
        this.following.remove(other);
        other.followers.remove(this);
    }

    public List<User> getFriends() {

        return this.following.stream()
                .filter(user -> user.getFollowers().contains(this))
                .toList();
    }
}