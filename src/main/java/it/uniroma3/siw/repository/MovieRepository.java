package it.uniroma3.siw.repository;

import java.time.Year;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> {
	
    public List<Movie> findAllByYear(Integer year);
    public boolean existsByTitleAndYear(String title, Integer year);
}
