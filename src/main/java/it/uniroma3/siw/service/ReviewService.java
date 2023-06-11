package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.authentication.AuthConfiguration;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.validator.ReviewValidator;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ReviewValidator reviewValidator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    CredentialsService credentialsService;

    @Transactional
    public void persistReview(Review review){
        this.reviewRepository.save(review);
    }

    @Transactional
    public void deleteGenre(Review review){
        this.reviewRepository.delete(review);
    }

    @Transactional
    public Iterable<Review> getAllGenres(){
        return  this.reviewRepository.findAll();
    }

    @Transactional
    public Review getReviewById(Long id){
        return this.reviewRepository.findById(id).get();
    }

    @Transactional
    public Float getMovieRatingById(Long id){
        return this.reviewRepository.findValutationAvgByMovie(this.movieRepository.findById(id).get());
    }

    @Transactional
    public void deleteReviewById(Long id){
        Review review = this.reviewRepository.findById(id).get();
		review.getMovie().getReviews().remove(review);

		this.reviewRepository.delete(review);
    }

    @Transactional
    public Movie deleteReviewByIdAndReturnMovie(Long id){
        Review review = this.reviewRepository.findById(id).get();
		review.getMovie().getReviews().remove(review);

		Movie movie = review.getMovie();
		this.reviewRepository.delete(review);

        return movie;
    }

    @Transactional
    public Movie createReviewForMovie(Long id, Review review, BindingResult bindingResult) throws IOException{
        Movie movie = this.movieRepository.findById(id).get();
		
        //Collegamenti uscenti di review
		review.setUser(AuthConfiguration.getCurrentUser(credentialsService));
        review.setMovie(movie);

		//Controllo se tutti i valori sono stati inseriti correttamente e che non ci sia già una recensione per quell'utente
		this.reviewValidator.validate(review, bindingResult);
	
		if (!bindingResult.hasErrors()) {

            //collegamenti entranti
            movie.getReviews().add(review);
			review.getUser().getReview().add(review);
			
			//rendo persistenti i cambiamenti
			this.reviewRepository.save(review);

			return movie;
		} else {
			throw new IOException();
		}
    }

    @Transactional
    public User editReviewForMovie(Long id, Review newreview, BindingResult bindingResult) throws IOException{
        this.reviewValidator.validate(newreview, bindingResult);
		Review review = this.reviewRepository.findById(id).get();

		if (!bindingResult.hasErrors()) {
			
			review.setTitle(newreview.getTitle());
			review.setValutation(newreview.getValutation());
			review.setContent(newreview.getContent());

			this.reviewRepository.save(review);

			return review.getUser();
		} else {
			throw new IOException();
		}
	}
}
