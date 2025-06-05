package mk.ukim.finki.wp.ukimctfwebsite.repository;

import mk.ukim.finki.wp.ukimctfwebsite.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
