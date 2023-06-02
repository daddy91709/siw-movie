package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.service.GenreService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;

@Controller
public class GenreController {

    @Autowired
    GenreService genreService;
    @Autowired
    MovieService movieService;

    @GetMapping("/admin/formNewGenre")
    public String newGenre(Model model) {
        model.addAttribute("genre", new Genre());
        return "admin/formNewGenre.html";
    }

    @PostMapping("/admin/addGenre")
    public String addGenre(@Valid @ModelAttribute("genre") Genre genre, BindingResult bindingResult, Model model) {
        try {
            model.addAttribute("genres", this.genreService.addGenreAndReturnAll(genre, bindingResult));
            return "index.html";
        }
        catch(IOException e) {
            return "admin/formNewGenre.html";
        }
    }

    @GetMapping("/genres")
    public String showGenres(Model model) {
        model.addAttribute("genres", this.genreService.getAllGenres());
        return "genres.html";
    }

    @GetMapping("/admin/deleteGenre/{id}")
    public String deleteMovie(Model model, @PathVariable("id") Long id) {
        model.addAttribute("genres", this.genreService.deleteGenreByIdAndReturnAll(id));
        return "admin/deleteGenres.html";
    }

    @GetMapping("/admin/deleteGenres")
    public String deleteGenres(Model model) {
        model.addAttribute("genres", this.genreService.getAllGenres());
        return "admin/deleteGenres.html";
    }


}