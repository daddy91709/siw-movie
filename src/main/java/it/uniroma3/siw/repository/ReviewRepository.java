package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    public boolean existsByUserAndMovie(User user, Movie movie); 
    public List<Review> findByMovie(Movie movie);

    @Query("SELECT AVG(r.valutation) FROM Review r WHERE r.movie = :movie")
    public Float findValutationAvgByMovie(@Param("movie") Movie movie);
}
