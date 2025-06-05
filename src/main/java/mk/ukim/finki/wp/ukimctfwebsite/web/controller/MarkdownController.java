package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import mk.ukim.finki.wp.ukimctfwebsite.service.impl.MarkdownService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/markdown")
@PreAuthorize("isAuthenticated()")
public class MarkdownController {

    private final MarkdownService markdownService;

    public MarkdownController(MarkdownService markdownService) {
        this.markdownService = markdownService;
    }

    @PostMapping("/render")
    public ResponseEntity<String> renderMarkdown(@RequestBody String markdown) {
        String html = markdownService.convertMarkdownToHtml(markdown);
        return ResponseEntity.ok(html);
    }
}
