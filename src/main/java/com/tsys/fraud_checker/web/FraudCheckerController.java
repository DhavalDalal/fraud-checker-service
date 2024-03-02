package com.tsys.fraud_checker.web;

import com.tsys.fraud_checker.domain.FraudStatus;
import com.tsys.fraud_checker.services.VerificationService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
//import springfox.documentation.annotations.ApiIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.logging.Logger;

@Tag(name = "Fraud Check Controller", description = "Checks for Credit Card Frauds using CVV and Address Check.")
// NOTE:
// =====
// Here I've not used @RestController as I want to display HTML
// page as well.  But at the same time, I want few of the methods
// to return non-HTML response, hence we annotate each method with
// @ResponseBody.
// In case, if there was no requirement to show HTML page, then
// this needs to be annotated with @RestController and not
// @Controller.  Once annotated with @RestController, you don't
// need @ResponseBody annotation.
@Controller

// NOTE:
// =====
// We have to add Spring’s @Validated annotation to the controller
// at class level to tell Spring to evaluate the constraint annotations
// on method parameters.
//
// The @Validated annotation is only evaluated on class level in this
// case, even though it’s allowed to be used on methods
//
// This was introduced, because the two GetMappings
// 1. validatePathVariable
// 2. validateRequestParameter
// needed it.
//
// It was not necessary when earlier had only the PostMapping
// 1. checkFraud
// It was enough to have @Valid annotation on the method
// parameter FraudCheckPayload.
@Validated
@RequestMapping("/")
//@Qualifier("verificationService")
public class FraudCheckerController {

    private static final Logger LOG = Logger.getLogger(FraudCheckerController.class.getName());

    private final VerificationService verificationService;

    @Autowired
    public FraudCheckerController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @Hidden
    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String index() {
        return "index.html";
    }

    @Operation(summary = "Am I alive?")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got Health status", content = { @Content(schema = @Schema(title = "Health Status", implementation = String.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "429", description = "Too Many Requests")
    })
    @GetMapping(value = "ping", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> pong() {
        return ResponseEntity.ok(String.format("{ \"PONG\" : \"%s is running fine!\" }", FraudCheckerController.class.getSimpleName()));
    }

    @Operation(summary = "Validate Path Variable")
    @ApiResponse(content = { @Content(mediaType = "text/plain") })
    // The @Parameter annotation is for the parameters of an API resource request,
    //  whereas @Schema is for properties of the model.
    @GetMapping("validatePathVariable/{id}")
    ResponseEntity<String> validatePathVariable(
            @PathVariable("id")
            @Min(value = 5, message = "A minimum value of 5 is required")
            @Max(value = 9999, message = "A maximum value of 9999 can be given")
            @Parameter(name = "id",
                schema = @Schema (implementation = Integer.class),
                description = "Value must be between 5 and 9999 (inclusive)",
                example = "1",
                required = true)
                    int id) {
        LOG.info(() -> String.format("validatePathVariable(), Got id = %d", id));
        return ResponseEntity.ok("valid");
    }

    @Operation(summary = "Validate Request Parameter",
        parameters = {
            @Parameter(name = "param", schema = @Schema(implementation = Integer.class), description = "Value must be between 5 and 9999 (inclusive)")
        }
    )
    @ApiResponse(content = { @Content(mediaType = "text/plain") })
    @GetMapping("validateRequestParameter")
    ResponseEntity<String> validateRequestParameter(
            @RequestParam("param")
            @Min(5) @Max(9999) int param) {
        LOG.info(() -> String.format("validateRequestParameter(), Got param = %d", param));
        return ResponseEntity.ok("valid");
    }

    @Operation(summary = "Validate Header Parameter",
       parameters = {
           @Parameter(name = "param",
               schema = @Schema(implementation = Integer.class),
               description = "Value must be between 5 and 9999 (inclusive)")
        }
    )
    @ApiResponse(content = { @Content(mediaType = "text/plain") })
    @GetMapping("validateHeaderParameter")
    ResponseEntity<String> validateHeaderParameter(
            @RequestHeader("param")
            @Min(5) @Max(9999) int param) {
        LOG.info(() -> String.format("validateHeader(), Got param = %d", param));
        return ResponseEntity.ok("valid");
    }

    @Operation(summary = "Validate Header Parameter Via Post",
        parameters = {
            @Parameter(name = "param", schema = @Schema(implementation = Integer.class), description = "Value must be between 5 and 9999 (inclusive)")
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody (
            content = {
                @Content(schema = @Schema(implementation = FraudCheckPayload.class), mediaType = "application/json")
            }
        )
    )
    @ApiResponse(content = { @Content(mediaType = "text/plain") })
    @PostMapping("validateHeaderParameterUsingPost")
    ResponseEntity<String> validateHeaderParameterUsingPost(
            @RequestHeader(value = "param")
            @Min(5) @Max(9999) int param,
            @RequestBody @Valid FraudCheckPayload fraudCheckPayload) {
        LOG.info(() -> String.format("validateHeaderParameterUsingPost(), Got param = %d", param));
        return ResponseEntity.ok("valid");
    }

    /**
     * https://reflectoring.io/bean-validation-with-spring-boot/
     * Bean Validation works by defining constraints to the fields
     * of a class by annotating them with certain annotations.
     * <p>
     * Then, you pass an object of that class into a Validator
     * which checks if the constraints are satisfied.
     * <p>
     * There are three things we can validate for any incoming HTTP request:
     * 1. the request body,
     *
     * @see FraudCheckerController#checkFraud(FraudCheckPayload)
     * 2. variables within the path (e.g. id in /foos/{id})
     * @see FraudCheckerController#validatePathVariable(int)
     * 3. query parameters.
     * @see FraudCheckerController#validateRequestParameter(int)
     * <p>
     * Use @Valid on Complex Types
     * If the Input class contains a field with another complex type that
     * should be validated, this field, too, needs to be annotated with
     * Valid.
     */

    @Operation(summary = "Check possibility of a fradulent transaction and return a status to the caller.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody (content = {
                        @Content(schema = @Schema(implementation = FraudCheckPayload.class),
                            mediaType = "application/json") }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got Fraud Status for the check", content = { @Content(schema = @Schema(title = "Fraud Status", implementation = FraudStatus.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping(value = "check", consumes = "application/json", produces = "application/json")
    public ResponseEntity<FraudStatus> checkFraud(
            @RequestBody @Valid FraudCheckPayload payload) {
        try {
            LOG.info(() -> String.format("{ 'checkFraud' : ' for chargedAmount %s on %s'}", payload.charge, payload.creditCard));
            FraudStatus fraudStatus = verificationService.verifyTransactionAuthenticity(payload.creditCard, payload.charge);
            LOG.info(() -> String.format("{ 'FraudStatus' : '%s'}", fraudStatus));
            final var httpHeaders = new HttpHeaders() {{
                setContentType(MediaType.APPLICATION_JSON);
            }};

            return new ResponseEntity<>(fraudStatus, httpHeaders, HttpStatus.OK);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}