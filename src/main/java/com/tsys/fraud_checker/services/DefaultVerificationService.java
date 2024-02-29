package com.tsys.fraud_checker.services;

import com.tsys.fraud_checker.domain.CreditCard;
import com.tsys.fraud_checker.domain.FraudStatus;
import com.tsys.fraud_checker.domain.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Random;
import java.util.logging.Logger;

/**
 * https://reflectoring.io/bean-validation-with-spring-boot/
 * <p>
 * Instead of (or additionally to) validating input on the controller level,
 * we can also validate the input to any Spring components. In order to to this,
 * we use a combination of the @Validated and @Valid annotations:
 * <p>
 * The @Validated annotation is only evaluated on class level, so can’t put
 * it on a method.
 * <p>
 * In contrast to request body validation a failed validation will trigger a
 * ConstraintViolationException instead of a MethodArgumentNotValidException.
 * Spring does not register a default exception handler for this exception,
 * so it will by default cause a response with HTTP status
 * 500 (Internal Server Error).
 * <p>
 * If we want to return a HTTP status 400 instead (which makes sense, since
 * the client provided an invalid parameter, making it a bad request), we can
 * add a custom exception handler to our controller
 *
 * @see com.tsys.fraud_checker.web.advices.GlobalExceptionAdvice#onConstraintValidationException(ConstraintViolationException)
 * @see com.tsys.fraud_checker.web.advices.GlobalExceptionAdvice#onMethodArgumentNotValidException(MethodArgumentNotValidException)
 */
@Service
@Validated
public class DefaultVerificationService implements VerificationService {

    private static final Logger LOG = Logger.getLogger(DefaultVerificationService.class.getName());

    private Random random;

    @Autowired
    public DefaultVerificationService(Random random) {
        this.random = random;
    }

    int randomNumberBetween(int lower, int upper) {
        return random.nextInt(upper - lower) + lower;
    }

    private int verifyCVV(CreditCard creditCard) throws InterruptedException {
        final int sleepMillis = randomNumberBetween(2000, 5000);
        LOG.info(() -> String.format("{ 'verifyCVV() will respond after ' : '%.3f seconds' }", ((float) sleepMillis) / 1000));
        Thread.sleep(sleepMillis);
        final int index = randomNumberBetween(0, 2);
        LOG.info(() -> String.format("{ 'verifyCVV() Generating Response for value ==> ' : %d }", index));
        return index;
    }

    private int verifyAddressWithIssuingBank(CreditCard creditCard) {
        final int index = randomNumberBetween(0, 2);
        LOG.info(() -> String.format("{ 'verifyAddressWithIssuingBank() Generating Response for value ==> ' : %d }", index));
        return index;
    }

    @Override
    public FraudStatus verifyTransactionAuthenticity(@NotNull @Valid CreditCard card,
                                                     @NotNull @Valid Money charge) throws InterruptedException {
        return new FraudStatus(verifyCVV(card), verifyAddressWithIssuingBank(card), card.hasExpired());
    }
}