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
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;

@Controller
public class ArtistController {

    @Autowired
    ArtistService artistService;
    @Autowired
    MovieService movieService;

    @GetMapping("/admin/formNewArtist")
    public String newArtist(Model model) {
        model.addAttribute("artist", new Artist());
        return "/admin/formNewArtist.html";
    }

    @PostMapping("/admin/addArtist")
    public String addArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult,
            MultipartFile image, Model model) {
        try{
           model.addAttribute("artist", this.artistService.createArtist(artist, bindingResult, image));
            return "artist.html";
        }
        catch(IOException e) {
            return "/admin/formNewArtist.html";
        }
    }

    @GetMapping("/artists")
    public String showArtists(Model model) {
        model.addAttribute("artists", this.artistService.getAllArtists());
        return "artists.html";
    }

    @GetMapping("/artistsForMovie/{id}")
    public String showArtists(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artists", this.movieService.getMovieById(id).getArtists());
        return "artists.html";
    }

    @GetMapping("/artists/{id}")
    public String getArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistService.getArtistById(id));
        return "artist.html";
    }

    @GetMapping("/admin/deleteArtist/{id}")
    public String deleteMovie(Model model, @PathVariable("id") Long id) {
        model.addAttribute("artists", this.artistService.deleteArtistByIdAndReturnAll(id));
        return "admin/manageArtists.html";
    }

    @GetMapping("/admin/manageArtists")
    public String toManageArtists(Model model) {
        model.addAttribute("artists", this.artistService.getAllArtists());
        return "admin/manageArtists.html";
    }

    @GetMapping("/admin/manageArtist/{id}")
    public String manageArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistService.getArtistById(id));
        return "admin/manageArtist.html";
    }

    @GetMapping("/admin/editArtist/{id}")
    public String editArtist(@PathVariable("id") Long id, Model model) {
        model.addAttribute("artist", this.artistService.getArtistById(id));
        return "admin/formEditArtist.html";
    }

    @PostMapping("/admin/editArtist/{id}")
    public String saveArtistChanges(@PathVariable("id") Long id, @Valid @ModelAttribute Artist newartist,
            BindingResult bindingResult, MultipartFile image, Model model) {

        try{
            model.addAttribute("artist", this.artistService.editArtist(id, newartist, bindingResult, image));
            return "/admin/manageArtist.html";
        } 
        catch(IOException e) {
            model.addAttribute("artist", this.artistService.getArtistById(id));
            return "/admin/formEditArtist.html";
        }
    }
}