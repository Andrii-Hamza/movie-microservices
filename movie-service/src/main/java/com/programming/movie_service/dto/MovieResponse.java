package com.programming.movie_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {

    private Long movieId;

    private String movieTitle;

    private String movieDirector;

    private int movieReleaseYear;
}
