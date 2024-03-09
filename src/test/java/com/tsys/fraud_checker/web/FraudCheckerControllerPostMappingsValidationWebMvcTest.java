package com.tsys.fraud_checker.web;

import com.tsys.fraud_checker.services.DefaultVerificationService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// For Junit4, use @RunWith
// @RunWith(SpringRunner.class)
// For Junit5, use @ExtendWith
// SpringExtension.class provides a bridge between Spring Boot test features
// and JUnit. Whenever we use any Spring Boot testing features in our JUnit
// tests, this annotation will be required.
@ExtendWith(SpringExtension.class)
// We're only testing the web layer, we use the @WebMvcTest
// annotation. It allows us to easily test requests and responses
// using the set of static methods implemented by the
// MockMvcRequestBuilders and MockMvcResultMatchers classes.
//
// Using the @WebMvcTest Annotation we are loading Spring's
// WebApplication Context and hence all Controller Advices and Filters
// get automatically applied.
//
// We verify the validation behavior by applying Validation Advice, it
// is automatically available, because we are using @WebMvcTest annotation.
//
// NOTE: No Web-Server is deployed
@WebMvcTest(FraudCheckerController.class)
@Tag("UnitTest")
public class FraudCheckerControllerPostMappingsValidationWebMvcTest {

    @MockBean
    private DefaultVerificationService verificationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void validatesRequestHeaderParameterAtOrAboveValue5() throws Exception {
        final var request = givenAFraudCheckRequestFor("{\n" +
                "    \"creditCard\" : {\n" +
                "        \"number\": \"4485-2847-2013-4093\",\n" +
                "        \"holderName\" : \"Jumping Jack\",\n" +
                "        \"issuingBank\" : \"Bank of America\",\n" +
                "        \"validUntil\" : \"2020-10-04T01:00:26.874+00:00\",\n" +
                "        \"cvv\" : 123\n" +
                "    },\n" +
                "    \"charge\" : {\n" +
                "        \"currency\" : \"INR\",\n" +
                "        \"amount\" : 1235.45\n" +
                "    }\n" +
                "}",
            "6");

        final ResultActions resultActions = whenTheRequestIsMade(request);
        thenExpect(resultActions,
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.content().bytes("valid".getBytes()));
    }

    @Test
    public void shoutsWhenRequestHeaderParameterIsBelow5() throws Exception {
        final var request = givenAFraudCheckRequestFor("{\n" +
                "    \"creditCard\" : {\n" +
                "        \"number\": \"4485-2847-2013-4093\",\n" +
                "        \"holderName\" : \"Jumping Jack\",\n" +
                "        \"issuingBank\" : \"Bank of America\",\n" +
                "        \"validUntil\" : \"2020-10-04T01:00:26.874+00:00\",\n" +
                "        \"cvv\" : 123\n" +
                "    },\n" +
                "    \"charge\" : {\n" +
                "        \"currency\" : \"INR\",\n" +
                "        \"amount\" : 1235.45\n" +
                "    }\n" +
                "}",
            "4");

        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
            "    \"validationErrors\": [\n" +
            "        {\n" +
            "            \"fieldName\": \"validateHeaderParameterUsingPost.param\",\n" +
            "            \"message\": \"must be greater than or equal to 5\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
        final var content = MockMvcResultMatchers.content();
        thenExpect(resultActions,
            MockMvcResultMatchers.status().isBadRequest(),
            content.contentType(MediaType.APPLICATION_JSON),
            content.json(response));
    }

    @Test
    public void shoutsWhenRequestHeaderParameterIsAbove9999() throws Exception {
        final var request = givenAFraudCheckRequestFor("{\n" +
                "    \"creditCard\" : {\n" +
                "        \"number\": \"4485-2847-2013-4093\",\n" +
                "        \"holderName\" : \"Jumping Jack\",\n" +
                "        \"issuingBank\" : \"Bank of America\",\n" +
                "        \"validUntil\" : \"2020-10-04T01:00:26.874+00:00\",\n" +
                "        \"cvv\" : 123\n" +
                "    },\n" +
                "    \"charge\" : {\n" +
                "        \"currency\" : \"INR\",\n" +
                "        \"amount\" : 1235.45\n" +
                "    }\n" +
                "}",
            "10000");

        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
            "    \"validationErrors\": [\n" +
            "        {\n" +
            "            \"fieldName\": \"validateHeaderParameterUsingPost.param\",\n" +
            "            \"message\": \"must be less than or equal to 9999\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
        final var content = MockMvcResultMatchers.content();
        thenExpect(resultActions,
            MockMvcResultMatchers.status().isBadRequest(),
            content.contentType(MediaType.APPLICATION_JSON),
            content.json(response));
    }


    private MockHttpServletRequestBuilder givenAFraudCheckRequestFor(String requestBody, String headerParam) {
        return MockMvcRequestBuilders.post("/validateHeaderParameterUsingPost")
            .contentType(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(requestBody)
            .header("param", headerParam);
    }

    private ResultActions whenTheRequestIsMade(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private void thenExpect(ResultActions resultActions, ResultMatcher... matchers) throws Exception {
        resultActions.andExpect(ResultMatcher.matchAll(matchers));
    }
}
