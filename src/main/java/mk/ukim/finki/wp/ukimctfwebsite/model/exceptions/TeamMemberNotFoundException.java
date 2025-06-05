package mk.ukim.finki.wp.ukimctfwebsite.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TeamMemberNotFoundException extends RuntimeException {
    public TeamMemberNotFoundException(Long id) {
        super(
                String.format("[Exception] TeamMember with id %s not found.", id)
        );
    }
}
