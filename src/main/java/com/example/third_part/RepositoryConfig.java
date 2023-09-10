package com.example.third_part;

import com.example.third_part.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RepositoryConfig {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    /*@Bean
    public Map<String, JpaRepository<?, Long>> repositoryMap() {
        Map<String, JpaRepository<?, Long>> repositoryMap = new HashMap<>();
        repositoryMap.put("Comment", (JpaRepository<?, Long>) commentRepository);
        repositoryMap.put("Friendship", (JpaRepository<?, Long>) friendshipRepository);
        repositoryMap.put("Message", (JpaRepository<?, Long>) messageRepository);
        repositoryMap.put("Post", (JpaRepository<?, Long>) postRepository);
        repositoryMap.put("User", (JpaRepository<?, Long>) userRepository);
        return repositoryMap;
    }*/


//
//    @Bean
//    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
//        return new HiddenHttpMethodFilter();
//    }
}