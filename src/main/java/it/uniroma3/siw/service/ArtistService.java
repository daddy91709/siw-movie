package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.ArtistValidator;

@Service
public class ArtistService {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ArtistValidator artistValidator;

    @Transactional
    public void persistArtist(Artist artist){
        this.artistRepository.save(artist);
    }

    @Transactional
    public void deleteArtist(Artist artist){
        this.artistRepository.delete(artist);
    }

    @Transactional
    public Iterable<Artist> getAllArtists(){
        return  this.artistRepository.findAll();
    }

    @Transactional
    public Artist getArtistById(Long id){
        return this.artistRepository.findById(id).get();
    }

    @Transactional
    public Iterable<Artist> getArtistsNotInMovieById(Long id){
        return this.artistRepository.getArtistByMoviesNotContains(this.movieRepository.findById(id).get());
    }

    @Transactional
    public void deleteArtistById(Long id){
        Artist artist = this.artistRepository.findById(id).get();

        for (Movie movie : artist.getDirectedMovies()) {
            movie.setDirector(null);
        }

        this.artistRepository.delete(artist);
    }

    @Transactional
    public Iterable<Artist> deleteArtistByIdAndReturnAll(Long id){
        Artist artist = this.artistRepository.findById(id).get();

        for (Movie movie : artist.getDirectedMovies()) {
            movie.setDirector(null);
        }

        this.artistRepository.delete(artist);

        return this.artistRepository.findAll();
    }

    @Transactional
    public Artist createArtist(Artist artist, BindingResult bindingResult, MultipartFile image) throws IOException{
        this.artistValidator.validate(artist, bindingResult);

        if (!bindingResult.hasErrors()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                artist.setImageString(base64Image);
            } catch (IOException e) {
            }

            this.artistRepository.save(artist);

            return artist;
        } else {
            throw new IOException();
        }
    }

    @Transactional
    public Artist editArtist(Long id, Artist newartist, BindingResult bindingResult, MultipartFile image) throws IOException{
               // valida i nuovi dati per verificare che non ci siano stringhe nulle
               this.artistValidator.validate(newartist, bindingResult);

               // se non ci sono errori di campo salva i nuovi dati
               if (!bindingResult.hasFieldErrors()) {
       
                   Artist artist = this.artistRepository.findById(id).get();
                   artist.setName(newartist.getName());
                   artist.setSurname(newartist.getSurname());
                   
                   artist.setBirthDate(newartist.getBirthDate());
                   artist.setDeathDate(newartist.getDeathDate());
       
                   // se è cambiata anche l'immagine aggiornala
                   try {
                       if (image.getBytes().length != 0) {
                           String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                           artist.setImageString(base64Image);
                       }
                   } catch (IOException e) {
                   }
       
                   this.artistRepository.save(artist);
       
                   return artist;
               } 
               // se c'erano errori di campo allora riporta alla form con gli errori
               else {
                   throw new IOException();
               }
    }
}
