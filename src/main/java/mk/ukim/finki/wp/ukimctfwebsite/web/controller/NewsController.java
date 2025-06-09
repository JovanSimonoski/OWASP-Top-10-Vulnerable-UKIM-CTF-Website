package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/news")
public class NewsController {

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("bodyContent", "news/news");
        return "master-template";
    }
}
