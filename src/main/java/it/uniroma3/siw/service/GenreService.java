package it.uniroma3.siw.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.GenreRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.GenreValidator;

@Service
public class GenreService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private GenreValidator genreValidator;

    @Transactional
    public void persistGenre(Genre genre){
        this.genreRepository.save(genre);
    }

    @Transactional
    public void deleteGenre(Genre genre){
        this.genreRepository.delete(genre);
    }

    @Transactional
    public Iterable<Genre> getAllGenres(){
        return  this.genreRepository.findAll();
    }

    @Transactional
    public Genre getGenreById(Long id){
        return this.genreRepository.findById(id).get();
    }

    @Transactional
    public Iterable<Genre> getGenresNotInMovieById(Long id){
        return this.genreRepository.getGenreByMoviesNotContains(this.movieRepository.findById(id).get());
    }

    @Transactional
    public void deleteGenreById(Long id){
        Genre genre = this.genreRepository.findById(id).get();

        this.genreRepository.delete(genre);
    }

    @Transactional
    public Iterable<Genre> deleteGenreByIdAndReturnAll(Long id){
        Genre genre = this.genreRepository.findById(id).get();

        this.genreRepository.delete(genre);

        return this.genreRepository.findAll();
    }

    @Transactional
    public Iterable<Genre> addGenreAndReturnAll(Genre genre, BindingResult bindingResult) throws IOException{
        this.genreValidator.validate(genre, bindingResult);

        if (!bindingResult.hasErrors()) {
            this.genreRepository.save(genre);
            return this.genreRepository.findAll();
        } else {
           throw new IOException();
        }
    }
}
