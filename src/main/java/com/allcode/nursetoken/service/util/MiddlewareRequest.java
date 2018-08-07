package com.allcode.nursetoken.service.util;

import java.util.List;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

public class MiddlewareRequest {

    public MiddlewareRequest(){
    }

    public static JSONObject get(String transactionUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(transactionUrl);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        JSONObject root = new JSONObject(response.getBody());
        return root;
    }

    public static JSONObject post(String transactionUrl,  List<String> params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        HttpEntity<String> httpEntity = new HttpEntity <String> (json.toString(), httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(transactionUrl, httpEntity, String.class);
        JSONObject root = new JSONObject(response);
        return root;
    }

}
