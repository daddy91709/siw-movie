package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.ArtistValidator;
import jakarta.validation.Valid;

@Controller
public class ArtistController {

    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    ArtistValidator artistValidator;

    @GetMapping("/admin/formNewArtist")
    public String newArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "/admin/formNewArtist.html";
    }

    @PostMapping("/admin/addArtist")
    public String addArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult,
            MultipartFile image, Model model) {

        this.artistValidator.validate(artist, bindingResult);

        if (!bindingResult.hasErrors()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                artist.setImageString(base64Image);
            } catch (IOException e) {}

            this.artistRepository.save(artist);
            model.addAttribute("artist", artist);
            return "artist.html";
        } else {
            return "/admin/formNewArtist.html";
        }
    }

    @GetMapping("/artists")
    public String showArtists(Model model) {
        model.addAttribute("artists", this.artistRepository.findAll());
        return "artists.html";
    }

    @GetMapping("/artistsForMovie/{id}")
    public String showArtists(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artists", this.movieRepository.findById(id).get().getArtists());
        return "artists.html";
    }

    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistRepository.findById(id).get());
        return "artist.html";
    }

    @GetMapping("/admin/deleteArtist/{id}")
    public String deleteMovie(Model model, @PathVariable("id") Long id) {
        Artist artist = this.artistRepository.findById(id).get();

        for (Movie movie : artist.getDirectedMovies()) {
            movie.setDirector(null);
        }

        this.artistRepository.delete(artist);

        model.addAttribute("artists", this.artistRepository.findAll());
        return "admin/manageArtists.html";
    }

    @GetMapping("/admin/manageArtists")
    public String toManageArtists(Model model) {
        model.addAttribute("artists", this.artistRepository.findAll());
        return "admin/manageArtists.html";
    }

    @GetMapping("/admin/manageArtist/{id}")
    public String manageArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistRepository.findById(id).get());
        return "admin/manageArtist.html";
    }

    @GetMapping("/admin/editArtist/{id}")
    public String editArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistRepository.findById(id).get());
        return "admin/formEditArtist.html";
    }

    @PostMapping("/admin/editArtist/{id}")
    public String saveArtistChanges(@PathVariable("id") Long id, @Valid @ModelAttribute Artist newartist,
            BindingResult bindingResult, MultipartFile image, Model model) {

        this.artistValidator.validate(newartist, bindingResult);

        if (!bindingResult.hasErrors()) {
            Artist artist = this.artistRepository.findById(id).get();

            artist.setName(newartist.getName());
            artist.setSurname(newartist.getSurname());
            artist.setBirthDate(newartist.getBirthDate());
            artist.setDeathDate(newartist.getDeathDate());
            
            try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                artist.setImageString(base64Image);
            } catch (IOException e) {}

            this.artistRepository.save(artist);

            model.addAttribute("artist", artist);

            return "/admin/manageArtist.html";
        } else {
            model.addAttribute("artist", this.artistRepository.findById(id).get());
            return "/admin/formEditArtist.html";
        }
    }
}