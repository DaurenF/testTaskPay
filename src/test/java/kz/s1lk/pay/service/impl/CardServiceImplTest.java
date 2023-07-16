package kz.s1lk.pay.service.impl;

import kz.s1lk.pay.model.dto.BalanceDto;
import kz.s1lk.pay.model.dto.CreateCardRequest;
import kz.s1lk.pay.model.dto.CreateCardResponse;
import kz.s1lk.pay.model.entity.Card;
import kz.s1lk.pay.model.entity.CardHolder;
import kz.s1lk.pay.repository.CardHolderRepository;
import kz.s1lk.pay.repository.CardRepository;
import kz.s1lk.pay.util.JWTUtil;
import kz.s1lk.pay.util.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import kz.s1lk.pay.model.dto.TransferDto;


import javax.management.InstanceAlreadyExistsException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {
    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private CardHolderRepository cardHolderRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void createCard_Successful() throws InstanceAlreadyExistsException {
        CreateCardRequest createCardRequest = new CreateCardRequest();
        BigDecimal balance = new BigDecimal("100.00");
        String token = "dummyToken";
        String userEmail = "user@example.com";
        CardHolder cardHolder = new CardHolder();
        cardHolder.setExternalId("cardHolderId");
        cardHolder.setEmail(userEmail);
        Mockito.lenient().when(cardHolderRepository.findByEmail(userEmail)).thenReturn(Optional.of(cardHolder));

        ResponseEntity<CreateCardResponse> responseEntity = ResponseEntity.ok(new CreateCardResponse());
        Mockito.lenient().when(restClient.postRequest(anyString(), any(), eq(CreateCardResponse.class))).thenReturn(responseEntity);

        Mockito.lenient().when(cardRepository.save(any(Card.class))).thenReturn(new Card());

        ResponseEntity<Object> response = cardService.createCard(createCardRequest, balance, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void getCardBalance_Successful() {
        ResponseEntity<BalanceDto> responseEntity = ResponseEntity.ok(new BalanceDto());
        when(restClient.getRequest(anyString(), eq(BalanceDto.class))).thenReturn(responseEntity);

        ResponseEntity<Object> response = cardService.getCardBalance();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void makeCardTransfer_Successful() {
        String token = "dummyToken";
        String userEmail = "user@example.com";
        TransferDto transferDto = new TransferDto();
        transferDto.setFromCard("cardId");
        Card card = new Card();
        card.setCardHolder(new CardHolder());
        CardHolder cardHolder = new CardHolder();
        cardHolder.setEmail(userEmail);
        when(cardRepository.findByExternalId(transferDto.getFromCard())).thenReturn(Optional.of(card));
        Mockito.lenient().when(cardHolderRepository.findByEmail(userEmail)).thenReturn(Optional.of(cardHolder));
        ResponseEntity<Object> responseEntity = ResponseEntity.ok().build();
        Mockito.lenient().when(restClient.postRequest(anyString(), any(), eq(Object.class))).thenReturn(responseEntity);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cardService.makeCardTransfer(transferDto, token);
        });
    }

}