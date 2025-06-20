package mk.ukim.finki.wp.ukimctfwebsite.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long id) {
        super(
                String.format("[Exception] Event with id %s not found.", id)
        );
    }
}
