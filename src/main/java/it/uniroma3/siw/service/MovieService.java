package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.GenreRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.MovieValidator;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MovieValidator movieValidator;

    @Transactional
    public void persistMovie(Movie movie){
        this.movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovie(Movie movie){
        this.movieRepository.delete(movie);
    }

    @Transactional
    public Iterable<Movie> getAllMovies(){
        return  this.movieRepository.findAll();
    }

    @Transactional
    public Movie getMovieById(Long id){
        return this.movieRepository.findById(id).get();
    }

    @Transactional
    public List<Movie> getMoviesByYear(Integer year){
        return this.movieRepository.findAllByYear(year);
    }

    @Transactional
    public List<Movie> getMoviesByGenre(Genre genre){
        return this.movieRepository.findAllByGenresContains(genre);
    }

    @Transactional
    public boolean existsByTitleAndYear(String title, Integer year){
        return this.movieRepository.existsByTitleAndYear(title, year);
    }

    @Transactional
    public Movie addDirectorTo(Long idMovie, Long idArtist){
        Movie movie = this.movieRepository.findById(idMovie).get();
		Artist artist = this.artistRepository.findById(idArtist).get();

		movie.setDirector(artist);
		this.movieRepository.save(movie);

		artist.getDirectedMovies().add(movie);
		this.artistRepository.save(artist);

        return movie;
    }

    @Transactional
    public Movie addActorTo(Long idMovie, Long idArtist){
        Movie movie = this.movieRepository.findById(idMovie).get();
		Artist artist = this.artistRepository.findById(idArtist).get();

		movie.getArtists().add(artist);
		this.movieRepository.save(movie);

		artist.getMovies().add(movie);
		this.artistRepository.save(artist);

        return movie;
    }

    @Transactional
    public Movie removeActorFrom(Long idMovie, Long idArtist){
        Movie movie = this.movieRepository.findById(idMovie).get();
		Artist artist = this.artistRepository.findById(idArtist).get();

		movie.getArtists().remove(artist);
		this.movieRepository.save(movie);

		artist.getMovies().remove(movie);
		this.artistRepository.save(artist);

        return movie;
    }
    
    @Transactional
    public Movie addGenreTo(Long idMovie, Long idGenre){
        Movie movie = this.movieRepository.findById(idMovie).get();
		Genre genre = this.genreRepository.findById(idGenre).get();

		movie.getGenres().add(genre);
		this.movieRepository.save(movie);

		genre.getMovies().add(movie);
		this.genreRepository.save(genre);

        return movie;
    }

    @Transactional
    public Movie removeGenreFrom(Long idMovie, Long idGenre){
        Movie movie = this.movieRepository.findById(idMovie).get();
		Genre genre = this.genreRepository.findById(idGenre).get();

		movie.getGenres().remove(genre);
		this.movieRepository.save(movie);

		genre.getMovies().remove(movie);
		this.genreRepository.save(genre);

        return movie;
    }

    @Transactional
    public void deleteMovieById(Long id){
        Movie movie = this.movieRepository.findById(id).get();

		for (Artist artist : movie.getArtists()) {
			artist.getMovies().remove(movie);
		}

		this.movieRepository.delete(movie);
    }

    @Transactional
    public Iterable<Movie> deleteMovieByIdAndReturnAll(Long id){
        Movie movie = this.movieRepository.findById(id).get();

		for (Artist artist : movie.getArtists()) {
			artist.getMovies().remove(movie);
		}

        for (Genre genre : movie.getGenres()) {
			genre.getMovies().remove(movie);
		}

		this.movieRepository.delete(movie);

        return this.movieRepository.findAll();
    }

    @Transactional
    public Movie createMovie(Movie movie, BindingResult bindingResult, MultipartFile image) throws IOException{
        this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {

			try {
				String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
				movie.setImageString(base64Image);
			} catch (IOException e) {
			}

			this.movieRepository.save(movie);

            return movie;
        }
        else throw new IOException();
    }

    @Transactional
    public Movie editMovie(Long id,Movie newmovie, BindingResult bindingResult, MultipartFile image) throws IOException{
        // valida i nuovi dati per verificare che non ci siano stringhe nulle
		this.movieValidator.validate(newmovie, bindingResult);

		// se non ci sono errori di campo salva i nuovi dati
		if (!bindingResult.hasFieldErrors()) {
			Movie movie = this.movieRepository.findById(id).get();
			movie.setTitle(newmovie.getTitle());
			movie.setYear(newmovie.getYear());

			// se è cambiata anche l'immagine aggiornala
			try {
				if (image.getBytes().length != 0) {
					String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
					movie.setImageString(base64Image);
				}
			} catch (IOException e) {
			}

			this.movieRepository.save(movie);

            return movie;
        }
        else throw new IOException();
    }
}

