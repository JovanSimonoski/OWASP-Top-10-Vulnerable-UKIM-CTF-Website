package mk.ukim.finki.wp.ukimctfwebsite.repository;

import mk.ukim.finki.wp.ukimctfwebsite.model.Writeup;
import org.springframework.stereotype.Repository;

@Repository
public interface WriteupRepository extends JpaSpecificationRepository<Writeup, Long> {
}
