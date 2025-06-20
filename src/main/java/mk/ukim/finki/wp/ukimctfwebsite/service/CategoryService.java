package mk.ukim.finki.wp.ukimctfwebsite.service;

import mk.ukim.finki.wp.ukimctfwebsite.model.Category;
import mk.ukim.finki.wp.ukimctfwebsite.model.Event;

import java.util.List;

public interface CategoryService {
    Category findById(Long id);

    List<Category> listAll();

    Category update(Long id, String name);

    Category create(String name);

    Category delete(Long id);

    List<Category> searchUnsafe(String query);
}
