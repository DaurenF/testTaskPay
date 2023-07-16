package kz.s1lk.pay.util;

import kz.s1lk.pay.model.dto.CardHolderRequest;
import kz.s1lk.pay.model.dto.TransferDto;
import kz.s1lk.pay.model.entity.Transfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestClient {

    private final RestTemplate restTemplate;

    @Value("${stripe.apikey}")
    private String apiKey;

    @Autowired
    public void configureRestTemplate() {
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
    }

    public <T> ResponseEntity<T> postRequest(String url, Object request, Class<T> responseType) {
        if(request instanceof TransferDto transferDto) return (ResponseEntity<T>) this.getTransfer(transferDto); //костыль для имитации перевода
        try {
            HttpHeaders headers = createHeaders();

            HttpEntity<MultiValueMap<String, Object>> requestEntity = convertToFormEntity(request, headers);

            logRequest(url, requestEntity);

            ResponseEntity<T> responseEntity = sendRequest(url, HttpMethod.POST, requestEntity, responseType);

            logResponse(responseEntity);

            return responseEntity;
        } catch (HttpClientErrorException e) {
            logError(e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.badRequest().body((T) e.getResponseBodyAsString());
        }
    }

    public <T> ResponseEntity<T> getRequest(String url, Class<T> responseType) {
        try {
            HttpHeaders headers = createHeaders();

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            logRequest(url, null);

            ResponseEntity<T> responseEntity = sendRequest(url, HttpMethod.GET, requestEntity, responseType);

            logResponse(responseEntity);

            return responseEntity;
        } catch (HttpClientErrorException e) {
            logError(e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    //todo is it a good practice for APPLICATION_FORM_URLENCODED type
    private <T> HttpEntity<MultiValueMap<String, Object>> convertToFormEntity(Object request, HttpHeaders headers) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        if(request instanceof CardHolderRequest){
            formData = this.convertToFormData(request);
            return new HttpEntity<>(formData, headers);
        }
        Field[] fields = request.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if(field.getName().equals("password")) continue;
                Object value = field.get(request);
                formData.add(field.getName(), value);
            } catch (IllegalAccessException e) {
                log.error("Error while converting request to form entity", e);
            }
        }

        return new HttpEntity<>(formData, headers);
    }

    private MultiValueMap<String, Object> convertToFormData(Object request) {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        if (request instanceof CardHolderRequest cardHolderRequest) {
            formData.add("name", cardHolderRequest.getName());
            formData.add("email", cardHolderRequest.getEmail());
            formData.add("phone_number", cardHolderRequest.getPhone_number());
            formData.add("status", cardHolderRequest.getStatus());
            formData.add("type", cardHolderRequest.getType());
            formData.add("individual[first_name]", cardHolderRequest.getIndividual().getFirst_name());
            formData.add("individual[last_name]", cardHolderRequest.getIndividual().getFirst_name());
            formData.add("individual[dob][day]", String.valueOf(cardHolderRequest.getIndividual().getDob().getDay()));
            formData.add("individual[dob][month]", String.valueOf(cardHolderRequest.getIndividual().getDob().getMonth()));
            formData.add("individual[dob][year]", String.valueOf(cardHolderRequest.getIndividual().getDob().getYear()));
            formData.add("billing[address][line1]", cardHolderRequest.getBilling().getAddress().getLine1());
            formData.add("billing[address][city]", cardHolderRequest.getBilling().getAddress().getCity());
            formData.add("billing[address][state]", cardHolderRequest.getBilling().getAddress().getState());
            formData.add("billing[address][postal_code]", cardHolderRequest.getBilling().getAddress().getPostal_code());
            formData.add("billing[address][country]", cardHolderRequest.getBilling().getAddress().getCountry());
        }
        return formData;
    }
    private ResponseEntity<Transfer> getTransfer(TransferDto transferDto){
        Transfer transfer = new Transfer();
        BeanUtils.copyProperties(transferDto, transfer, "id");
        transfer.setExternalId(UUID.randomUUID().toString());
        return ResponseEntity.ok(transfer);
    }
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(apiKey, "");
        return headers;
    }

    private <T> ResponseEntity<T> sendRequest(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    private void logRequest(String url, Object requestBody) {
        log.info("Sending request to: {}", url);
        log.info("Request Body: {}", requestBody != null ? requestBody.toString() : "N/A");
    }

    private <T> void logResponse(ResponseEntity<T> responseEntity) {
        log.info("Response Status: {}", responseEntity.getStatusCode());
        log.info("Response Body: {}", responseEntity.getBody());
    }

    private void logError(HttpStatusCode status, String responseBody) {
        log.error("Error Status: {}", status);
        log.error("Error Body: {}", responseBody);
    }
}
