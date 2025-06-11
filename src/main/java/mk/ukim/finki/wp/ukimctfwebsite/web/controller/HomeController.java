package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping({"/","/home"})
public class HomeController {
    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "home");
        return "master-template";
    }

    @GetMapping("/vuln/a08")
    public String getA08VulnPage(Model model) {
        model.addAttribute("bodyContent", "vuln/a08demo");
        return "master-template";
    }
}
