package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Genre;
import it.uniroma3.siw.repository.GenreRepository;

@Component
public class GenreValidator implements Validator {

	@Autowired
	private GenreRepository genreRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Genre genre = (Genre) o;
		if (genre.getName() != null
				&& genreRepository.existsByName(genre.getName())) {
			errors.reject("genre.duplicate");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Artist.class.equals(aClass);
	}

}
