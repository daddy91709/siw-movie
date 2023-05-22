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

import ch.qos.logback.core.joran.action.NewRuleAction;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
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

	@GetMapping("/user/reviewFilm/{id}")
	public String formNewMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id));
		model.addAttribute("review", new Review());
		return "formNewReview.html";
	}

	@PostMapping("/user/addReviewTo/{id}")
	public String newMovie(@PathVariable("id") Long id, @Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult, Model model) {
		this.reviewValidator.validate(review, bindingResult);
		Movie movie= this.movieRepository.findById(id).get();

		if (!bindingResult.hasErrors()) {

			movie.getReviews().add(review);
			this.reviewRepository.save(review);
			this.movieRepository.save(movie);

			model.addAttribute("movie", movie);

			return "movie.html";
		} else {
			return "formNewReview.html";
		}
	}
}
