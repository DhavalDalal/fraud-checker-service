package com.tsys.fraud_checker.services;

import com.tsys.fraud_checker.domain.CreditCard;
import com.tsys.fraud_checker.domain.CreditCardBuilder;
import com.tsys.fraud_checker.domain.FraudStatus;
import com.tsys.fraud_checker.domain.Money;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Currency;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
// This is a Unit Test, so why can't we run this as a Standalone test using
// just Mockito?
// This is because this service uses @Validated annotation to validate methods
// marked with @Valid...this combination requires a Spring advice to be applied
// at service-level.  Either we wire it by hand (lot-of-work) or use the next
// available option where we have WebApplicationContext available and Spring does it
// for us automatically.
// So, maybe we can use @WebMvcTest.

//@WebMvcTest
// But that too won't work here, because when I call the service method directly, I
// don't get a wrapped validation advice (provided out-of-box by Spring)
// and hence I cannot call the service methods directly. I'd need to use MockMvc
// to hit the controller and eventually get a call on this.
//
// So how do I test this Service directly (and as a Unit Test)?  Lets go to next
// level of Unit Tests in Spring that gives us WebApplicationContext, but still
// does not load the Web-Server by using @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//
// Using this I directly auto-wire the service in the test and I also get it
// wrapped in the Spring's default validation advice.
//
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Tag("UnitTest")
// Here’s a validation test at the service level
public class DefaultVerificationServiceTest {

  private static final int CVV_STATUS_PASS = 0;
  private static final int CVV_STATUS_FAIL = 1;
  private static final int ADDRESS_VERIFICATION_STATUS_PASS = 0;
  private static final int ADDRESS_VERIFICATION_STATUS_FAIL = 1;
  private final Money chargedAmount = new Money(Currency.getInstance("INR"), 1235.45d);
  private final CreditCard validCard = CreditCardBuilder.make()
      .withHolder("Jumping Jack")
      .withIssuingBank("Bank of Test")
      .withValidNumber()
      .withValidCVV()
      .withFutureExpiryDate()
      .build();

  @MockBean
  private Random random;
  @Autowired
  private DefaultVerificationService defaultVerificationService;

  @Test
  public void aValidCardPassesFraudCheck() throws InterruptedException {
    given(random.nextInt(anyInt()))
        .willReturn(-2000) // for sleepMillis
        .willReturn(CVV_STATUS_PASS)
        .willReturn(ADDRESS_VERIFICATION_STATUS_PASS);

    final var actualFraudStatus = defaultVerificationService.verifyTransactionAuthenticity(validCard, chargedAmount);
    final var expectedFraudStatus = new FraudStatus(CVV_STATUS_PASS, ADDRESS_VERIFICATION_STATUS_PASS, false);
    assertThat(actualFraudStatus).isEqualTo(expectedFraudStatus);
  }

  @Test
  public void aValidCardWithIncorrectCVVFailsFraudCheck() throws InterruptedException {
    given(random.nextInt(anyInt()))
        .willReturn(-2000) // for sleepMillis
        .willReturn(CVV_STATUS_FAIL)
        .willReturn(ADDRESS_VERIFICATION_STATUS_PASS);

    final var actualFraudStatus = defaultVerificationService.verifyTransactionAuthenticity(validCard, chargedAmount);
    final var expectedFraudStatus = new FraudStatus(CVV_STATUS_FAIL, ADDRESS_VERIFICATION_STATUS_PASS, false);
    assertThat(actualFraudStatus).isEqualTo(expectedFraudStatus);
  }

  @Test
  public void aValidCardWithIncorrectAddressFailsFraudCheck() throws InterruptedException {
    given(random.nextInt(anyInt()))
        .willReturn(-2000) // for sleepMillis
        .willReturn(CVV_STATUS_PASS)
        .willReturn(ADDRESS_VERIFICATION_STATUS_FAIL);

    final var actualFraudStatus = defaultVerificationService.verifyTransactionAuthenticity(validCard, chargedAmount);
    final var expectedFraudStatus = new FraudStatus(CVV_STATUS_PASS, ADDRESS_VERIFICATION_STATUS_FAIL, false);
    assertThat(actualFraudStatus).isEqualTo(expectedFraudStatus);
  }

  @Test
  public void anExpiredCardFailsFraudCheck() throws InterruptedException {
    given(random.nextInt(anyInt()))
        .willReturn(-2000) // for sleepMillis
        .willReturn(CVV_STATUS_PASS)
        .willReturn(ADDRESS_VERIFICATION_STATUS_PASS);

    final CreditCard expiredCard = CreditCardBuilder.make()
        .withHolder("Jumping Jack")
        .withIssuingBank("Bank of Test")
        .withValidNumber()
        .withValidCVV()
        .withPastExpiryDate()
        .build();

    final var actualFraudStatus = defaultVerificationService.verifyTransactionAuthenticity(expiredCard, chargedAmount);
    final var expectedFraudStatus = new FraudStatus(CVV_STATUS_PASS, ADDRESS_VERIFICATION_STATUS_PASS, true);
    assertThat(actualFraudStatus).isEqualTo(expectedFraudStatus);
  }
}