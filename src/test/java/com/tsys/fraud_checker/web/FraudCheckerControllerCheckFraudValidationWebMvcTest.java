package com.tsys.fraud_checker.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsys.fraud_checker.domain.CreditCard;
import com.tsys.fraud_checker.domain.CreditCardBuilder;
import com.tsys.fraud_checker.domain.FraudStatus;
import com.tsys.fraud_checker.domain.Money;
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

import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
// We verify the validation behavior by applying Validation Advice, it
// is automatically available, because we are using @WebMvcTest annotation
//
// NOTE: No Web-Server is deployed
@WebMvcTest(FraudCheckerController.class)
@Tag("UnitTest")
public class FraudCheckerControllerCheckFraudValidationWebMvcTest {

    private final Money charge = new Money(Currency.getInstance("INR"), 1235.45d);
    private final CreditCard validCard = CreditCardBuilder.make()
            .withHolder("Jumping Jack")
            .withIssuingBank("Bank of Test")
            .withValidNumber()
            .withValidCVV()
            .withFutureExpiryDate()
            .build();
    @MockBean
    private DefaultVerificationService verificationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void chargingAValidCard() throws Exception {
        final var request = givenAFraudCheckRequestFor(validCard, charge);
        FraudStatus ignoreSuccess = new FraudStatus(0, 0, false);
        given(verificationService.verifyTransactionAuthenticity(any(CreditCard.class), any(Money.class)))
                .willReturn(ignoreSuccess);

        final ResultActions resultActions = whenTheRequestIsMade(request);
        thenExpect(resultActions,
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.content().json(convertObjectToJson(ignoreSuccess))
        );
    }

    @Test
    public void shoutsWhenThereIsAProblemWithCheckingCardFraud() throws Exception {
        given(verificationService.verifyTransactionAuthenticity(any(CreditCard.class), any(Money.class)))
                .willThrow(new InterruptedException());

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
                "}");

        final ResultActions resultActions = whenTheRequestIsMade(request);

        thenExpect(resultActions,
                MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void shoutsWhenChargingWithoutAnyCreditCard() throws Exception {
        final var request = givenAFraudCheckRequestFor(null, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard\",\n" +
                "            \"message\": \"Require Credit Card Details!\"\n" +
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
    public void shoutsWhenChargingCardWithoutAmount() throws Exception {
        final var request = givenAFraudCheckRequestFor(validCard, null);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"charge\",\n" +
                "            \"message\": \"amount must be supplied!\"\n" +
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
    public void shoutsWhenCardNumberIsAbsent() throws Exception {
        var cardWithoutNumber = CreditCardBuilder.make()
                .withHolder("Card Holder")
                .withIssuingBank("Bank")
                .withFutureExpiryDate()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithoutNumber, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"Card number is required\"\n" +
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
    public void shoutsWhenCardNumberIsEmpty() throws Exception {
        var cardWithEmptyNumber = CreditCardBuilder.make()
                .withNumber("")
                .withHolder("Card Holder")
                .withIssuingBank("Bank")
                .withFutureExpiryDate()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithEmptyNumber, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"Card number is required\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"Failed Luhn check!\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"length must be between 16 and 19\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"Invalid Credit Card Number\"\n" +
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
    public void shoutsWhenCardNumberIsInvalid() throws Exception {
        var cardWithoutNumber = CreditCardBuilder.make()
                .withInvalidNumber()
                .withHolder("Card Holder")
                .withIssuingBank("Bank")
                .withFutureExpiryDate()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithoutNumber, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"Failed Luhn check!\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"Invalid Credit Card Number\"\n" +
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
    public void shoutsWhenCardNumberIsOfInsufficientLength() throws Exception {
        var cardWithoutNumber = CreditCardBuilder.make()
                .withNumber("4992 7398 716")
                .withHolder("Card Holder")
                .withIssuingBank("Bank")
                .withFutureExpiryDate()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithoutNumber, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.number\",\n" +
                "            \"message\": \"length must be between 16 and 19\"\n" +
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
    public void shoutsWhenCardHolderIsAbsent() throws Exception {
        var cardWithoutHolder = CreditCardBuilder.make()
                .withValidNumber()
                .withIssuingBank("Bank")
                .withFutureExpiryDate()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithoutHolder, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.holderName\",\n" +
                "            \"message\": \"is required\"\n" +
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
    public void shoutsWhenCardHolderIsEmpty() throws Exception {
        var cardWithoutHolder = CreditCardBuilder.make()
                .withValidNumber()
                .withHolder("")
                .withIssuingBank("Bank")
                .withFutureExpiryDate()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithoutHolder, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.holderName\",\n" +
                "            \"message\": \"is required\"\n" +
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
    public void shoutsWhenCardIssuingBankIsAbsent() throws Exception {
        CreditCard cardWithoutBank = CreditCardBuilder.make()
                .withHolder("Card Without Bank")
                .withValidNumber()
                .withFutureExpiryDate()
                .withValidCVV()
                .build();
        final var request = givenAFraudCheckRequestFor(cardWithoutBank, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.issuingBank\",\n" +
                "            \"message\": \"Issuing Bank name is required\"\n" +
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
    public void shoutsWhenCardIssuingBankIsEmpty() throws Exception {
        CreditCard cardWithoutBank = CreditCardBuilder.make()
                .withHolder("Card Without Bank")
                .withIssuingBank("")
                .withValidNumber()
                .withFutureExpiryDate()
                .withValidCVV()
                .build();
        final var request = givenAFraudCheckRequestFor(cardWithoutBank, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.issuingBank\",\n" +
                "            \"message\": \"Issuing Bank name is required\"\n" +
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
    public void shoutsWhenCardExpiryDateIsAbsent() throws Exception {
        CreditCard cardWithoutExpiryDate = CreditCardBuilder.make()
                .withHolder("Card Without Expiry Date")
                .withIssuingBank("Bank")
                .withValidNumber()
                .withValidCVV()
                .build();

        final var request = givenAFraudCheckRequestFor(cardWithoutExpiryDate, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.validUntil\",\n" +
                "            \"message\": \"Expiry Date is mandatory!\"\n" +
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
    public void shoutsWhenCardExpiryDateIsEmpty() throws Exception {
        final var requestBody = "{\n" +
                "    \"creditCard\" : {\n" +
                "        \"number\": \"4485 2847 2013 4093\",\n" +
                "        \"issuingBank\" : \"Bank of America\",\n" +
                "        \"holderName\" : \"Jumping Jack\",\n" +
                "        \"validUntil\" : \"\",\n" +
                "        \"cvv\" : 123\n" +
                "    },\n" +
                "    \"charge\" : {\n" +
                "        \"currency\" : \"INR\",\n" +
                "        \"amount\" : 1235.45\n" +
                "    }\n" +
                "}";
        final var request = givenAFraudCheckRequestFor(requestBody);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.validUntil\",\n" +
                "            \"message\": \"Expiry Date is mandatory!\"\n" +
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
    public void shoutsWhenCardCVVIsNotPresent() throws Exception {
        CreditCard cardWithoutCVV = CreditCardBuilder.make()
                .withHolder("Card Without CVV")
                .withIssuingBank("Bank")
                .withValidNumber()
                .withFutureExpiryDate()
                .build();
        final var request = givenAFraudCheckRequestFor(cardWithoutCVV, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.cvv\",\n" +
                "            \"message\": \"is mandatory!\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.cvv\",\n" +
                "            \"message\": \"must have 3 digits\"\n" +
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
    public void shoutsWhenCardCVVDoesNotContain3Digits() throws Exception {
        CreditCard cardWith2DigitCVV = CreditCardBuilder.make()
                .withHolder("Card With 2 Digit CVV")
                .withIssuingBank("Bank")
                .withValidNumber()
                .withFutureExpiryDate()
                .havingCVVDigits(2)
                .build();
        final var request = givenAFraudCheckRequestFor(cardWith2DigitCVV, charge);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"creditCard.cvv\",\n" +
                "            \"message\": \"must have 3 digits\"\n" +
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
    public void shoutsWhenAmountValueIsNotPresentInCharge() throws Exception {
        final var chargeWithoutAmount = new Money(Currency.getInstance("INR"), null);
        final var request = givenAFraudCheckRequestFor(validCard, chargeWithoutAmount);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"charge.amount\",\n" +
                "            \"message\": \"is required!\"\n" +
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
    public void shoutsWhenCurrencyIsNotPresentInCharge() throws Exception {
        final var chargeWithoutCurrency = new Money(null, 1234.56d);
        final var request = givenAFraudCheckRequestFor(validCard, chargeWithoutCurrency);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"charge.currency\",\n" +
                "            \"message\": \"is required!\"\n" +
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
    public void shoutsWhenCurrencyIsEmpty() throws Exception {
        var requestBody = "{\n" +
                "    \"creditCard\" : {\n" +
                "        \"number\": \"4485 2847 2013 4093\",\n" +
                "        \"holderName\" : \"Jumping Jack\",\n" +
                "        \"issuingBank\" : \"Bank of America\",\n" +
                "        \"validUntil\" : \"2020-10-04T01:00:26.874+00:00\",\n" +
                "        \"cvv\" : 123\n" +
                "    },\n" +
                "    \"charge\" : {\n" +
                "        \"currency\" : \"\",\n" +
                "        \"amount\" : 1235.45\n" +
                "    }\n" +
                "}";
        final var request = givenAFraudCheckRequestFor(requestBody);
        final ResultActions resultActions = whenTheRequestIsMade(request);
        final var response = "{\n" +
                "    \"validationErrors\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"charge.currency\",\n" +
                "            \"message\": \"is required!\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        final var content = MockMvcResultMatchers.content();
        thenExpect(resultActions,
                MockMvcResultMatchers.status().isBadRequest(),
                content.contentType(MediaType.APPLICATION_JSON),
                content.json(response));
    }

    private MockHttpServletRequestBuilder givenAFraudCheckRequestFor(CreditCard card, Money charge) throws JsonProcessingException {
        var payload = new FraudCheckPayload(card, charge);
        var requestBody = convertObjectToJson(payload);
        return givenAFraudCheckRequestFor(requestBody);
    }

    private String convertObjectToJson(Object payload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(payload);
    }

    private MockHttpServletRequestBuilder givenAFraudCheckRequestFor(String requestBody) {
        return MockMvcRequestBuilders.post("/check")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(requestBody);
    }

    private ResultActions whenTheRequestIsMade(MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private void thenExpect(ResultActions resultActions, ResultMatcher... matchers) throws Exception {
        resultActions.andExpect(ResultMatcher.matchAll(matchers));
    }
}
