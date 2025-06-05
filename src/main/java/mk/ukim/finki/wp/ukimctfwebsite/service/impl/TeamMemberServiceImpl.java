package mk.ukim.finki.wp.ukimctfwebsite.service.impl;

import mk.ukim.finki.wp.ukimctfwebsite.model.Category;
import mk.ukim.finki.wp.ukimctfwebsite.model.TeamMember;
import mk.ukim.finki.wp.ukimctfwebsite.model.exceptions.TeamMemberNotFoundException;
import mk.ukim.finki.wp.ukimctfwebsite.repository.TeamMemberRepository;
import mk.ukim.finki.wp.ukimctfwebsite.service.CategoryService;
import mk.ukim.finki.wp.ukimctfwebsite.service.TeamMemberService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final CategoryService categoryService;

    public TeamMemberServiceImpl(TeamMemberRepository teamMemberRepository, CategoryService categoryService) {
        this.teamMemberRepository = teamMemberRepository;
        this.categoryService = categoryService;
    }

    @Override
    public TeamMember findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return this.teamMemberRepository.findById(id).orElseThrow(() -> new TeamMemberNotFoundException(id));
    }

    @Override
    public List<TeamMember> listAll() {
        return this.teamMemberRepository.findAll();
    }

    @Override
    public TeamMember create(String nickname, String shortBiography, String githubLink, String imagePath, List<Long> categoryList) {
        if (nickname == null || nickname.isEmpty() ||
                shortBiography == null || shortBiography.isEmpty() ||
                githubLink == null || githubLink.isEmpty() ||
                imagePath == null || imagePath.isEmpty() ||
                categoryList == null || categoryList.isEmpty()) {
            throw new IllegalArgumentException();
        }

        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryList) {
            categories.add(this.categoryService.findById(categoryId));
        }

        return this.teamMemberRepository.save(new TeamMember(nickname, shortBiography, githubLink, imagePath, categories));
    }

    @Override
    public TeamMember update(Long id, String nickname, String shortBiography, String githubLink, String imagePath, List<Long> categoryList) {
        if (id == null ||
                nickname == null || nickname.isEmpty() ||
                shortBiography == null || shortBiography.isEmpty() ||
                githubLink == null || githubLink.isEmpty() ||
                imagePath == null || imagePath.isEmpty() ||
                categoryList == null || categoryList.isEmpty()) {
            throw new IllegalArgumentException();
        }

        TeamMember teamMember = this.findById(id);
        teamMember.setNickname(nickname);
        teamMember.setShortBiography(shortBiography);
        teamMember.setGithubLink(githubLink);
        teamMember.setImagePath(imagePath);

        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryList) {
            categories.add(this.categoryService.findById(categoryId));
        }
        teamMember.setCategoryList(categories);

        return this.teamMemberRepository.save(teamMember);
    }

    @Override
    public TeamMember updateImage(Long id, String imagePath) {
        if (id == null ||
                imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException();
        }

        TeamMember teamMember = this.findById(id);
        teamMember.setImagePath(imagePath);

        return this.teamMemberRepository.save(teamMember);
    }

    @Override
    public TeamMember delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        TeamMember teamMember = this.findById(id);

        teamMember.getCategoryList().clear();
        this.teamMemberRepository.save(teamMember);

        File directory = new File("uploads/images/teamMembers/" + id);

        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
            if (directory.delete()) {
                System.out.println("Directory deleted successfully.");
            } else {
                System.out.println("Failed to delete directory.");
            }
        }

        this.teamMemberRepository.deleteById(id);

        return teamMember;
    }
}
