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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.validator.MovieValidator;
import jakarta.validation.Valid;

@Controller
public class MovieController {
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	MovieValidator movieValidator;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ReviewRepository reviewRepository;

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
	public String setDirector(@PathVariable("idMovie") Long idMovie, @PathVariable("idDirector") Long idDirector,
			Model model) {
		Movie movie = this.movieRepository.findById(idMovie).get();
		Artist director = this.artistRepository.findById(idDirector).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);
		model.addAttribute("movie", movie);
		return "/admin/manageMovie.html";
	}

	@GetMapping("/admin/manageActors/{id}")
	public String manageActors(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		model.addAttribute("artists", this.artistRepository.getArtistByMoviesNotContains(movie));
		return "/admin/manageActors.html";
	}

	@GetMapping("/admin/addActor/{idArtist}/{idMovie}")
	public String addActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie,
			Model model) {
		Movie movie = this.movieRepository.findById(idMovie).get();
		Artist artist = this.artistRepository.findById(idArtist).get();

		movie.getArtists().add(artist);
		this.movieRepository.save(movie);

		artist.getMovies().add(movie);
		this.artistRepository.save(artist);

		model.addAttribute("movie", movie);
		model.addAttribute("artists", this.artistRepository.getArtistByMoviesNotContains(movie));
		return "/admin/manageActors.html";
	}

	@GetMapping("/admin/removeActor/{idArtist}/{idMovie}")
	public String removeActor(@PathVariable("idArtist") Long idArtist, @PathVariable("idMovie") Long idMovie,
			Model model) {
		Movie movie = this.movieRepository.findById(idMovie).get();
		Artist artist = this.artistRepository.findById(idArtist).get();

		movie.getArtists().remove(artist);
		this.movieRepository.save(movie);

		artist.getMovies().remove(movie);
		this.artistRepository.save(artist);

		model.addAttribute("movie", movie);
		model.addAttribute("artists", this.artistRepository.getArtistByMoviesNotContains(movie));
		return "/admin/manageActors.html";
	}

	@PostMapping("/admin/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, MultipartFile image, Model model) {
		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {

			try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                movie.setImageString(base64Image);
            } catch (IOException e) {}

			this.movieRepository.save(movie);
			model.addAttribute("movie", movie);
			return "/admin/manageMovie.html";
		} else {
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
		return "movies.html";
	}

	@GetMapping("/admin/deleteMovie/{id}")
	public String deleteMovie(Model model, @PathVariable("id") Long id) {
		Movie movie = this.movieRepository.findById(id).get();

		for (Artist artist : movie.getArtists()) {
			artist.getMovies().remove(movie);
		}

		this.movieRepository.delete(movie);

		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/manageMovies.html";
	}

	@GetMapping("/admin/editMovie/{id}")
	public String editMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return "admin/formEditMovie.html";
	}

	@PostMapping("/admin/editMovie/{id}")
	public String saveMovieChanges(@PathVariable("id") Long id, @Valid @ModelAttribute Movie newmovie,
			BindingResult bindingResult, MultipartFile image, Model model) {

		this.movieValidator.validate(newmovie, bindingResult);

		if (!bindingResult.hasErrors()) {
			Movie movie = this.movieRepository.findById(id).get();

			movie.setTitle(newmovie.getTitle());
			movie.setYear(newmovie.getYear());

			try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                movie.setImageString(base64Image);
            } catch (IOException e) {}

			this.movieRepository.save(movie);

			model.addAttribute("movie", movie);

			return "/admin/manageMovie.html";
		} else {
			model.addAttribute("movie", this.movieRepository.findById(id).get());
			return "/admin/formEditMovie.html";
		}
	}
}
