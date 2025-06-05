package mk.ukim.finki.wp.ukimctfwebsite.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;
    private String shortBiography;
    private String githubLink;
    private String imagePath;

    @ManyToMany
    private List<Category> categoryList;

    public TeamMember(String nickname, String shortBiography, String githubLink, String imagePath, List<Category> categoryList) {
        this.nickname = nickname;
        this.shortBiography = shortBiography;
        this.githubLink = githubLink;
        this.imagePath = imagePath;
        this.categoryList = categoryList;
    }
}
