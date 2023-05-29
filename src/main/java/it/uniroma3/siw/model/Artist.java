package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.*;

@Entity
public class Artist {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
    private String name;
	@NotBlank
    private String surname;
	
	@NotNull @PastOrPresent
    private LocalDate birthDate;
	private LocalDate deathDate;

	@Column(length = 100000000)//max 10Mb
	private String imageString;
    
    @ManyToMany
    private List<Movie> movies;
    
    @OneToMany(mappedBy="director")
    private List<Movie> directedMovies;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	public LocalDate getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}
	public String getImageString() {
		return imageString;
	}
	public void setImageString(String imageString) {
		this.imageString = imageString;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(name, other.name);

    }
	
	public List<Movie> getMovies() {
		return movies;
	}
	
	public void setMovies(List<Movie> movies) {
		this.movies = movies;
	}
	
	public List<Movie> getDirectedMovies() {
		return directedMovies;
	}
	
	public void setDirectedMovies(List<Movie> directedMovies) {
		this.directedMovies = directedMovies;
	}
}
