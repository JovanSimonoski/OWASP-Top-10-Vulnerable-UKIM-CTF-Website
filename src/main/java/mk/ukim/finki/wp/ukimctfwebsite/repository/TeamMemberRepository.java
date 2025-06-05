package mk.ukim.finki.wp.ukimctfwebsite.repository;

import mk.ukim.finki.wp.ukimctfwebsite.model.Category;
import mk.ukim.finki.wp.ukimctfwebsite.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByCategoryListContaining(Category category);
}
