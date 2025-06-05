package mk.ukim.finki.wp.ukimctfwebsite.config;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.ukimctfwebsite.model.Category;
import mk.ukim.finki.wp.ukimctfwebsite.model.Event;
import mk.ukim.finki.wp.ukimctfwebsite.model.User;
import mk.ukim.finki.wp.ukimctfwebsite.repository.CategoryRepository;
import mk.ukim.finki.wp.ukimctfwebsite.repository.EventRepository;
import mk.ukim.finki.wp.ukimctfwebsite.repository.UserRepository;
import mk.ukim.finki.wp.ukimctfwebsite.service.CategoryService;
import mk.ukim.finki.wp.ukimctfwebsite.service.EventService;
import mk.ukim.finki.wp.ukimctfwebsite.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer {
    private final TeamMemberService teamMemberService;
    private final CategoryService categoryService;
    private final EventService eventService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EventRepository eventRepository;

    public DataInitializer(TeamMemberService teamMemberService, CategoryService categoryService, EventService eventService) {
        this.teamMemberService = teamMemberService;
        this.categoryService = categoryService;
        this.eventService = eventService;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            admin.setEmail("jovan.simonoski@students.finki.ukim.mk");
            userRepository.save(admin);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("ROLE_USER");
            user.setEmail("jovan.simonoski.1@students.finki.ukim.mk");
            userRepository.save(user);
        }
        if (categoryRepository.count() == 0) {
            for (int i = 0; i < 10; i++) {
                Category category = new Category();
                category.setName("Example Category " + i);
                categoryRepository.save(category);
            }
        }
        if (eventRepository.count() == 0) {
            for(int i = 0; i < 10; i++) {
                Event event = new Event();
                event.setTitle("Example Event " + i);
                event.setUrl("https://example.com/");
                Date date = new Date();
                event.setStartDate(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, 1); // add 1 day
                Date tomorrow = calendar.getTime();
                event.setEndDate(tomorrow);
                eventRepository.save(event);
            }
        }
    }
}
