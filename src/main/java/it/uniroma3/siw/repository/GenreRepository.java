package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Movie;

public interface GenreRepository extends CrudRepository<Genre, Long> {
    public boolean existsByName(String name);
    public List<Genre> getGenreByMoviesNotContains(Movie movie);
}
