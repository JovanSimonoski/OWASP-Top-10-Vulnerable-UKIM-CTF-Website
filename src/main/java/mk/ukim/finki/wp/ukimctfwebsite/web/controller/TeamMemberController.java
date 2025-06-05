package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import mk.ukim.finki.wp.ukimctfwebsite.model.TeamMember;
import mk.ukim.finki.wp.ukimctfwebsite.service.CategoryService;
import mk.ukim.finki.wp.ukimctfwebsite.service.TeamMemberService;
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
import java.util.List;

@Controller
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    private final CategoryService categoryService;

    public TeamMemberController(TeamMemberService teamMemberService, CategoryService categoryService) {
        this.teamMemberService = teamMemberService;
        this.categoryService = categoryService;
    }

    @GetMapping("/team")
    public String getTeamPage(Model model) {
        model.addAttribute("teamMembers", this.teamMemberService.listAll());
        model.addAttribute("bodyContent", "teamMember/team");
        return "master-template";
    }

    @GetMapping("/admin/team/list")
    @PreAuthorize("isAuthenticated()")
    public String listAll(Model model) {
        model.addAttribute("teamMembers", this.teamMemberService.listAll());
        model.addAttribute("bodyContent", "teamMember/teamMembers");
        return "master-template";
    }

    @GetMapping("/admin/team/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model) {
        model.addAttribute("categories", this.categoryService.listAll());
        model.addAttribute("bodyContent", "teamMember/teamMemberCreate");
        return "master-template";
    }

    @PostMapping("/admin/team/create")
    @PreAuthorize("isAuthenticated()")
    public String createPost(@RequestParam String nickname,
                             @RequestParam String shortBiography,
                             @RequestParam String githubLink,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam List<Long> categoryList) throws IOException {
        TeamMember teamMember = this.teamMemberService.create(nickname, shortBiography, githubLink, "/uploads/images/default.png", categoryList);

        String imagePath = saveImage(imageFile, teamMember.getId());
        this.teamMemberService.updateImage(teamMember.getId(), imagePath);

        return "redirect:/admin/team/list";
    }

    @GetMapping("/admin/team/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Long id,
                       Model model) {
        model.addAttribute("categories", this.categoryService.listAll());
        model.addAttribute("teamMember", this.teamMemberService.findById(id));
        model.addAttribute("bodyContent", "teamMember/teamMemberCreate");
        return "master-template";
    }

    @PostMapping("/admin/team/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editPost(@PathVariable Long id,
                           @RequestParam String nickname,
                           @RequestParam String shortBiography,
                           @RequestParam String githubLink,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           @RequestParam String existingImagePath,
                           @RequestParam List<Long> categoryList) throws IOException {
        String imagePath = existingImagePath;
        if (imageFile != null && !imageFile.isEmpty()) {
            imagePath = saveImage(imageFile, id);
        }

        this.teamMemberService.update(id, nickname, shortBiography, githubLink, imagePath, categoryList);

        return "redirect:/admin/team/list";
    }

    @PostMapping("/admin/team/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Long id) {
        this.teamMemberService.delete(id);
        return "redirect:/admin/team/list";
    }

    private String saveImage(MultipartFile imageFile, Long id) throws IOException {
        File directory = new File("uploads/images/teamMembers/" + id);

        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/images/teamMembers/" + id + "/");
            Files.createDirectories(uploadDir);
            assert fileName != null;
            Path imagePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/images/teamMembers/" + id + "/" + fileName;
        }
        return "/images/default.png";
    }
}
