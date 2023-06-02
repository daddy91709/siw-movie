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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.GenreService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.MovieValidator;
import jakarta.validation.Valid;

@Controller
public class MovieController {
	@Autowired
	MovieService movieService;
	@Autowired
	MovieValidator movieValidator;
	@Autowired
	ArtistService artistService;
	@Autowired
	GenreService genreService;
	@Autowired
	ReviewService reviewService;

	@GetMapping("/admin/manageMovies")
	public String managemovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "/admin/manageMovies.html";
	}

	@GetMapping("/admin/manageMovies/{id}")
	public String updateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		return "/admin/manageMovie.html";
	}

	@GetMapping("/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "/admin/formNewMovie.html";
	}

	@GetMapping("/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		model.addAttribute("directors", this.artistService.getAllArtists());
		return "/admin/addDirector.html";
	}

	@GetMapping("/admin/setDirector/{idMovie}/{idDirector}")
	public String setDirector(@PathVariable("idMovie") Long idMovie, @PathVariable("idDirector") Long idDirector,
			Model model) {
		model.addAttribute("movie", this.movieService.addDirectorTo(idMovie, idDirector));
		return "/admin/manageMovie.html";
	}

	@GetMapping("/admin/manageActors/{id}")
	public String manageActors(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		model.addAttribute("artists", this.artistService.getArtistsNotInMovieById(id));
		return "/admin/manageActors.html";
	}

	@GetMapping("/admin/addActor/{idArtist}/{idMovie}")
	public String addActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie,
			Model model) {

		model.addAttribute("movie", this.movieService.addActorTo(idMovie, idArtist));
		model.addAttribute("artists", this.artistService.getArtistsNotInMovieById(idMovie));
		return "/admin/manageActors.html";
	}

	@GetMapping("/admin/removeActor/{idArtist}/{idMovie}")
	public String removeActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie,
			Model model) {

		model.addAttribute("movie", this.movieService.removeActorFrom(idMovie, idArtist));
		model.addAttribute("artists", this.artistService.getArtistsNotInMovieById(idMovie));
		return "/admin/manageActors.html";
	}

	@PostMapping("/admin/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult,
			MultipartFile image, Model model) {
		try{
			model.addAttribute("movie", movieService.createMovie(movie, bindingResult, image));
			return "/admin/manageMovie.html";
		}
		catch(IOException e){
			return "/admin/formNewMovie.html";
		}
	}

	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		model.addAttribute("rating", this.reviewService.getMovieRatingById(id));
		return "movie.html";
	}

	@GetMapping("/movies")
	public String showMovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "movies.html";
	}

	@GetMapping("/searchMoviesByYear")
	public String searchMoviesByYear(Model model, @RequestParam Integer year) {
		model.addAttribute("movies", this.movieService.getMoviesByYear(year));
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "movies.html";
	}

	@GetMapping("/manageMoviesByGenre")
	public String manageMoviesByGenre(Model model, @RequestParam Long genreId) {
		model.addAttribute("movies", this.movieService.getMoviesByGenre(this.genreService.getGenreById(genreId)));
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "admin/manageMovies.html";
	}

	@GetMapping("/manageMoviesByYear")
	public String manageMoviesByYear(Model model, @RequestParam Integer year) {
		model.addAttribute("movies", this.movieService.getMoviesByYear(year));
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "admin/manageMovies.html";
	}

	@GetMapping("/searchMoviesByGenre")
	public String searchMoviesByGenre(Model model, @RequestParam Long genreId) {
		model.addAttribute("movies", this.movieService.getMoviesByGenre(this.genreService.getGenreById(genreId)));
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "movies.html";
	}

	@GetMapping("/getMoviesByGenre/{id}")
	public String getMoviesByGenre(Model model, @PathVariable("id") Long id) {
		model.addAttribute("movies", this.movieService.getMoviesByGenre(this.genreService.getGenreById(id)));
		model.addAttribute("genres", this.genreService.getAllGenres());
		return "movies.html";
	}

	@GetMapping("/admin/deleteMovie/{id}")
	public String deleteMovie(Model model, @PathVariable("id") Long id) {
		model.addAttribute("movies", this.movieService.deleteMovieByIdAndReturnAll(id));
		return "admin/manageMovies.html";
	}

	@GetMapping("/admin/editMovie/{id}")
	public String editMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		return "admin/formEditMovie.html";
	}

	@PostMapping("/admin/editMovie/{id}")
	public String saveMovieChanges(@PathVariable("id") Long id, @Valid @ModelAttribute Movie newmovie,
			BindingResult bindingResult, MultipartFile image, Model model) {

		try{
			model.addAttribute("movie", this.movieService.editMovie(id, newmovie, bindingResult, image));
			return "admin/manageMovie.html";
		}
		catch (IOException e){
			model.addAttribute("movie", this.movieService.getMovieById(id));
			return "admin/formEditMovie.html";
		}
	}

	@GetMapping("/admin/manageGenres/{id}")
	public String manageGenres(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		model.addAttribute("genres", this.genreService.getGenresNotInMovieById(id));
		return "/admin/manageGenres.html";
	}

	@GetMapping("/admin/addGenre/{idGenre}/{idMovie}")
	public String addGenre(@PathVariable("idGenre") Long idGenre, @PathVariable("idMovie") Long idMovie,
			Model model) {

		model.addAttribute("movie", this.movieService.addGenreTo(idMovie, idGenre));
		model.addAttribute("genres", this.genreService.getGenresNotInMovieById(idMovie));
		return "/admin/manageGenres.html";
	}

	@GetMapping("/admin/removeGenre/{idGenre}/{idMovie}")
	public String removeGenre(@PathVariable("idGenre") Long idGenre, @PathVariable("idMovie") Long idMovie,
			Model model) {

		model.addAttribute("movie", this.movieService.removeGenreFrom(idMovie, idGenre));
		model.addAttribute("genres", this.genreService.getGenresNotInMovieById(idMovie));
		return "/admin/manageGenres.html";
	}

}
