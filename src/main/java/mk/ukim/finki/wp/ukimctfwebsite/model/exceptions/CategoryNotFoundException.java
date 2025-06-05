package mk.ukim.finki.wp.ukimctfwebsite.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super(
                String.format("[Exception] Category with id %s not found.", id)
        );
    }
}
