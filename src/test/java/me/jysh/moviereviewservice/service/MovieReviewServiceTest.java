package me.jysh.moviereviewservice.service;

import me.jysh.moviereviewservice.config.TestConfiguration;
import me.jysh.moviereviewservice.TestArgumentsGenerators;
import me.jysh.moviereviewservice.constant.TestConstants;
import me.jysh.moviereviewservice.enums.Genre;
import me.jysh.moviereviewservice.enums.Role;
import me.jysh.moviereviewservice.exception.MovieNotFoundException;
import me.jysh.moviereviewservice.exception.MultipleReviewsNotAllowedException;
import me.jysh.moviereviewservice.exception.UserNotFoundException;
import me.jysh.moviereviewservice.model.BaseEntity;
import me.jysh.moviereviewservice.model.Movie;
import me.jysh.moviereviewservice.model.Review;
import me.jysh.moviereviewservice.model.User;
import me.jysh.moviereviewservice.repository.MovieRepository;
import me.jysh.moviereviewservice.repository.ReviewRepository;
import me.jysh.moviereviewservice.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static me.jysh.moviereviewservice.constant.TestConstants.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
class MovieReviewServiceTest {

  @Autowired private MovieReviewService movieReviewService;

  @MockBean private UserRepository userRepository;

  @MockBean private MovieRepository movieRepository;

  @MockBean private ReviewRepository reviewRepository;

  @ParameterizedTest
  @DisplayName("Add User Test")
  @MethodSource(ADD_USER_ARGUMENTS_GENERATOR_LOCATION)
  void addUser(final String name) {

    Mockito.doNothing().when(userRepository).save(Mockito.any(User.class));

    final User user = movieReviewService.addUser(name);

    Assertions.assertNotNull(user.getId(), "User id is not null");

    Assertions.assertEquals(user.getName(), name, "User name is set correctly");

    Assertions.assertTrue(
        user.hasRole(Role.ROLE_USER), "User has the role :: " + Role.ROLE_USER.name());

    Arrays.stream(Role.values())
        .filter(role -> !role.equals(Role.ROLE_USER))
        .forEach(
            role ->
                Assertions.assertFalse(
                    user.hasRole(role), "User does not have the role :: " + role.name()));
  }

  @ParameterizedTest
  @DisplayName("Add Movie Test")
  @MethodSource(ADD_MOVIE_ARGUMENTS_GENERATOR_LOCATION)
  public void addMovie(final String name, final Year year, final Genre... genres) {

    Mockito.doNothing().when(movieRepository).save(Mockito.any(Movie.class));

    final Movie movie = movieReviewService.addMovie(name, year, genres);

    Assertions.assertNotNull(movie.getId(), "Movie id is not null");

    Assertions.assertEquals(movie.getName(), name, "Movie name is set correctly.");

    Assertions.assertEquals(movie.getReleaseYear(), year, "Movie release year is set correctly.");

    Assertions.assertTrue(
        Arrays.stream(genres).allMatch(movie::hasGenre), "Movie has all given genres.");
  }

  @Test
  public void addReview_forNormalUser() {

    final User user = new User("Salman");
    user.addRole(Role.ROLE_USER);

    final Movie movie = new Movie("Don", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    addReviewMocks(user, movie);

    final Review review = movieReviewService.addReview(user.getId(), movie.getId(), 5);

    Assertions.assertNotNull(review.getId());
    Assertions.assertFalse(review.getIsCriticReview());
    Assertions.assertEquals(5, review.getRating());
  }

  @Test
  public void addReview_movieYetToBePublishedException() {

    final User user = new User("Salman");
    user.addRole(Role.ROLE_USER);

    final Movie movie = new Movie("Don", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    addReviewMocks(user, movie);

    final Review review = movieReviewService.addReview(user.getId(), movie.getId(), 5);

    Assertions.assertNotNull(review.getId());
    Assertions.assertFalse(review.getIsCriticReview());
    Assertions.assertEquals(5, review.getRating());
  }

  @Test
  public void addReview_forCriticUser() {

    final User user = new User("Salman");
    user.addRole(Role.ROLE_CRITIC);

    final Movie movie = new Movie("Don", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    addReviewMocks(user, movie);

    final Review review = movieReviewService.addReview(user.getId(), movie.getId(), 5);

    Assertions.assertNotNull(review.getId());
    Assertions.assertTrue(review.getIsCriticReview());
    Assertions.assertEquals(10, review.getRating());
  }

  @Test
  public void addReview_forMovieNotFound() {

    final User user = new User("Salman");
    user.addRole(Role.ROLE_CRITIC);

    addReviewMocks(user, null);

    Assertions.assertThrows(
        MovieNotFoundException.class,
        () -> movieReviewService.addReview(user.getId(), DUMMY_STRING, 5));
  }

  @Test
  public void addReview_forUserNotFound() {

    final Movie movie = new Movie("Don", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    addReviewMocks(null, movie);

    Assertions.assertThrows(
        UserNotFoundException.class,
        () -> movieReviewService.addReview(DUMMY_STRING, movie.getId(), 5));
  }

  @Test
  public void addReview_userUpdatedToCritic() {

    final User user = new User("Salman");
    user.addRole(Role.ROLE_USER);

    final Movie movie = new Movie("Don", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    addReviewMocks(user, movie);

    movieReviewService.addReview(user.getId(), DUMMY_STRING, 5);
    movieReviewService.addReview(user.getId(), DUMMY_STRING, 6);

    Assertions.assertFalse(user.hasRole(Role.ROLE_CRITIC));

    movieReviewService.addReview(user.getId(), DUMMY_STRING, 7);

    Assertions.assertTrue(user.hasRole(Role.ROLE_CRITIC));
  }

  @Test
  public void addReview_multipleReviews() {

    final User user = new User("Salman");
    user.addRole(Role.ROLE_USER);

    final Movie movie = new Movie("Don", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    addReviewMocks(user, movie);

    Assertions.assertNotNull(movieReviewService.addReview(user.getId(), movie.getId(), 5));

    Mockito.when(reviewRepository.hasOldReview(Mockito.any(), Mockito.any())).thenReturn(true);

    Assertions.assertThrows(
        MultipleReviewsNotAllowedException.class,
        () -> movieReviewService.addReview(user.getId(), movie.getId(), 10));
  }

  private void addReviewMocks(User user, Movie movie) {

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
    Mockito.when(movieRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(movie));

    Mockito.doNothing().when(userRepository).save(Mockito.any(User.class));
    Mockito.doNothing().when(movieRepository).save(Mockito.any(Movie.class));
    Mockito.doNothing().when(reviewRepository).save(Mockito.any(Review.class));
  }

  @ParameterizedTest
  @MethodSource(FIND_TOP_N_ARGUMENTS_GENERATOR_LOCATION)
  public void findTopNMoviesByGenre(
      List<Movie> movies, List<List<Review>> listOfReviews, String expected, Integer N) {

    Mockito.when(movieRepository.findAllBy(Mockito.any(Predicate.class))).thenReturn(movies);

    Mockito.when(reviewRepository.findAllBy(Mockito.anySet()))
        .thenReturn(listOfReviews.get(0))
        .thenReturn(listOfReviews.get(1))
        .thenReturn(listOfReviews.get(2))
        .thenReturn(listOfReviews.get(3))
        .thenReturn(listOfReviews.get(4))
        .thenReturn(listOfReviews.get(5));

    final String topNMovies =
        movieReviewService.findTopNMoviesByGenre(N, Genre.ACTION).stream()
            .map(Movie::getName)
            .collect(Collectors.joining(", "));

    Assertions.assertEquals(expected, topNMovies);
  }

  @Test
  public void getAverageReviewScoreByMovieId_success() {

    final Movie movie = new Movie("movie1", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    final List<Review> reviews =
        List.of(
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 5, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 6, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 4, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 1, true),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 2, false),
            new Review(TestConstants.DUMMY_STRING, TestConstants.DUMMY_STRING, 3, true));

    reviews.stream().map(BaseEntity::getId).forEach(movie::addReview);

    Mockito.when(movieRepository.findById(Mockito.anyString())).thenReturn(Optional.of(movie));

    Mockito.when(reviewRepository.findAllBy(Mockito.anySet())).thenReturn(reviews);

    Assertions.assertEquals(
        5.666666666666667, movieReviewService.getAverageReviewScore(DUMMY_STRING));
  }

  @Test
  public void getAverageReviewScoreByMovieId_noReviews() {

    final Movie movie = new Movie("movie1", Year.of(2006), Genre.ACTION, Genre.COMEDY);

    Mockito.when(movieRepository.findById(Mockito.anyString())).thenReturn(Optional.of(movie));

    Assertions.assertEquals(0, movieReviewService.getAverageReviewScore(DUMMY_STRING));
  }

  @Test
  public void getAverageReviewScoreByMovieId_movieNotFoundException() {

    Mockito.when(movieRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

    Assertions.assertThrows(
        MovieNotFoundException.class, () -> movieReviewService.getAverageReviewScore(DUMMY_STRING));
  }

  @Test
  public void getAverageReviewScoreByYear() {

    final List<Movie> movieList = TestArgumentsGenerators.getMovieList();

    final List<List<Review>> reviews = TestArgumentsGenerators.getReviews();

    final int sum = reviews.stream().flatMap(Collection::stream).mapToInt(Review::getRating).sum();

    Mockito.when(movieRepository.findAllBy(Mockito.any(Predicate.class))).thenReturn(movieList);

    Mockito.when(reviewRepository.findAllBy(Mockito.anySet()))
        .thenReturn(reviews.get(0))
        .thenReturn(reviews.get(1))
        .thenReturn(reviews.get(2))
        .thenReturn(reviews.get(3))
        .thenReturn(reviews.get(4))
        .thenReturn(reviews.get(5));

    Assertions.assertEquals(
        (double) sum / movieList.size(), movieReviewService.getAverageReviewScore(Year.now()));
  }
}
