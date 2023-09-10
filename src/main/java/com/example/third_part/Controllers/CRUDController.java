package com.example.third_part.Controllers;

import com.example.third_part.Repositories.*;
import com.example.third_part.Models.Comment;
import com.example.third_part.Models.Friendship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class CRUDController {
    private final CommentRepository commentRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UniversalRepository universalRepository;

    @Autowired
    public CRUDController(CommentRepository commentRepository,
                          FriendshipRepository friendshipRepository,
                          MessageRepository messageRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          UniversalRepository universalRepository) {
        this.commentRepository = commentRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.universalRepository = universalRepository;
    }

    @GetMapping("/{ModelName}/Edit/{id}")
    public String Redact(
            @PathVariable(name = "ModelName") String modelName,
            @RequestParam(name = "fieldNameList") List<String> fieldNameList,
            @RequestParam Map<String, String> requestParams,
            Model model, @PathVariable Long id) {
        Map<String, String> cleanedRequestParams = cleanRequestParamKeys(requestParams);

        Optional<?> optionalEntity = universalRepository.findEntityById(modelName, id);
        if (optionalEntity.isPresent()) {
            Object entity = optionalEntity.get();
            updateEntityFields(entity, cleanedRequestParams);

            universalRepository.saveEntity(modelName, entity);
            model.addAttribute("ModelName", modelName);
            model.addAttribute("val", cleanedRequestParams.values());
            model.addAttribute("fieldNameList", fieldNameList.stream().map(this::cleanFieldName).collect(Collectors.toList()));
            return "ModelDetail";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/{ModelName}/Edit/Submit")
    public String RedactSub(
            @PathVariable (name = "ModelName") String modelName,
            @RequestParam (name = "0", defaultValue = "") String val0,
            @RequestParam (name = "1", defaultValue = "") String val1,
            @RequestParam (name = "2", defaultValue = "") String val2,
            @RequestParam (name = "3", defaultValue = "") String val3,
            @RequestParam (name = "4", defaultValue = "") String val4,
            @RequestParam (name = "5", defaultValue = "") String val5,
            @RequestParam (name = "6", defaultValue = "") String val6,
            @RequestParam (name = "7", defaultValue = "") String val7,
            Model model){
        String[] val = new String[8];
        val[0] = val0;
        val[1] = val1;
        val[2] = val2;
        val[3] = val3;
        val[4] = val4;
        val[5] = val5;
        val[6] = val6;
        val[7] = val7;
        model.addAttribute("val",val);
        switch (modelName) {
            case "Comment" -> {
                Comment entityToUpdate1 = new Comment();
                entityToUpdate1.setId(Long.parseLong(val[0]));
                entityToUpdate1.setContent(val[1]);
                entityToUpdate1.setUrlPhoto(val[2]);
                entityToUpdate1.setTimestamp(val[3]);
                commentRepository.save(entityToUpdate1);
            }
            case "Friendship" -> {
                Friendship entityToUpdate2 = new Friendship();
                entityToUpdate2.setId(Long.parseLong(val[0]));
                entityToUpdate2.setUsername(val[1]);
                entityToUpdate2.setUrlPhoto(val[2]);
                entityToUpdate2.setStatus(val[3]);
                friendshipRepository.save(entityToUpdate2);
            }
            default -> {
                return null;
            }
        }
        return "redirect:/";
    }

    @GetMapping("/{modelName}/Create")
    public String Create(
            @PathVariable(name = "modelName") String modelName,
            @RequestParam(name = "fieldNameList") List<String> fieldNameList,
            Model model) {
        model.addAttribute("ModelName", modelName);
        List<String> cleanedFieldNameList = new ArrayList<>();
        int listSize = fieldNameList.size();
        for (int i = 0; i < listSize; i++) {
            String fieldName = fieldNameList.get(i);
            if (i == 0) {
                if (fieldName.length() > 0) {
                    fieldName = fieldName.substring(1);
                }
            } else if (i == listSize - 1) {
                if (fieldName.length() > 0) {
                    fieldName = fieldName.substring(0, fieldName.length() - 1);
                }
            }
            cleanedFieldNameList.add(fieldName);
        }
        model.addAttribute("fieldNameList", cleanedFieldNameList.stream().toList());
        return "ModelDetail";
    }

    @PostMapping("/{modelName}/Create/Submit")
    public String CreateSub(
            @PathVariable(name = "modelName") String modelName,
            @RequestParam Map<String, String> requestParams) {
        Map<String, String> cleanedRequestParams = cleanRequestParamKeys(requestParams);
        Object entity = createEntityFromParams(modelName, cleanedRequestParams);
        universalRepository.saveEntity(modelName, entity);
        return "redirect:/";
    }

    @GetMapping("/{modelName}/Delete/{id}")
    public String Delete(
            @PathVariable(name = "modelName") String modelName,
            @PathVariable(name = "id") Long id) {
        universalRepository.deleteEntityById(modelName, id);
        return "redirect:/";
    }

    private String cleanFieldName(String fieldName) {
        if (fieldName.length() > 0 && fieldName.charAt(0) == '[') {
            fieldName = fieldName.substring(1);
        }
        if (fieldName.length() > 0 && fieldName.charAt(fieldName.length() - 1) == ']') {
            fieldName = fieldName.substring(0, fieldName.length() - 1);
        }
        return fieldName;
    }

    private Map<String, String> cleanRequestParamKeys(Map<String, String> requestParams) {
        return requestParams.entrySet().stream()
                .filter(entry -> entry.getKey().matches("\\d+"))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
    private Object createEntityFromParams(String modelName, Map<String, String> fieldValues) {
        try {
            Class<?> entityClass = Class.forName("com.example.pr3_maven.Models." + modelName);
            Object entity = entityClass.getDeclaredConstructor().newInstance();

            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                String fieldName = "field" + entry.getKey();
                Field field = entityClass.getDeclaredField(fieldName);
                field.setAccessible(true);

                Class<?> fieldType = field.getType();
                if (fieldType == Long.class) {
                    field.set(entity, Long.parseLong(entry.getValue()));
                } else if (fieldType == Integer.class) {
                    field.set(entity, Integer.parseInt(entry.getValue()));
                } else {
                    field.set(entity, entry.getValue());
                }
            }

            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void updateEntityFields(Object entity, Map<String, String> fieldValues) {
        Class<?> entityClass = entity.getClass();
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            String fieldName = "field" + entry.getKey();
            try {
                var field = entityClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.getType() == Long.class) {
                    field.set(entity, Long.parseLong(entry.getValue()));
                } else if (field.getType() == Integer.class) {
                    field.set(entity, Integer.parseInt(entry.getValue()));
                } else {
                    field.set(entity, entry.getValue());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
