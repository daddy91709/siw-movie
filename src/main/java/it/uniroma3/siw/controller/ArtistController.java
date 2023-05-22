package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Controller
public class ArtistController {

    @Autowired ArtistRepository artistRepository;
    @Autowired MovieRepository movieRepository;

    @GetMapping("/formNewArtist")
    public String newArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "formNewArtist.html";
    }

    @PostMapping("/addartist")
    public String addArtist(@ModelAttribute("artist") Artist artist, Model model) {
        if (!artistRepository.existsByNameAndSurname(artist.getName(), artist.getSurname())) {
            this.artistRepository.save(artist);
            model.addAttribute("artist", artist);
            return "artist.html";
        } else {
            model.addAttribute("messaggioErrore", "Questo artista esiste già");
            return "formNewArtist.html";
        }
    }

    @GetMapping("/artists")
    public String showArtists(Model model) {
        model.addAttribute("artists", this.artistRepository.findAll());
        return "artists.html";
    }
    
    @GetMapping("/artistsForMovie/{id}")
    public String showArtists(@PathVariable("id") Long id,Model model) {
        model.addAttribute("artists", this.movieRepository.findById(id).get().getArtists());
        return "artists.html";
    }

    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistRepository.findById(id).get());
        return "artist.html";
    }

}