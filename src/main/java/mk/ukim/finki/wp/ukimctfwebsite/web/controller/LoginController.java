package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String login(Model model) {
        model.addAttribute("bodyContent", "login");
        return "master-template";
    }
}
