package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Entity
public class Movie {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String title;
	
	@NotNull @Min(1900) @Max(2023) //@PastOrPresent se volessimo indicare solo film passati
    private Integer year;

	@Column(length = 100000000)//max 10Mb
	private String imageString;
    
    @ManyToMany(mappedBy="movies")
    private List<Artist> artists;

	@ManyToMany(mappedBy="movies")
    private List<Genre> genres;
    
   

	@ManyToOne
    private Artist director;

	@OneToMany(mappedBy="movie") //introdurre fetch type lazy e gettare dalla repo?
	@Cascade(CascadeType.ALL)
	private List<Review> reviews;
    
	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(title, other.title) && Objects.equals(year, other.year);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getImageString() {
		return imageString;
	}
	public void setImageString(String imageString) {
		this.imageString = imageString;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	public Artist getDirector() {
		return director;
	}

	public void setDirector(Artist director) {
		this.director = director;
	}
	
	public List<Genre> getGenres() {
		return genres;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}
}