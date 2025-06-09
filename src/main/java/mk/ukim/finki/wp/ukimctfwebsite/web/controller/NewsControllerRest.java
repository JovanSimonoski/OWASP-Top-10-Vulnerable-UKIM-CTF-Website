package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsControllerRest {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchNews(@RequestBody Map<String, String> request) {
        try {
            String url = request.get("url");
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching data: " + e.getMessage());
        }
    }
}