package kz.s1lk.pay.service.impl;

import kz.s1lk.pay.exception.CardHolderAlreadyExists;
import kz.s1lk.pay.model.dto.CardHolderRequest;
import kz.s1lk.pay.model.dto.CardHolderResponse;
import kz.s1lk.pay.model.entity.CardHolder;
import kz.s1lk.pay.repository.CardHolderRepository;
import kz.s1lk.pay.service.CardHolderService;
import kz.s1lk.pay.util.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardHolderServiceImpl implements CardHolderService {

    private final RestClient restClient;
    private final CardHolderRepository cardHolderRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${stripe.url}")
    private String stripeUrl;

    @Override
    public ResponseEntity<Object> createCardHolder(CardHolderRequest cardholderDTO) {
        this.checkForExistence(cardholderDTO.getEmail());

        ResponseEntity<CardHolderResponse> response = restClient.postRequest(stripeUrl + "/issuing/cardholders",
                cardholderDTO, CardHolderResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(this.createCardHolder(response.getBody(), cardholderDTO.getPassword()));
        }
        return ResponseEntity.badRequest().body(response.getBody());
    }


    private CardHolderResponse createCardHolder(CardHolderResponse response, String password) {
        CardHolder cardHolder = new CardHolder();
        BeanUtils.copyProperties(response, cardHolder, "id");
        cardHolder.setExternalId(response.getId());
        cardHolder.setPassword(passwordEncoder.encode(password));
        cardHolderRepository.save(cardHolder);
        return response;
    }

    private void checkForExistence(String email) {
        Optional<CardHolder> cardHolderWrap = cardHolderRepository.findByEmail(email);
        if (cardHolderWrap.isPresent())
            throw new CardHolderAlreadyExists("CardHolder with email " + email + " already exists", HttpStatus.BAD_REQUEST.value());
    }

}
