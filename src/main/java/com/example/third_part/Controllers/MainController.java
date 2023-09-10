package com.example.third_part.Controllers;

import com.example.third_part.ModelInfoDTO;
import com.example.third_part.Other;
import com.example.third_part.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private final CommentRepository commentRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public MainController(CommentRepository commentRepository,
                          FriendshipRepository friendshipRepository,
                          MessageRepository messageRepository,
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String MenuShow(Model model) {
        List<List<String>> modelInfoList = Other.ReadModelsWithFields();
        List<ModelInfoDTO> modelInfoDTOList = new ArrayList<>();
        for (List<String> innerList : modelInfoList) {
            if (!innerList.isEmpty()) {
                String modelName = innerList.get(0);
                List<String> fieldNames = new ArrayList<>(innerList.subList(1, innerList.size()));
                ModelInfoDTO modelInfoDTO = new ModelInfoDTO(modelName, fieldNames);
                modelInfoDTOList.add(modelInfoDTO);
            }
        }
        List<List<?>> allModelDataList = new ArrayList<>();
        CrudRepository<?, Long>[] repositories = new CrudRepository[]{commentRepository, friendshipRepository, messageRepository, postRepository, userRepository};
        for (CrudRepository<?, Long> repository : repositories) {
            List<?> modelData = findAllFromRepository(repository);
            allModelDataList.add(modelData);
        }
        model.addAttribute("AllModelDataList", allModelDataList);
        model.addAttribute("ModelInfoList", modelInfoDTOList);
        return "Menu";
    }
    private List<?> findAllFromRepository(CrudRepository<?, Long> repository) {
        return (List<?>) repository.findAll();
    }

}
