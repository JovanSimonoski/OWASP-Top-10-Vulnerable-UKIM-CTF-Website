package mk.ukim.finki.wp.ukimctfwebsite.service.impl;

import mk.ukim.finki.wp.ukimctfwebsite.model.Category;
import mk.ukim.finki.wp.ukimctfwebsite.model.TeamMember;
import mk.ukim.finki.wp.ukimctfwebsite.model.exceptions.CategoryNotFoundException;
import mk.ukim.finki.wp.ukimctfwebsite.repository.CategoryRepository;
import mk.ukim.finki.wp.ukimctfwebsite.repository.TeamMemberRepository;
import mk.ukim.finki.wp.ukimctfwebsite.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final TeamMemberRepository teamMemberRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, TeamMemberRepository teamMemberRepository) {
        this.categoryRepository = categoryRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public Category findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public List<Category> listAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category update(Long id, String name) {
        if (id == null || name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Category category = this.findById(id);
        category.setName(name);

        return this.categoryRepository.save(category);
    }

    @Override
    public Category create(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return this.categoryRepository.save(new Category(name));
    }

    @Override
    public Category delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        Category category = this.findById(id);

        List<TeamMember> teamMembers = this.teamMemberRepository.findByCategoryListContaining(category);

        for (TeamMember member : teamMembers) {
            member.getCategoryList().remove(category);
            this.teamMemberRepository.save(member);
        }

        this.categoryRepository.deleteById(id);

        return category;
    }
}
