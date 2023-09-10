package com.example.third_part.Controllers;

import com.example.third_part.Repositories.UniversalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.directory.SearchResult;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    private UniversalRepository universalRepository;

    @GetMapping("/search")
    public String search(@RequestParam(name = "modelName") String modelName,
                         @RequestParam(name = "fieldName") String fieldName,
                         @RequestParam(name = "fieldValue") String fieldValue,
                         Model model) throws ClassNotFoundException {

        List<SearchResult> searchResults = universalRepository.searchEntities(modelName, fieldName, fieldValue);

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("modelName", modelName);

        return "result_view";
    }
}
