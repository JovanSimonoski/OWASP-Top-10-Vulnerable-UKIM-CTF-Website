package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import mk.ukim.finki.wp.ukimctfwebsite.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/admin/events")
@PreAuthorize("isAuthenticated()")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listAll(Model model) {
        model.addAttribute("events", this.eventService.listAll());
        model.addAttribute("bodyContent", "event/events");
        return "master-template";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("bodyContent", "event/eventsCreate");
        return "master-template";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam String title,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                             @RequestParam String url) {
        this.eventService.create(title, startDate, endDate, url);
        return "redirect:/admin/events";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       Model model) {
        model.addAttribute("event", this.eventService.findById(id));
        model.addAttribute("bodyContent", "event/eventsCreate");
        return "master-template";
    }

    @PostMapping("/edit/{id}")
    public String editPost(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                           @RequestParam String url) {
        this.eventService.update(id, title, startDate, endDate, url);
        return "redirect:/admin/events";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        this.eventService.delete(id);
        return "redirect:/admin/events";
    }

    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {
        model.addAttribute("events", this.eventService.searchUnsafe(query));
        model.addAttribute("bodyContent", "event/events");
        return "master-template";
    }

}
