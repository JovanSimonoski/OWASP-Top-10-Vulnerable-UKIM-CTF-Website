package mk.ukim.finki.wp.ukimctfwebsite.service;

import mk.ukim.finki.wp.ukimctfwebsite.model.Event;

import java.util.Date;
import java.util.List;

public interface EventService {
    Event findById(Long id);

    List<Event> listAll();

    Event update(Long id, String title, Date startDate, Date endDate, String url);

    Event create(String title, Date startDate, Date endDate, String url);

    Event delete(Long id);

    List<Event> searchUnsafe(String query);
}
