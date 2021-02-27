package me.jysh.moviereviewservice.model;

import lombok.Getter;
import me.jysh.moviereviewservice.enums.Genre;

import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Movie extends BaseEntity {

  private final String name;

  private final Set<Genre> genres;

  private final Year releaseYear;

  private final Set<String> reviews;

  public Movie(String name, Year releaseYear, Genre... genres) {

    this.name = name;
    this.releaseYear = releaseYear;
    this.genres = Arrays.stream(genres).collect(Collectors.toSet());
    this.reviews = new HashSet<>();
  }

  public boolean hasGenre(Genre genre) {

    return this.genres.contains(genre);
  }

  public int getReviewCount() {

    return this.reviews.size();
  }

  public void addReview(String reviewId) {

    this.reviews.add(reviewId);
  }

  @Override
  public String toString() {

    return "\"" + name + "\" released in Year " + releaseYear.toString() + " for " + genres;
  }
}
