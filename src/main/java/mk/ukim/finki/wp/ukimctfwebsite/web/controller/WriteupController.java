package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import mk.ukim.finki.wp.ukimctfwebsite.model.*;
import mk.ukim.finki.wp.ukimctfwebsite.service.*;
import mk.ukim.finki.wp.ukimctfwebsite.service.impl.MarkdownService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class WriteupController {

    private final WriteupService writeupService;
    private final CategoryService categoryService;
    private final TeamMemberService teamMemberService;
    private final EventService eventService;
    private final MarkdownService markdownService;

    public WriteupController(WriteupService writeupService, CategoryService categoryService, TeamMemberService teamMemberService, EventService eventService, MarkdownService markdownService) {
        this.writeupService = writeupService;
        this.categoryService = categoryService;
        this.teamMemberService = teamMemberService;
        this.eventService = eventService;
        this.markdownService = markdownService;
    }

    @GetMapping("/writeups/{id}")
    public String viewWriteup(@PathVariable Long id, Model model) {
        Writeup writeup = writeupService.findById(id);
        model.addAttribute("writeup", writeup);
        String html = markdownService.convertMarkdownToHtml(writeup.getBody());
        model.addAttribute("content", html);
        model.addAttribute("bodyContent", "writeup/writeupView");
        return "master-template";
    }

    @GetMapping("/writeups")
    public String listAll(@RequestParam(required = false) String title,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) Long eventId,
                          @RequestParam(required = false) Long teamMemberId,
                          @RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          Model model) {
        Page<Writeup> page = this.writeupService.findPage(title, categoryId, eventId, teamMemberId, pageNum - 1, pageSize);
        model.addAttribute("page", page);
        model.addAttribute("title", title);
        model.addAttribute("category", categoryId);
        model.addAttribute("event", eventId);
        model.addAttribute("teamMember", teamMemberId);
        model.addAttribute("categories", this.categoryService.listAll());
        model.addAttribute("events", this.eventService.listAll());
        model.addAttribute("teamMembers", this.teamMemberService.listAll());
        model.addAttribute("bodyContent", "writeup/writeups");
        return "master-template";
    }

    @GetMapping("/admin/writeups/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("events", eventService.listAll());
        model.addAttribute("teamMembers", teamMemberService.listAll());
        model.addAttribute("bodyContent", "writeup/writeupCreate");
        return "master-template";
    }

    @PostMapping("/admin/writeups/create")
    @PreAuthorize("isAuthenticated()")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("category") Long categoryId,
                             @RequestParam("event") Long eventId,
                             @RequestParam("teamMember") Long teamMemberId,
                             @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        Writeup writeup = this.writeupService.create(
                title,
                "",
                new Date(),
                categoryId,
                eventId,
                teamMemberId);

        for (MultipartFile imageFile : imageFiles) {
            saveImage(imageFile, writeup.getId());
        }

        return "redirect:/admin/writeups/editBody/" + writeup.getId();
    }

    @GetMapping("/admin/writeups/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Long id, Model model) {
        Writeup writeup = writeupService.findById(id);

        model.addAttribute("writeup", writeup);
        model.addAttribute("images", getWriteupImages(id));
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("events", eventService.listAll());
        model.addAttribute("teamMembers", teamMemberService.listAll());
        model.addAttribute("bodyContent", "writeup/writeupCreate");
        return "master-template";
    }

    @PostMapping("/admin/writeups/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editPost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("body") String body,
            @RequestParam("category") Long categoryId,
            @RequestParam("event") Long eventId,
            @RequestParam("teamMember") Long teamMemberId,
            @RequestParam("removedImages") List<String> removedImages,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {

        for (MultipartFile imageFile : imageFiles) {
            saveImage(imageFile, id);
        }

        deleteImages(removedImages, id);

        this.writeupService.update(id, title, body, new Date(), categoryId, eventId, teamMemberId);

        return "redirect:/admin/writeups/editBody/" + id;
    }

    @GetMapping("/admin/writeups/editBody/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editBody(@PathVariable Long id, Model model) {
        Writeup writeup = writeupService.findById(id);

        List<String> images = getWriteupImages(id);

        model.addAttribute("writeup", writeup);
        model.addAttribute("images", images);
        model.addAttribute("bodyContent", "writeup/writeupBodyEdit");
        return "master-template";
    }


    @PostMapping("/admin/writeups/editBody")
    @PreAuthorize("isAuthenticated()")
    public String editBodyPost(@RequestParam("id") Long id,
                               @RequestParam("body") String body) {
        this.writeupService.updateBody(id, body, new Date());

        return "redirect:/writeups";
    }

    @PostMapping("/admin/writeups/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Long id) {
        this.writeupService.delete(id);

        return "redirect:/writeups";
    }


    private void saveImage(MultipartFile imageFile, Long id) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {

            String fileName = getNextImageTitle(id);
//            String fileName = imageFile.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/images/writeups/" + id + "/");
            Files.createDirectories(uploadDir);
            assert fileName != null;
            Path imagePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String getNextImageTitle(Long id){
        File folder = new File("uploads/images/writeups/" + id);
        String[] files = folder.list();

        if (files != null) {
            return String.valueOf(Integer.parseInt(files[files.length - 1]) + 1);
        }
        return "0";
    }

    private static void deleteImages(List<String> images, Long id) throws IOException {
        File directory = new File("uploads/images/writeups/" + id);

        if (directory.isDirectory()) {
            for (String image : images) {

                String fileName = image.substring(image.lastIndexOf("/") + 1);

                File fileToDelete = new File(directory, fileName);

                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        System.out.println("Deleted: " + fileName);
                    } else {
                        System.err.println("Failed to delete: " + fileName);
                    }
                } else {
                    System.err.println("File not found: " + fileName);
                }
            }
        }
    }

    private static List<String> getWriteupImages(Long id) {
        File folder = new File("uploads/images/writeups/" + id);
        String[] files = folder.list();

        List<String> images = new ArrayList<>();
        if (files != null) {
            for (String file : files) {
                images.add("/uploads/images/writeups/" + id + "/" + file);
            }
        }

        return images;
    }
}
