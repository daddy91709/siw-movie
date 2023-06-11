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

import it.uniroma3.siw.authentication.AuthConfiguration;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.ReviewValidator;
import jakarta.validation.Valid;

@Controller
public class ReviewController {
	@Autowired
	MovieService movieService;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ReviewValidator reviewValidator;
	@Autowired
	ReviewService reviewService;
	@Autowired
	CredentialsService credentialsService;
	@Autowired
	UserRepository userRepository;

	@GetMapping("/user/reviewFilm/{id}")
	public String formNewReview(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.getMovieById(id));
		model.addAttribute("review", new Review());
		return "user/formNewReview.html";
	}

	@PostMapping("/user/addReviewTo/{id}")
	public String newReview(@PathVariable("id") Long id, @Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult, Model model) {
		try{
			model.addAttribute("movie",this.reviewService.createReviewForMovie(id, review, bindingResult));
			return "movie.html";
		}
		catch(IOException e) {
			model.addAttribute("movie", this.movieService.getMovieById(id));
			return "user/formNewReview.html";
		}
	}

	@GetMapping("/review/{id}")
	public String showReview(@PathVariable("id") Long id, Model model){
		model.addAttribute("review", this.reviewService.getReviewById(id));
		model.addAttribute("movie", this.reviewService.getReviewById(id).getMovie());
		return "review.html";
	}

	@GetMapping("admin/manageReview/{id}")
	public String manageReview(@PathVariable("id") Long id, Model model){
		model.addAttribute("review", this.reviewService.getReviewById(id));
		model.addAttribute("movie", this.reviewService.getReviewById(id).getMovie());
		return "admin/manageReview.html";
	}

	@GetMapping("admin/deleteReview/{id}")
	public String deleteReview(@PathVariable("id") Long id, Model model){
		model.addAttribute("movie", this.reviewService.deleteReviewByIdAndReturnMovie(id));
		return "admin/manageMovie.html";
	}
	
	@GetMapping("/user/editReviews")
	public String editReviewsPage(Model model){
		model.addAttribute("user", AuthConfiguration.getCurrentUser(credentialsService));
		return "user/editReviews";
	}

	@GetMapping("/user/editReview/{reviewId}")
	public String editReview(@PathVariable("reviewId") Long reviewId, Model model) {
		model.addAttribute("review", this.reviewService.getReviewById(reviewId));
		model.addAttribute("movie", this.reviewService.getReviewById(reviewId).getMovie());
		return "user/formEditReview.html";
	}

	@PostMapping("/user/editReview/{id}")
	public String saveReviewChanges(@PathVariable("id") Long id, @Valid @ModelAttribute Review newreview,
			BindingResult bindingResult, Model model) {
		try {
			model.addAttribute("user", this.reviewService.editReviewForMovie(id, newreview, bindingResult));
			return "user/editReviews.html";
		}
		catch(IOException e){
			model.addAttribute("movie", this.reviewService.getReviewById(id).getMovie());
			return "user/formEditReview.html";
		}
	}
}
