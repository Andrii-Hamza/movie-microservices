package com.programming.movie_service.service;

import com.programming.movie_service.dto.MovieRequest;
import com.programming.movie_service.dto.MovieResponse;
import com.programming.movie_service.model.Movie;
import com.programming.movie_service.repo.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    public void createMovie(MovieRequest movieRequest) {
        Movie movie = Movie.builder()
                .movieTitle(movieRequest.getMovieTitle())
                .movieDirector(movieRequest.getMovieDirector())
                .movieReleaseYear(movieRequest.getMovieReleaseYear())
                .build();

        movieRepository.save(movie);
        log.info("Movie {} is created", movie.getMovieId());
    }

    public List<MovieResponse> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        return movies.stream().map(this::mapToMovieResponse).toList();
    }


    private MovieResponse mapToMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .movieTitle(movie.getMovieTitle())
                .movieDirector(movie.getMovieDirector())
                .movieReleaseYear(movie.getMovieReleaseYear())
                .build();
    }
}
