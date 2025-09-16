package com.socialmedia.petTreff.service;

import com.socialmedia.petTreff.dto.CreateUserDTO;
import com.socialmedia.petTreff.dto.PetDTO;
import com.socialmedia.petTreff.dto.PostDTO;
import com.socialmedia.petTreff.dto.UserDTO;
import com.socialmedia.petTreff.entity.Pet;
import com.socialmedia.petTreff.entity.Post;
import com.socialmedia.petTreff.entity.Role;
import com.socialmedia.petTreff.entity.User;
import com.socialmedia.petTreff.mapper.UserMapper;
import com.socialmedia.petTreff.mapper.CreateUserMapper;
import com.socialmedia.petTreff.mapper.PetMapper;
import com.socialmedia.petTreff.mapper.PostMapper;
import com.socialmedia.petTreff.repository.RoleRepository;
import com.socialmedia.petTreff.repository.UserRepository;
import com.socialmedia.petTreff.security.InputSanitizer;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService { /// Repository gibt immer Optional zurück, wenn der User nicht gefunden wird,
    /// dann wird eine Exception geworfen

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDto);
    }

    @Transactional
    public User createUser(CreateUserDTO userDTO) {

        userDTO.setUsername(InputSanitizer.sanitizePlain(userDTO.getUsername()));

        if (userDTO.getProfilePictureUrl() != null) {
            userDTO.setProfilePictureUrl(InputSanitizer.sanitizeUrl(userDTO.getProfilePictureUrl()));
        }

        User user = CreateUserMapper.toEntity(userDTO);

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Role userRole = roleRepository.findByAuthority("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role 'ROLE_USER' not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setAuthorities(roles);
        return userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public User adminCreateUser(User user) {



        user.setUsername(InputSanitizer.sanitizePlain(user.getUsername()));

        Role adminRole = roleRepository.findByAuthority("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role 'ADMIN' not found"));
        user.setAuthorities(Set.of(adminRole));
        return userRepository.save(user);
    }

    // löscht einen User,
    // nur wenn der Request-Sender der User selbst oder ein Admin ist.
    @Transactional
    @PreAuthorize("hasRole('ADMIN')" + " or @userSecurity.hasUserId(#id)")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserDTO> searchUsers(String query) {
        List<User> users;
        if (query == null || query.isEmpty()) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByUsernameContainingIgnoreCase(query);
        }
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<PetDTO> getUserPets(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Pet> pets = user.getPets();

        List<PetDTO> petDTOs = pets.stream()
                .map(PetMapper::toDto)
                .toList();
        return petDTOs;
    }

    public List<PostDTO> getUserPosts(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Post> posts = user.getPosts();
        List<PostDTO> postDTOs = posts.stream()
                .map(PostMapper::toDto)
                .toList();

        return postDTOs;

    }

    public Set<UserDTO> getUserFriends(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<User> friends = user.getFriends();
        Set<UserDTO> friendDTOs = friends.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toSet());

        return friendDTOs;
    }

    public Set<UserDTO> getMutualUserFriends(Long id , Long otherUserId) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User other = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Set<User> mutual = user.getMutualFriends(other);

        Set<UserDTO> mutualDTO = mutual.stream().map(UserMapper::toDto)
                .collect(Collectors.toSet());

        return mutualDTO;
    }


    public int getFriendsCount(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getFriendsCount();

    }


    @Transactional
    @PreAuthorize("@userSecurity.hasUserId(#id)")
    public void changePassword(Long id, String newPassword) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String encoded = passwordEncoder.encode(newPassword);

        user.setPassword(encoded);
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("@userSecurity.hasUserId(#id)")
    public UserDTO updateProfilePicture(Long id, String profilePictureUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setProfilePictureUrl(profilePictureUrl);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("@userSecurity.hasUserId(#id)")
    public UserDTO updateUsername(Long id, String newUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setUsername(newUsername);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("@userSecurity.hasUserId(#id)")
    public UserDTO updateEmail(Long id, String newEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setEmail(newEmail);
        return UserMapper.toDto(userRepository.save(user));
    }



}

