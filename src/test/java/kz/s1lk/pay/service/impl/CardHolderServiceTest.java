package kz.s1lk.pay.service.impl;

import kz.s1lk.pay.model.dto.CardHolderRequest;
import kz.s1lk.pay.model.dto.CardHolderResponse;
import kz.s1lk.pay.repository.CardHolderRepository;
import kz.s1lk.pay.service.impl.CardHolderServiceImpl;
import kz.s1lk.pay.util.RestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class CardHolderServiceTest {
    @Mock
    private RestClient restClient;
    @Mock
    private CardHolderRepository cardHolderRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testCreateCardHolder_success() {
        CardHolderRequest cardholderDTO = new CardHolderRequest();
        cardholderDTO.setEmail("john.doe@example.com");
        cardholderDTO.setPassword("password");

        ResponseEntity<Object> response = new ResponseEntity<>(new CardHolderResponse(), HttpStatus.OK);
        Mockito.when(restClient.postRequest(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(response);

        ResponseEntity<Object> result = new CardHolderServiceImpl(restClient, cardHolderRepository, passwordEncoder).createCardHolder(cardholderDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }


    @Test
    void testCreateCardHolder_stripeError() {
        CardHolderRequest cardholderDTO = new CardHolderRequest();
        cardholderDTO.setEmail("john.doe@example.com");
        cardholderDTO.setPassword("password");

        ResponseEntity<Object> response = new ResponseEntity<>(new CardHolderResponse(), HttpStatus.BAD_REQUEST);
        Mockito.when(restClient.postRequest(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(response);

        ResponseEntity<Object> result = new CardHolderServiceImpl(restClient, cardHolderRepository, passwordEncoder).createCardHolder(cardholderDTO);

        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
