package mk.ukim.finki.wp.ukimctfwebsite.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Writeup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    private Date publishedDate;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Event event;

    @ManyToOne
    private TeamMember teamMemberAuthor;

    public Writeup(String title, String body, Date publishedDate, Category category, Event event, TeamMember teamMemberAuthor) {
        this.title = title;
        this.body = body;
        this.publishedDate = publishedDate;
        this.category = category;
        this.event = event;
        this.teamMemberAuthor = teamMemberAuthor;
    }
}
