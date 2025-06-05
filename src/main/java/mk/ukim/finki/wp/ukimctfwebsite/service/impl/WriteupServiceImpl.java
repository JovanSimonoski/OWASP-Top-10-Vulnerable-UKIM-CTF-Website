package mk.ukim.finki.wp.ukimctfwebsite.service.impl;

import mk.ukim.finki.wp.ukimctfwebsite.model.*;
import mk.ukim.finki.wp.ukimctfwebsite.model.exceptions.WriteupNotFoundException;
import mk.ukim.finki.wp.ukimctfwebsite.repository.WriteupRepository;
import mk.ukim.finki.wp.ukimctfwebsite.service.CategoryService;
import mk.ukim.finki.wp.ukimctfwebsite.service.EventService;
import mk.ukim.finki.wp.ukimctfwebsite.service.TeamMemberService;
import mk.ukim.finki.wp.ukimctfwebsite.service.WriteupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;

import static mk.ukim.finki.wp.ukimctfwebsite.service.specifications.FieldFilterSpecification.*;

@Service
public class WriteupServiceImpl implements WriteupService {
    private final WriteupRepository writeupRepository;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final TeamMemberService teamMemberService;

    public WriteupServiceImpl(WriteupRepository writeupRepository, CategoryService categoryService, EventService eventService, TeamMemberService teamMemberService) {
        this.writeupRepository = writeupRepository;
        this.categoryService = categoryService;
        this.eventService = eventService;
        this.teamMemberService = teamMemberService;
    }

    @Override
    public Writeup findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return this.writeupRepository.findById(id).orElseThrow(() -> new WriteupNotFoundException(id));
    }

    @Override
    public List<Writeup> listAll() {
        return this.writeupRepository.findAll();
    }

    @Override
    public Writeup update(Long id, String title, String body, Date datePublished, Long categoryId, Long eventId, Long teamMemberAuthorId) {
        if (id == null ||
                title == null || title.isEmpty() ||
                body == null ||
                datePublished == null ||
                eventId == null ||
                teamMemberAuthorId == null) {
            throw new IllegalArgumentException();
        }

        Writeup writeup = this.findById(id);
        writeup.setTitle(title);
        writeup.setBody(body);
        writeup.setPublishedDate(datePublished);

        Category category = this.categoryService.findById(categoryId);
        writeup.setCategory(category);

        Event event = this.eventService.findById(eventId);
        writeup.setEvent(event);

        TeamMember teamMember = this.teamMemberService.findById(teamMemberAuthorId);
        writeup.setTeamMemberAuthor(teamMember);

        return this.writeupRepository.save(writeup);
    }

    @Override
    public Writeup updateBody(Long id, String body, Date datePublished) {
        if (id == null ||
                body == null ||
                datePublished == null) {
            throw new IllegalArgumentException();
        }

        Writeup writeup = this.findById(id);
        writeup.setBody(body);
        writeup.setPublishedDate(datePublished);

        return this.writeupRepository.save(writeup);
    }

    @Override
    public Writeup create(String title, String body, Date datePublished,Long categoryId, Long eventId, Long teamMemberAuthorId) {
        if (title == null || title.isEmpty() ||
                body == null ||
                datePublished == null ||
                eventId == null ||
                teamMemberAuthorId == null) {
            throw new IllegalArgumentException();
        }

        Category category = this.categoryService.findById(categoryId);
        Event event = this.eventService.findById(eventId);
        TeamMember teamMember = this.teamMemberService.findById(teamMemberAuthorId);

        return this.writeupRepository.save(new Writeup(title, body, datePublished, category, event, teamMember));
    }

    @Override
    public Writeup delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        Writeup writeup = this.findById(id);

        File directory = new File("uploads/images/writeups/" + writeup.getId());

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

        this.writeupRepository.deleteById(id);

        return writeup;
    }

    @Override
    public Page<Writeup> findPage(String title, Long categoryId, Long eventId, Long teamMemberAuthorId, int pageNum, int pageSize) {
        Specification<Writeup> specification = Specification
                .where(filterContainsText(Writeup.class, "title", title))
                .and(filterEquals(Writeup.class, "category.id", categoryId))
                .and(filterEquals(Writeup.class, "event.id", eventId))
                .and(filterEquals(Writeup.class, "teamMemberAuthor.id", teamMemberAuthorId));

        return this.writeupRepository.findAll(
                specification,
                PageRequest.of(pageNum, pageSize)
        );
    }
}
