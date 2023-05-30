package it.uniroma3.siw.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.GenreRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.validator.UserValidator;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    public String indexPage(Model model){
        model.addAttribute("genres", this.genreRepository.findAll());
        return "index.html";
    }

    @GetMapping("/login")
    public String toLoginPage() {
        return "login.html";
    }

    @GetMapping("/formNewUser")
    public String formNewUser(Model model) {
        model.addAttribute("user", new User());
        return "formNewUser.html";
    }

    @PostMapping(value = { "/formNewUser" })
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult userBindingResult, Model model) {
        // se user ha contenuti validi, memorizza User e le Credentials nel DB
        this.userValidator.validate(user, userBindingResult);

        if (!userBindingResult.hasErrors()) {
            credentialsService.saveCredentials(user.getCredentials());

            user.getCredentials().setUser(user);
            userService.saveUser(user);

            model.addAttribute("genres", this.genreRepository.findAll());
            return "index.html";
        }
        return "formNewUser.html";
    }

    @GetMapping(value = "/index")
    public String index(Model model) {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("genres", this.genreRepository.findAll());
        //model.addAttribute("topMovies", this.reviewRepository.findTopRatedMovies());

        if (authentication instanceof AnonymousAuthenticationToken) {
            return "index.html";
        }
        else {
            UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
            if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
                return "admin/adminindex.html";
            }
        }
        return "index.html";
    }

    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            // carica la pagina admin
            model.addAttribute("genres", this.genreRepository.findAll());
            return "admin/adminindex.html";
        } else if (credentials.getRole().equals(Credentials.DEFAULT_ROLE)) {
            // carica la pagina utente autenticato
            model.addAttribute("genres", this.genreRepository.findAll());
            return "index.html";
        }
        return "login.html";
    }
}
