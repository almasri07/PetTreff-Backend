package com.socialmedia.petTreff.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

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

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends = new HashSet<>();


    public Set<User> getFriends() {

        return this.friends;
    }

    // Add Freundschaft
    public boolean addFriend(User other) {
        if (other == null || other.equals(this)) return false;
        boolean added = this.friends.add(other);
        if (added) other.getFriends().add(this);
        return added;
    }

    // lösch Freundschaft
    public boolean removeFriend(User other) {
        if (other == null || other.equals(this)) return false;
        boolean removed = this.friends.remove(other);
        if (removed) other.getFriends().remove(this);
        return removed;
    }

    //  testet, ob zwischen 2 Users Freundscheft existiert.
    public boolean isFriendsWith(User other) {
        return other != null && this.friends.contains(other);
    }

    // gibt Anzahl der Freunden zurück
    public int getFriendsCount() {
        return this.friends.size();
    }

    // gibt eine Menge der gemeinsame Freunde zurück
    public Set<User> getMutualFriends(User other) {
        if (other == null) return Set.of();
        Set<User> mutual = new HashSet<>(this.friends);
        mutual.retainAll(other.getFriends());
        return mutual;
    }

    // wenn ein User gelöscht würde, dann wird auch aus Liste seiner Freunden gelöscht.
    @PreRemove
    private void preRemove() {
        for (User f : new HashSet<>(friends)) {
            f.getFriends().remove(this);
        }
        friends.clear();
    }


}