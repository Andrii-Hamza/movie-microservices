package com.programming.movie_service.controller;

import com.programming.movie_service.dto.MovieRequest;
import com.programming.movie_service.dto.MovieResponse;
import com.programming.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping     // ("/createMovie")
    @ResponseStatus(HttpStatus.CREATED)
    public void createMovie(@RequestBody MovieRequest movieRequest) {
        movieService.createMovie(movieRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }
}
