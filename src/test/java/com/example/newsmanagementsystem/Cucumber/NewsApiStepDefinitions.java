package com.example.newsmanagementsystem.Cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class NewsApiStepDefinitions {

    private final WebApplicationContext applicationContext;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private boolean authenticated;


    public NewsApiStepDefinitions(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        authenticated = false;
        mvcResult = null;
    }

    @Given("the request has no authentication")
    public void requestHasNoAuthentication() {
        authenticated = false;
    }

    @Given("the request has an authenticated JWT user")
    public void requestHasAuthenticatedJwtUser() {
        authenticated = true;
    }

    @When("the user requests all news")
    public void userRequestsAllNews() throws Exception {

        var request = get("/api/v1/news")
                .accept(MediaType.APPLICATION_JSON);


        if (authenticated) {
            request.with(
                    jwt().jwt(jwtBuilder ->
                            jwtBuilder
                                    .subject("cucumber-test-user")
                                    .claim("email", "cucumber@example.com")
                    )
            );
        }

        mvcResult = mockMvc.perform(request)
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {

        assertThat(mvcResult)
                .as("A request should have been executed")
                .isNotNull();

        int actualStatus = mvcResult
                .getResponse()
                .getStatus();

        assertThat(actualStatus)
                .as("Unexpected HTTP response status")
                .isEqualTo(expectedStatus);
    }
}