package kz.s1lk.pay.service;

import kz.s1lk.pay.model.dto.CreateCardRequest;
import kz.s1lk.pay.model.dto.TransferDto;
import org.springframework.http.ResponseEntity;

import javax.management.InstanceAlreadyExistsException;
import java.math.BigDecimal;

public interface CardService {
    ResponseEntity<Object> createCard(CreateCardRequest createCardRequest, BigDecimal balance, String token) throws InstanceAlreadyExistsException;
    ResponseEntity<Object> getCardBalance();
    ResponseEntity<Object> makeCardTransfer(TransferDto transferDto, String token);
}
