package me.jysh.moviereviewservice.service;

import me.jysh.moviereviewservice.enums.Genre;
import me.jysh.moviereviewservice.enums.Role;
import me.jysh.moviereviewservice.enums.UserUpgradationPolicy;
import me.jysh.moviereviewservice.exception.MovieNotFoundException;
import me.jysh.moviereviewservice.exception.MovieNotYetReleasedException;
import me.jysh.moviereviewservice.exception.MultipleReviewsNotAllowedException;
import me.jysh.moviereviewservice.exception.UserNotFoundException;
import me.jysh.moviereviewservice.model.Movie;
import me.jysh.moviereviewservice.model.Review;
import me.jysh.moviereviewservice.model.User;
import me.jysh.moviereviewservice.repository.MovieRepository;
import me.jysh.moviereviewservice.repository.ReviewRepository;
import me.jysh.moviereviewservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MovieReviewService {

  private final UserRepository userRepository;

  private final ReviewRepository reviewRepository;

  private final MovieRepository movieRepository;

  @Autowired
  public MovieReviewService(
      UserRepository userRepository,
      ReviewRepository reviewRepository,
      MovieRepository movieRepository) {

    this.userRepository = userRepository;
    this.reviewRepository = reviewRepository;
    this.movieRepository = movieRepository;
  }

  public User addUser(String username) {

    final User user = new User(username);
    user.addRole(Role.ROLE_USER);

    userRepository.save(user);

    return user;
  }

  public Movie addMovie(String movieName, Year year, Genre... genres) {

    final Movie movie = new Movie(movieName, year, genres);

    movieRepository.save(movie);

    return movie;
  }

  public Review addReview(String userId, String movieId, Integer rating) {

    final Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);

    if (movie.getReleaseYear().isAfter(Year.now())) {
      throw new MovieNotYetReleasedException();
    }

    if (reviewRepository.hasOldReview(userId, movieId)) {
      throw new MultipleReviewsNotAllowedException();
    }

    final User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    Review review = new Review(userId, movieId, rating, user.hasRole(Role.ROLE_CRITIC));

    this.updateUserAfterReview(user, review);

    movie.addReview(review.getId());

    movieRepository.save(movie);
    reviewRepository.save(review);

    return review;
  }

  private void updateUserAfterReview(User user, Review review) {

    user.addReview(review.getId());

    if (UserUpgradationPolicy.CRITIC_UPGRADE_POLICY.passes(user)) {
      user.addRole(UserUpgradationPolicy.CRITIC_UPGRADE_POLICY.getUpgradableRole());
    }

    userRepository.save(user);
  }

  public List<Movie> findTopNMoviesByGenre(final Integer n, final Genre genre) {

    final List<Movie> moviesByGenre = movieRepository.findAllBy(movie -> movie.hasGenre(genre));

    final Map<Movie, Integer> movieByGenreWithScore =
        moviesByGenre.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    movie -> this.getMovieScore(movie, Review::getIsCriticReview)));

    return movieByGenreWithScore.entrySet().stream()
        .sorted(movieScoreEntryComparator())
        .limit(n)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private Comparator<Map.Entry<Movie, Integer>> movieScoreEntryComparator() {

    return (movieScoreEntry1, movieScoreEntry2) -> {
      final Integer scoreEntry1 = movieScoreEntry1.getValue();
      final Integer scoreEntry2 = movieScoreEntry2.getValue();

      final String movieNameEntry1 = movieScoreEntry1.getKey().getName();
      final String movieNameEntry2 = movieScoreEntry2.getKey().getName();

      return scoreEntry2.equals(scoreEntry1)
          ? String.CASE_INSENSITIVE_ORDER.compare(movieNameEntry1, movieNameEntry2)
          : scoreEntry2 - scoreEntry1;
    };
  }

  public double getAverageReviewScore(Year year) {

    final List<Movie> moviesByYear =
        movieRepository.findAllBy(movie -> movie.getReleaseYear().equals(year));

    final Map<Movie, Integer> movieByYearWithScore =
        moviesByYear.stream()
            .collect(
                Collectors.toMap(
                    Function.identity(), movie -> this.getMovieScore(movie, review -> true)));

    if (movieByYearWithScore.size() == 0) {
      return 0;
    }

    int sum = movieByYearWithScore.values().stream().mapToInt(value -> value).sum();

    return (double) sum / movieByYearWithScore.size();
  }

  public double getAverageReviewScore(String movieId) {

    final Movie movie = movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);

    if (movie.getReviewCount() == 0) {
      return 0;
    }

    int totalScore = this.getMovieScore(movie, review -> true);

    return (double) totalScore / movie.getReviewCount();
  }

  private int getMovieScore(Movie movie, Predicate<Review> reviewPredicate) {

    return reviewRepository.findAllBy(movie.getReviews()).stream()
        .filter(reviewPredicate)
        .mapToInt(Review::getRating)
        .sum();
  }
}
