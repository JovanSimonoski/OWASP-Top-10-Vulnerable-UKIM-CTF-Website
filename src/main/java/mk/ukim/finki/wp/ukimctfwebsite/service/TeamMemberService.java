package mk.ukim.finki.wp.ukimctfwebsite.service;

import mk.ukim.finki.wp.ukimctfwebsite.model.TeamMember;

import java.util.List;

public interface TeamMemberService {
    TeamMember findById(Long id);

    List<TeamMember> listAll();

    TeamMember create(String nickname, String shortBiography, String githubLink, String imagePath, List<Long> categoryList);

    TeamMember update(Long id, String nickname, String shortBiography, String githubLink, String imagePath, List<Long> categoryList);

    TeamMember updateImage(Long id, String imagePath);

    TeamMember delete(Long id);
}
