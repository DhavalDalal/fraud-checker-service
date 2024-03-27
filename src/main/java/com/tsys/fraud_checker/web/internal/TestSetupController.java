package com.tsys.fraud_checker.web.internal;

import com.tsys.fraud_checker.services.StubbedDelayVerificationService;
import com.tsys.fraud_checker.services.VerificationServiceRouter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

import static com.tsys.fraud_checker.services.VerificationServiceRouter.RouteTo.ACTUAL;
import static com.tsys.fraud_checker.services.VerificationServiceRouter.RouteTo.STUB;

@Profile("development")
@Tag(name = "Test Setup Controller", description = "Sets up Routing to Real or Stubbed Service for Fraud Checking with various parameters")
@Controller
@RequestMapping("/setup")
public class TestSetupController {

    private final StubbedDelayVerificationService stubbedDelayVerificationService;
    private final Stubs stubs;
    private final VerificationServiceRouter verificationServiceRouter;
    private static final Logger LOG = Logger.getLogger(TestSetupController.class.getName());

    @Autowired
    public TestSetupController(StubbedDelayVerificationService stubbedDelayVerificationService,
                               Stubs stubs, VerificationServiceRouter verificationServiceRouter) {
        this.stubbedDelayVerificationService = stubbedDelayVerificationService;
        this.stubs = stubs;
        this.verificationServiceRouter = verificationServiceRouter;
    }

    @Operation(description = "Am I alive?")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Got Health status", content = {
            @Content(schema = @Schema(title = "Health Status",
                implementation = String.class,
                minLength = 10,
                maxLength = 100,
                pattern = "^[a-zA-Z0-9_ ]*$"),
                mediaType = "application/json")
        })
    })
    @GetMapping(value = "/ping", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> pong() {
        return ResponseEntity.ok(String.format("{ \"PONG\" : \"%s is running fine!\" }", getClass().getSimpleName()));
    }

    // url for setting up stubbed response for a url with payload
    @Operation(description = "Stub Fraud Check payload request and its corresponding Fraud Status response.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody (
            content = {
                @Content(schema = @Schema(implementation = FraudCheckStub.class), mediaType = "application/json")
            }
        )
    )
    @PostMapping("/stubFor/check")
    ResponseEntity<String> stub(@RequestBody FraudCheckStub fraudCheckStub) {
        final var url = "/check";
        LOG.info(() -> String.format("Setting stub for url = %s, stub = %s", url, fraudCheckStub));
        stubs.put(url, fraudCheckStub);
        LOG.info("Stubs = " + stubs);
        return ResponseEntity.ok(String.format("{ \"url\" : %s, \"stub\" : \"%s\" }", url, fraudCheckStub));
    }

    @Operation(description = "Get All the set stubs")
    @GetMapping(path = "/getStubs", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Stubs> stubs() {
        return ResponseEntity.ok(stubs);
    }

    // url for setting up stubbed delay
    @Operation(description = "Setup delay time in millis for the response to be sent back for a fraud check request",
        parameters = {
            @Parameter(name = "respondIn", schema = @Schema(implementation = Integer.class), description = "An integer value, 0 is permissible for no delay")
        }
    )
    @GetMapping("/fraudCheckDelay")
    public ResponseEntity<Void> fraudCheckDelay(@RequestParam("respondIn")
                                                @Min(value = 0, message = "For No delay")
                                                @Max(value = 99999)
                                                int timeInMillis) {
        LOG.info(() -> String.format("Setting Delay to respond from VerificationService for %d", timeInMillis));
        stubbedDelayVerificationService.setDelay(timeInMillis);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // url for turning stubResponses on/off
    @Operation(description = "Switch the routing of request to Stub or Real Service",
        parameters = {
            @Parameter(name = "on",
                schema = @Schema(implementation = Boolean.class),
                description = "true for routing requests to stub, false to route request to real service.")
        }
    )
    @GetMapping("/stubbingFor/check")
    public ResponseEntity<Void> turnStubbingForFraudCheck(@RequestParam("on") boolean isEnabled) {
        if (isEnabled) {
            verificationServiceRouter.routeTo = STUB;
        } else {
            verificationServiceRouter.routeTo = ACTUAL;
        }
        LOG.info(String.format("Stubbing for Fraud Check is now turned %s, will route to %s", isEnabled ? "ON" : "OFF",
                verificationServiceRouter.routeTo));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}