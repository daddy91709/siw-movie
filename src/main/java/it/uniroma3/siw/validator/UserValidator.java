package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Component
public class UserValidator implements Validator {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void validate(Object o, Errors errors) {
		User user = (User) o;
		if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
			errors.reject("user.duplicate");
		}

		 // Validazione specifica per le credenziali
		 ValidationUtils.rejectIfEmptyOrWhitespace(errors, "credentials.username", "NotBlank");
		 ValidationUtils.rejectIfEmptyOrWhitespace(errors, "credentials.password", "NotBlank");
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

}