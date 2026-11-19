package org.example.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// In production the backend also delivers the React frontend (from resources/static).
// The pages of the frontend, e.g. /trips/123, exist only in React Router, not as files.
// When the user opens such a page directly or reloads it, this controller sends index.html,
// and React Router then shows the right page.
@Controller
public class FrontendController {

    // The same paths as in frontend/src/App.tsx and navigation.tsx; a new page there needs its path here too
    @GetMapping({"/", "/login", "/register", "/trips", "/trips/{id}", "/map", "/wishlist"})
    public String showFrontend() {
        return "forward:/index.html";
    }
}
