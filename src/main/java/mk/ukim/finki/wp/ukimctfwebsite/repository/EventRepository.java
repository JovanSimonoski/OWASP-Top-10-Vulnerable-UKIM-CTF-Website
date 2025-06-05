package mk.ukim.finki.wp.ukimctfwebsite.repository;

import mk.ukim.finki.wp.ukimctfwebsite.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
