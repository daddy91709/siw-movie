package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.MovieValidator;
import jakarta.validation.Valid;

@Controller 
public class MovieController {
  @Autowired MovieRepository movieRepository;
  @Autowired MovieValidator movieValidator;
  @Autowired ArtistRepository artistRepository;

	
	@GetMapping("/manageMovies")
	public String managemovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "manageMovies.html";
	}
	
	@GetMapping("/manageMovies/{id}")
	public String updateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return "manageMovie.html";
	}
		
  
  @GetMapping("/formNewMovie")
    public String formNewMovie(Model model) {
    model.addAttribute("movie", new Movie());
    return "formNewMovie.html";
  }
  
  @GetMapping("/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		model.addAttribute("directors", this.artistRepository.findAll());
		return "addDirector.html";
	}
	
	@GetMapping("/setDirector/{idMovie}/{idDirector}")
	public String setDirector(@PathVariable("idMovie") Long idMovie, @PathVariable("idDirector") Long idDirector, Model model) {
		Movie movie= this.movieRepository.findById(idMovie).get();
		Artist director= this.artistRepository.findById(idDirector).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);
		model.addAttribute("movie", movie);
		return "manageMovie.html";
	}
	
	  @GetMapping("/manageActors/{id}")
		public String manageActors(@PathVariable("id") Long id, Model model) {
		  Movie movie= this.movieRepository.findById(id).get();
			model.addAttribute("movie",movie);
			model.addAttribute("artists", this.artistRepository.getArtistByMoviesNotContains(movie));
			return "manageActors.html";
		}
	  
	  @GetMapping("/addActor/{idArtist}/{idMovie}")
	  public String addActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie, Model model){
		  Movie movie= this.movieRepository.findById(idMovie).get();
		  Artist artist= this.artistRepository.findById(idArtist).get();
		  
		  movie.getArtists().add(artist);
		  this.movieRepository.save(movie);
		  
		  artist.getMovies().add(movie);
		  this.artistRepository.save(artist);
		  
		  model.addAttribute("movie",movie);
		  model.addAttribute("artists",this.artistRepository.getArtistByMoviesNotContains(movie));
		  return "manageActors.html"; 
	  }
	  
	  @GetMapping("/removeActor/{idArtist}/{idMovie}")
	  public String removeActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie, Model model){
		  Movie movie= this.movieRepository.findById(idMovie).get();
		  Artist artist= this.artistRepository.findById(idArtist).get();
		  
		  movie.getArtists().remove(artist);
		  this.movieRepository.save(movie);
		  
		  artist.getMovies().remove(movie);
		  this.artistRepository.save(artist);
		  
		  model.addAttribute("movie",movie);
		  model.addAttribute("artists",this.artistRepository.getArtistByMoviesNotContains(movie));
		  return "manageActors.html"; 
	  }

  @PostMapping("/movies")
  public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
	  this.movieValidator.validate(movie,bindingResult);
	  if(!bindingResult.hasErrors())
	  {
      this.movieRepository.save(movie);
      model.addAttribute("movie", movie);
      return "movie.html";
    } 
    else
    {
      return "formNewMovie.html";
    }
  }
  
  @GetMapping("/movies/{id}")
  public String getMovie(@PathVariable("id") Long id, Model model) {
    model.addAttribute("movie", this.movieRepository.findById(id).get());
    return "movie.html";
  }

  @GetMapping("/movies")
  public String showMovies(Model model) {
    model.addAttribute("movies", this.movieRepository.findAll());
    return "movies.html";
  }

  @GetMapping("/formSearchMovies")
  public String formSearchMovies() {
    return "formSearchMovies.html";
  }

  @GetMapping("/searchMovies")
  public String searchMovies(Model model, @RequestParam Integer year) {
    model.addAttribute("movies", this.movieRepository.findByYear(year));
    return "foundMovies.html";
  }
  
  @GetMapping("/index")
  public String toIndex() {
    return "index.html";
  }
}
