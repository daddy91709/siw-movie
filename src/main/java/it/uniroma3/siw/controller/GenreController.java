package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.GenreRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.GenreValidator;
import jakarta.validation.Valid;

@Controller
public class GenreController {

    @Autowired
    GenreRepository genreRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    GenreValidator genreValidator;

    @GetMapping("/admin/formNewGenre")
    public String newGenre(Model model) {
        model.addAttribute("genre", new Genre());
        return "admin/formNewGenre.html";
    }

    @PostMapping("/admin/addGenre")
    public String addGenre(@Valid @ModelAttribute("genre") Genre genre, BindingResult bindingResult, Model model) {
        this.genreValidator.validate(genre, bindingResult);

        if (!bindingResult.hasErrors()) {
            this.genreRepository.save(genre);
            model.addAttribute("genre", genre);
            return "index.html";
        } else {
            return "admin/formNewGenre.html";
        }
    }

    @GetMapping("/genres")
    public String showGenres(Model model) {
        model.addAttribute("genres", this.genreRepository.findAll());
        return "genres.html";
    }

    @GetMapping("/admin/deleteGenre/{id}")
    public String deleteMovie(Model model, @PathVariable("id") Long id) {
        Genre genre = this.genreRepository.findById(id).get();

        for (Movie movie : genre.getMovies()) {
            movie.setDirector(null);
        }

        this.genreRepository.delete(genre);

        model.addAttribute("genres", this.genreRepository.findAll());
        return "admin/deleteGenres.html";
    }

    @GetMapping("/admin/deleteGenres")
    public String deleteGenres(Model model) {
        model.addAttribute("genres", this.genreRepository.findAll());
        return "admin/deleteGenres.html";
    }


}