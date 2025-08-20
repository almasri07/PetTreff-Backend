package com.socialmedia.petTreff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetTreffApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetTreffApplication.class, args);
	}
	/*
	 * @Bean
	 * public CommandLineRunner test(UserRepository userRepository, PostRepository
	 * postRepository, CommentRepository commentRepository) {
	 * return args -> {
	 * User user = new User();
	 * user.setUsername("hundeFan");
	 * user.setPassword("123456");
	 * user.setProfilePictureUrl("none");
	 * 
	 * Post post = new Post();
	 * post.setContent("it is just a testing content!");
	 * post.setAuthor(user); // ← Setze den Autor
	 * 
	 * Comment comment = new Comment();
	 * comment.setContent(" test test comment ");
	 * comment.setAuthor(user); // ← Falls du den Autor beim Comment trotzdem
	 * brauchst
	 * comment.setPost(post); // ← WICHTIG: Verknüpfe den Kommentar mit dem Post
	 * 
	 * List<Comment> comments = new ArrayList<>();
	 * comments.add(comment);
	 * post.setCommentList(comments); // ← Kommentare dem Post zuordnen
	 * 
	 * // Optional, falls du bidirektional arbeitest:
	 * List<Post> posts = new ArrayList<>();
	 * posts.add(post);
	 * user.setPosts(posts);
	 * 
	 * // Zuerst User speichern → damit `user` eine ID bekommt
	 * userRepository.save(user);
	 * 
	 * // Dann Post und Kommentar (wegen referenzierter IDs)
	 * postRepository.save(post);
	 * commentRepository.save(comment);
	 * 
	 * userRepository.deleteById(6L);
	 * userRepository.deleteById(7L);
	 * postRepository.deleteById(4L);
	 * commentRepository.deleteById(4L);
	 * };
	 */

}
