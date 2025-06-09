package mk.ukim.finki.wp.ukimctfwebsite.service.impl;

import mk.ukim.finki.wp.ukimctfwebsite.model.Event;
import mk.ukim.finki.wp.ukimctfwebsite.model.exceptions.EventNotFoundException;
import mk.ukim.finki.wp.ukimctfwebsite.repository.EventRepository;
import mk.ukim.finki.wp.ukimctfwebsite.service.EventService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        return this.eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    @Override
    public List<Event> listAll() {
        return this.eventRepository.findAll();
    }

    @Override
    public Event update(Long id, String title, Date startDate, Date endDate, String url) {
        if (id == null ||
                title == null || title.isEmpty() ||
                startDate == null || endDate == null || startDate.after(endDate) ||
                url == null || url.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Event event = findById(id);
        event.setTitle(title);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setUrl(url);

        return this.eventRepository.save(event);
    }

    @Override
    public Event create(String title, Date startDate, Date endDate, String url) {
        if (title == null || title.isEmpty() ||
                startDate == null || endDate == null || startDate.after(endDate) ||
                url == null || url.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return this.eventRepository.save(new Event(title, startDate, endDate, url));
    }

    @Override
    public Event delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        Event event = findById(id);
        this.eventRepository.deleteById(id);

        return event;
    }

    @Override
    public List<Event> searchUnsafe(String query) {
        String sql = "SELECT * FROM event WHERE title LIKE '%" + query + "%'";
        return entityManager.createNativeQuery(sql, Event.class).getResultList();
    }

}
