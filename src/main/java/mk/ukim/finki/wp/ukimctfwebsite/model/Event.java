package mk.ukim.finki.wp.ukimctfwebsite.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Date startDate;
    private Date endDate;
    private String url;

    public Event(String title, Date startDate, Date endDate, String url) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.url = url;
    }
}
