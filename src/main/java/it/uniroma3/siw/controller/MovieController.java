package it.uniroma3.siw.controller;

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

	
	@GetMapping("/admin/manageMovies")
	public String managemovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "/admin/manageMovies.html";
	}
	
	@GetMapping("/admin/manageMovies/{id}")
	public String updateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return "/admin/manageMovie.html";
	}
		
  
  @GetMapping("/admin/formNewMovie")
    public String formNewMovie(Model model) {
    model.addAttribute("movie", new Movie());
    return "/admin/formNewMovie.html";
  }
  
  @GetMapping("/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		model.addAttribute("directors", this.artistRepository.findAll());
		return "/admin/addDirector.html";
	}
	
	@GetMapping("/admin/setDirector/{idMovie}/{idDirector}")
	public String setDirector(@PathVariable("idMovie") Long idMovie, @PathVariable("idDirector") Long idDirector, Model model) {
		Movie movie= this.movieRepository.findById(idMovie).get();
		Artist director= this.artistRepository.findById(idDirector).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);
		model.addAttribute("movie", movie);
		return "/admin/manageMovie.html";
	}
	
	  @GetMapping("/admin/manageActors/{id}")
		public String manageActors(@PathVariable("id") Long id, Model model) {
		  Movie movie= this.movieRepository.findById(id).get();
			model.addAttribute("movie",movie);
			model.addAttribute("artists", this.artistRepository.getArtistByMoviesNotContains(movie));
			return "/admin/manageActors.html";
		}
	  
	  @GetMapping("/admin/addActor/{idArtist}/{idMovie}")
	  public String addActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie, Model model){
		  Movie movie= this.movieRepository.findById(idMovie).get();
		  Artist artist= this.artistRepository.findById(idArtist).get();
		  
		  movie.getArtists().add(artist);
		  this.movieRepository.save(movie);
		  
		  artist.getMovies().add(movie);
		  this.artistRepository.save(artist);
		  
		  model.addAttribute("movie",movie);
		  model.addAttribute("artists",this.artistRepository.getArtistByMoviesNotContains(movie));
		  return "/admin/manageActors.html"; 
	  }
	  
	  @GetMapping("/admin/removeActor/{idArtist}/{idMovie}")
	  public String removeActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie, Model model){
		  Movie movie= this.movieRepository.findById(idMovie).get();
		  Artist artist= this.artistRepository.findById(idArtist).get();
		  
		  movie.getArtists().remove(artist);
		  this.movieRepository.save(movie);
		  
		  artist.getMovies().remove(movie);
		  this.artistRepository.save(artist);
		  
		  model.addAttribute("movie",movie);
		  model.addAttribute("artists",this.artistRepository.getArtistByMoviesNotContains(movie));
		  return "/admin/manageActors.html"; 
	  }

  @PostMapping("/admin/movies")
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
      return "/admin/formNewMovie.html";
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
    model.addAttribute("movies", this.movieRepository.findAllByYear(year));
    return "foundMovies.html";
  }
}
