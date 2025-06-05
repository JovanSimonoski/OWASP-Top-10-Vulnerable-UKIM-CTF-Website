package mk.ukim.finki.wp.ukimctfwebsite.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class WriteupNotFoundException extends RuntimeException {
    public WriteupNotFoundException(Long id) {
        super(
                String.format("[Exception] Writeup with id %s not found.", id)
        );
    }
}
