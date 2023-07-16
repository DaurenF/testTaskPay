package kz.s1lk.pay.controller;

import jakarta.validation.Valid;
import kz.s1lk.pay.model.dto.CardHolderRequest;
import kz.s1lk.pay.model.dto.CreateCardRequest;
import kz.s1lk.pay.model.dto.ErrorResponse;
import kz.s1lk.pay.model.dto.TransferDto;
import kz.s1lk.pay.service.CardHolderService;
import kz.s1lk.pay.service.CardService;
import kz.s1lk.pay.util.HttpExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Slf4j
public class CardController {

    private final CardHolderService cardHolderService;
    private final CardService cardService;
    private final HttpExceptionHandler exceptionHandler;


    @GetMapping(value = "/healthcheck")
    public ResponseEntity<Object> healthcheck() {
        log.info("healthcheck");
        Map<String, String> map = new HashMap<>();
        map.put("status", "OK");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/create/card-holder")
    public ResponseEntity<Object> createCardHolder(@Valid @RequestBody CardHolderRequest request,
                                                   BindingResult result) {
        log.info("Client request to /api/card/create/card-holder " + request);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        return cardHolderService.createCardHolder(request);
    }


    @PostMapping("/create")
    public ResponseEntity<Object> createCard(@RequestHeader("Authorization") String token,
                                             @RequestParam("balance") BigDecimal balance,
                                             @Valid @RequestBody CreateCardRequest request,
                                             BindingResult result) throws InstanceAlreadyExistsException {
        log.info("Client request to /api/card/create/card " + request);
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        return cardService.createCard(request, balance, token);
    }

    @GetMapping("/balance")
    public ResponseEntity<Object> getCardBalance() {
        log.info("Client request to /api/card/balance");
        return cardService.getCardBalance();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Object> cardTransfer(@RequestHeader("Authorization") String token , @RequestBody TransferDto transferDto){
        log.info("Client request to /api/card/transfer");
        return cardService.makeCardTransfer(transferDto, token);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return exceptionHandler.handleException(e);
    }
}
