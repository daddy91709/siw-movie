package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.authentication.AuthConfiguration;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validator.MovieValidator;
import it.uniroma3.siw.validator.ReviewValidator;
import jakarta.validation.Valid;

@Controller
public class ReviewController {
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	MovieValidator movieValidator;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ReviewValidator reviewValidator;
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	CredentialsService credentialsService;
	@Autowired
	UserRepository userRepository;

	@GetMapping("/user/reviewFilm/{id}")
	public String formNewMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		model.addAttribute("review", new Review());
		return "/user/formNewReview.html";
	}

	@PostMapping("/user/addReviewTo/{id}")
	public String newMovie(@PathVariable("id") Long id, @Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult, Model model) {
				
		Movie movie = this.movieRepository.findById(id).get();

		//Collegamenti uscenti di review
		review.setUser(AuthConfiguration.getCurrentUser(credentialsService));
        review.setMovie(movie);

		//Controllo se tutti i valori sono stati inseriti correttamente e che non ci sia già una recensione per quell'utente
		this.reviewValidator.validate(review, bindingResult);
	
		if (!bindingResult.hasErrors()) {

			//Collegamenti entranti di review
			movie.getReviews().add(review);
			review.getUser().getReview().add(review);
			
			this.reviewRepository.save(review);
			this.movieRepository.save(movie);
			this.userRepository.save(review.getUser());

			model.addAttribute("movie", movie);

			return "movie.html";
		} else {
			model.addAttribute("movie", movie);
			return "/user/formNewReview.html";
		}
	}

	@GetMapping("/review/{id}")
	public String showReview(@PathVariable("id") Long id, Model model){
		Review review = this.reviewRepository.findById(id).get();
		model.addAttribute("review", review);
		model.addAttribute("movie", review.getMovie());
		return "review.html";
	}

	@GetMapping("admin/manageReview/{id}")
	public String manageReview(@PathVariable("id") Long id, Model model){
		Review review = this.reviewRepository.findById(id).get();
		model.addAttribute("review", review);
		model.addAttribute("movie", review.getMovie());
		return "admin/manageReview.html";
	}

	@GetMapping("admin/deleteReview/{id}")
	public String deleteReview(@PathVariable("id") Long id, Model model){
		Review review = this.reviewRepository.findById(id).get();
		review.getMovie().getReviews().remove(review);

		model.addAttribute("movie", review.getMovie());

		this.reviewRepository.delete(review);
		return "admin/manageMovie.html";
	}
}
