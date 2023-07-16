package kz.s1lk.pay.service;

import kz.s1lk.pay.model.dto.CardHolderRequest;
import org.springframework.http.ResponseEntity;

public interface CardHolderService {
    ResponseEntity<Object> createCardHolder(CardHolderRequest cardholderDTO);

}
