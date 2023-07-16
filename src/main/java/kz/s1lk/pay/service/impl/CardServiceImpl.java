package kz.s1lk.pay.service.impl;

import kz.s1lk.pay.model.dto.*;
import kz.s1lk.pay.model.entity.Card;
import kz.s1lk.pay.model.entity.CardHolder;
import kz.s1lk.pay.repository.CardHolderRepository;
import kz.s1lk.pay.repository.CardRepository;
import kz.s1lk.pay.service.CardService;
import kz.s1lk.pay.util.JWTUtil;
import kz.s1lk.pay.util.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final JWTUtil jwtUtil;
    private final CardHolderRepository cardHolderRepository;
    private final RestClient restClient;
    private final CardRepository cardRepository;

    @Value("${stripe.url}")
    private String stripeUrl;

    @Override
    public ResponseEntity<Object> createCard(CreateCardRequest createCardRequest, BigDecimal balance, String token) throws InstanceAlreadyExistsException {
        String userEmail = jwtUtil.validateTokenAndRetrieveClaim(token.substring(7));
        Optional<CardHolder> cardHolderWrap = cardHolderRepository.findByEmail(userEmail);

        if (cardHolderWrap.isPresent()) {
            createCardRequest.setCardholder(cardHolderWrap.get().getExternalId());
            ResponseEntity<CreateCardResponse> response = restClient
                    .postRequest(stripeUrl + "/issuing/cards", createCardRequest, CreateCardResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                this.checkForExistence(Objects.requireNonNull(response.getBody()));
                Card card = this.createCard(response.getBody(), cardHolderWrap.get());
                return ResponseEntity.ok(setStartingBalance(card, balance, createCardRequest.getCurrency()));
            }
            return ResponseEntity.badRequest().body(response.getBody());
        }
        return ResponseEntity.badRequest().body("Current Card holder not found");
    }

    @Override
    public ResponseEntity<Object> getCardBalance() {
        ResponseEntity<BalanceDto> response = restClient
                .getRequest(stripeUrl + "/balance", BalanceDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(response.getBody());
        }
        return ResponseEntity.badRequest().body(response.getBody());
    }

    @Override
    public ResponseEntity<Object> makeCardTransfer(TransferDto transferDto, String token) {
        Card card = cardRepository.findByExternalId(transferDto.getFromCard())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        String userEmail = jwtUtil.validateTokenAndRetrieveClaim(token.substring(7));
        CardHolder cardHolder = cardHolderRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Card holder not found"));

        this.checkCardBelongsToUser(cardHolder, card);
        this.checkCardForTransfer(card, transferDto);

        ResponseEntity<Object> response = restClient
                .postRequest(stripeUrl + "/transfers", transferDto, Object.class);

        return ResponseEntity.ok(response.getBody());
    }

    private void checkCardBelongsToUser(CardHolder cardHolder, Card card) {
        if (card.getCardHolder() != cardHolder) throw new IllegalArgumentException("Card doesn't belong to user");
    }

    private void checkCardForTransfer(Card card, TransferDto transferDto) {
        if (card.getStatus().equals("BLOCKED")) throw new IllegalArgumentException("Card is blocked");
        if (card.getBalance().compareTo(transferDto.getAmount()) < 0)
            throw new IllegalArgumentException("Not enough money on card");
    }

    private Card setStartingBalance(Card card, BigDecimal balance, String currency) {
        FundBalanceRequest request = new FundBalanceRequest(balance, currency);
        ResponseEntity<Object> response = restClient
                .postRequest(stripeUrl + "/test_helpers/issuing/fund_balance", request, Object.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            card.setBalance(balance);
            return cardRepository.save(card);
        }
        return null;
    }

    private Card createCard(CreateCardResponse createCardResponse, CardHolder cardHolder) {
        Card card = new Card();
        card.setCardHolder(cardHolder);
        BeanUtils.copyProperties(createCardResponse, card, "id");
        card.setExternalId(createCardResponse.getId());
        return cardRepository.save(card);
    }

    private void checkForExistence(CreateCardResponse response) throws InstanceAlreadyExistsException {
        Optional<Card> cardWrap = cardRepository.findByExternalId(response.getId());
        if (cardWrap.isPresent())
            throw new InstanceAlreadyExistsException("Card with externalId " + response.getId() + " already exists");
    }
}
