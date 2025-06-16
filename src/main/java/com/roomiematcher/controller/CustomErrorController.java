package com.roomiematcher.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get error status code
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        // Add error details to the model
        model.addAttribute("status", status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", exception != null ? exception.toString() : "Unknown error");
        model.addAttribute("message", message != null ? message : "An unexpected error occurred");
        model.addAttribute("path", path != null ? path : request.getRequestURI());
        
        // Log the error
        System.err.println("Error occurred: Status=" + status + ", Path=" + path + ", Message=" + message);
        
        if (exception != null) {
            System.err.println("Exception: " + exception);
        }
        
        // Return the error view
        return "error";
    }
} 