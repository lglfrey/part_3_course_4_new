package com.example.third_part.Repositories;

import com.example.third_part.Models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.naming.directory.SearchResult;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class UniversalRepositoryImpl implements UniversalRepository{
    private final CommentRepository commentRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UniversalRepositoryImpl(CommentRepository commentRepository,
                                   FriendshipRepository friendshipRepository,
                                   MessageRepository messageRepository,
                                   PostRepository postRepository,
                                   UserRepository userRepository, Map<String, org.springframework.data.repository.Repository<?, ?>> repositoryMap ) {
        this.commentRepository = commentRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.repositoryMap = repositoryMap;
    }
    private final Map<String, org.springframework.data.repository.Repository<?, ?>> repositoryMap;
    @Override
    public Optional<?> findEntityById(String modelName, Long id) {
        return switch (modelName) {
            case "Comment" -> commentRepository.findById(id);
            case "Friendship" -> friendshipRepository.findById(id);
            case "Message" -> messageRepository.findById(id);
            case "Post" -> postRepository.findById(id);
            case "User" -> userRepository.findById(id);
            default -> Optional.empty();
        };
    }

    @Override
    public void saveEntity(String modelName, Object entity) {
        switch (modelName) {
            case "Comment" -> commentRepository.save((Comment) entity);
            case "Friendship" -> friendshipRepository.save((Friendship) entity);
            case "Message" -> messageRepository.save((Message) entity);
            case "Post" -> postRepository.save((Post) entity);
            case "User" -> userRepository.save((User) entity);
            default -> throw new IllegalArgumentException("Invalid model name");
        }
    }

    @Override
    public <T> void updateEntity(T entity, Map<String, String> fieldValues) {
        Class<?> entityClass = entity.getClass();

        for (Field field : entityClass.getDeclaredFields()) {
            String fieldName = field.getName();

            if (fieldValues.containsKey(fieldName)) {
                String fieldValue = fieldValues.get(fieldName);

                try {
                    String setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Class<?> fieldClass = field.getType();
                    Class<?>[] paramTypes = new Class<?>[] { fieldClass };
                    java.lang.reflect.Method setterMethod = entityClass.getMethod(setterMethodName, paramTypes);

                    Object convertedValue = convertStringToType(fieldClass, fieldValue);

                    setterMethod.invoke(entity, convertedValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<SearchResult> searchEntities(String modelName, String fieldName, String fieldValue) throws ClassNotFoundException {
        String jpql = "SELECT e FROM " + modelName + " e WHERE e." + fieldName + " = :fieldValue";
        Query query = entityManager.createQuery(jpql, Class.forName("com.example.third_part.Models." + modelName));
        query.setParameter("fieldValue", fieldValue);
        return query.getResultList();
    }
    private Class<?> getEntityClassByName(String modelName) {
        try {
            return Class.forName("com.example.third_part.models." + modelName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid model name: " + modelName);
        }
    }
    @Override
    public void deleteEntityById(String modelName, Long id) {
        /*JpaRepository<?, Long> repository = (JpaRepository<?, Long>) repositoryMap.get(modelName);

        if (repository != null) {
            repository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Invalid model name");
        }*/
        switch (modelName) {
            case "Comment" -> commentRepository.deleteById(id);
            case "Friendship" -> friendshipRepository.deleteById(id);
            case "Message" -> messageRepository.deleteById(id);
            case "Post" -> postRepository.deleteById(id);
            case "User" -> userRepository.deleteById(id);
            default -> throw new IllegalArgumentException("Invalid model name");
        }
    }

    private Object convertStringToType(Class<?> targetType, String value) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        }
        return null;
    }
}
