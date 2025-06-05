package mk.ukim.finki.wp.ukimctfwebsite.web.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = 500;
        String message = "An unexpected error occurred";

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());

            if (statusCode == 404) {
                message = "Page not found";
            } else if (statusCode == 403) {
                message = "Access forbidden";
            } else if (statusCode == 500) {
                message = "Internal server error";
            }
        }

        if (errorMessage != null) {
            message = errorMessage.toString();
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("message", message);
        model.addAttribute("bodyContent", "error/error");

        return "master-template";
    }
}