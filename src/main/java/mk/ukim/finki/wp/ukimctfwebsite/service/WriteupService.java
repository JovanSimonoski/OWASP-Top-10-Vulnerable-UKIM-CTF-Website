package mk.ukim.finki.wp.ukimctfwebsite.service;

import mk.ukim.finki.wp.ukimctfwebsite.model.*;
import org.springframework.data.domain.Page;


import java.util.Date;
import java.util.List;

public interface WriteupService {
    Writeup findById(Long id);

    List<Writeup> listAll();

    Writeup update(Long id, String title, String body, Date datePublished, Long categoryId, Long eventId, Long teamMemberAuthorId);

    Writeup updateBody(Long id, String body, Date datePublished);

    Writeup create(String title, String body, Date datePublished, Long categoryId, Long eventId, Long teamMemberAuthorId);

    Writeup delete(Long id);

    Page<Writeup> findPage(String title, Long categoryId, Long eventId, Long teamMemberAuthorId, int pageNum, int pageSize);
}
