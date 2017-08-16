package com.girish.example.jerseyswagger.rest.v1;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.girish.example.jerseyswagger.main.Application;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class HelloResourceTest {

	private static final String API_PATH = "/api";
	private static final String MSG_TEMPLATE = "Hello %s. Version %s - passed in %s";

	@Value("${local.server.port}")
	private int port;

	@Before
	public void setup() {
		RestAssured.port = this.port;
	}

	@Test
	public void shouldRetrieveNameVersion1InURL() {
		String name = "world";
		RestAssured.
			given().
				accept(ContentType.JSON).
			when().
				get(String.format("%s/v1/hello/{name}", API_PATH), name).
			then().
				statusCode(HttpStatus.SC_OK).
				contentType(ContentType.JSON).
				body("msg", Matchers.equalTo(String.format(MSG_TEMPLATE, name, 1, "URL")));
	}

	@Test
	public void retrieveShouldResultIn404Version1InURL() {
		String name = "404";
		RestAssured.
			when().
				get(String.format("%s/v1/hello/{name}", API_PATH), name).
			then().
				statusCode(HttpStatus.SC_NOT_FOUND).
				contentType(ContentType.JSON);
	}

	

	@Test
	public void shouldCreateNewResourceVersion1InUrl() {
		String name = "world";
		RestAssured.
			given().
				contentType("application/json").
				accept("application/json").
				body("{ \"msg\": \"world\" }").
			when().
				post(String.format("%s/v1/hello", API_PATH)).
			then().
				statusCode(HttpStatus.SC_CREATED).
				header("Location", Matchers.equalTo(String.format("http://localhost:%s%s/v1/hello/%s", this.port, API_PATH, name)));
	}

	
}