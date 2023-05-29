package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface ArtistRepository extends CrudRepository<Artist, Long>{

    public boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate);
    public List<Artist> getArtistByMoviesNotContains(Movie movie); //?
    //List<Artist> FindAllByActedMoviesIsNotControlling(Movie movie);

}