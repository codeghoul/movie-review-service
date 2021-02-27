package me.jysh.moviereviewservice;

import me.jysh.moviereviewservice.constant.TestConstants;
import me.jysh.moviereviewservice.enums.Genre;
import me.jysh.moviereviewservice.model.Movie;
import me.jysh.moviereviewservice.model.Review;
import org.junit.jupiter.params.provider.Arguments;

import java.time.Year;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestArgumentsGenerators {

  static Stream<Arguments> getAverageReviewScoreByYearArgumentsGenerator() {

    final List<Movie> movies = getMovieList();

    final List<List<Review>> reviews = getReviews();

    final List<Review> reviewsSet1 =
        reviews.stream().flatMap(Collection::stream).limit(8).collect(Collectors.toList());

    return Stream.of(
        Arguments.of(movies, reviews, Year.of(2006)),
        Arguments.of(movies, reviews, Year.of(2008)),
        Arguments.of(movies, reviews, Year.of(2020)),
        Arguments.of(movies, reviews, Year.of(2000)));
  }

  static Stream<Arguments> findTopNMoviesByGenreArgumentsGenerator() {

    final List<Movie> movies = getMovieList();

    final List<List<Review>> reviews = getReviews();

    return Stream.of(
        Arguments.of(movies, reviews, "movie5, movie3, movie6, movie1, movie4, movie2", 6),
        Arguments.of(movies, reviews, "movie5, movie3, movie6", 3),
        Arguments.of(movies, reviews, "", 0),
        Arguments.of(movies, reviews, "movie5", 1),
        Arguments.of(movies, reviews, "movie5, movie3, movie6, movie1, movie4, movie2", 7));
  }

  public static Stream<Arguments> movieArgumentsGenerator() {

    return Stream.of(
        Arguments.of("Don", Year.of(2006), new Genre[] {Genre.ACTION, Genre.COMEDY}),
        Arguments.of("Tiger", Year.of(2008), new Genre[] {Genre.DRAMA}),
        Arguments.of("Padmaavat", Year.of(2006), new Genre[] {Genre.COMEDY}),
        Arguments.of("Lunchbox", Year.of(2020), new Genre[] {Genre.DRAMA}),
        Arguments.of("Guru", Year.of(2006), new Genre[] {Genre.DRAMA}),
        Arguments.of("Metro", Year.of(2006), new Genre[] {Genre.ROMANCE}));
  }

  public static Stream<String> userArgumentGenerator() {

    return Stream.of("SRK", "Salman", "Deepika");
  }

  public static List<List<Review>> getReviews() {

    final List<Review> movie1Review =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 5, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 6, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 4, true));

    final List<Review> movie2Review =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 1, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 2, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 3, true));

    final List<Review> movie3Review =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 4, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 5, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 6, true));

    final List<Review> movie4Review =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 1, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 5, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 7, true));

    final List<Review> movie5Review =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 6, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 8, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 9, true));

    final List<Review> movie6Review =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 5, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 6, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 5, true));

    return List.of(
        movie1Review, movie2Review, movie3Review, movie4Review, movie5Review, movie6Review);
  }

  public static List<Movie> getMovieList() {

    return List.of(
        new Movie("movie1", Year.of(2006), Genre.ACTION, Genre.COMEDY),
        new Movie("movie2", Year.of(2008), Genre.ACTION),
        new Movie("movie3", Year.of(2006), Genre.ACTION, Genre.ROMANCE),
        new Movie("movie4", Year.of(2020), Genre.ACTION, Genre.DRAMA),
        new Movie("movie5", Year.of(2006), Genre.ACTION),
        new Movie("movie6", Year.of(2006), Genre.ACTION));
  }
}
