package com.tsys.fraud_checker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("development")
public class FraudCheckerApplicationSpecs {

  @Autowired
  private ApplicationContext context;

  @Test
  void contextLoads() {
    assertNotNull(context);
  }
}
