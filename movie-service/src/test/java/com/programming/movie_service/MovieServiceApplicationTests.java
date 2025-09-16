package com.programming.movie_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programming.movie_service.dto.MovieRequest;
import com.programming.movie_service.repo.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class MovieServiceApplicationTests {

//	@Container
//	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:5.7");   // mysql:8.0

	@Container
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpass");   // mysql:8.0

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MovieRepository movieRepository;


	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
	}

	@Test
	void shouldCreateMovie() throws Exception {
		MovieRequest movieRequest = getMovieRequest();
		String movieRequestString = objectMapper.writeValueAsString(movieRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/movie")
				.contentType(MediaType.APPLICATION_JSON)
				.content(movieRequestString))
				.andExpect(status().isCreated());
        Assertions.assertEquals(1, movieRepository.findAll().size());
	}

	private MovieRequest getMovieRequest() {
		return MovieRequest.builder()
				.movieTitle("Harry Potter")
				.movieDirector("Carl Motue")
				.movieReleaseYear(2002)
				.build();
	}

}
