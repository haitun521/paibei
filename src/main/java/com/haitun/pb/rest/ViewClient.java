package com.haitun.pb.rest;

import org.androidannotations.annotations.rest.Accept;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.api.rest.MediaType;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.io.IOException;

/**
 * Created by 笨笨 on 2015/7/25.
 */
@Rest(rootUrl ="http://122.206.78.33/pb", converters = {StringHttpMessageConverter.class},interceptors = {HttpBasicAuthenticatorInterceptor.class})
public interface ViewClient {
    @Get("/viewListSer?start={start}&num={num}&address={address}")
    @Accept(MediaType.APPLICATION_JSON)

    String geViews(int start,int num,String address );
}

class HttpBasicAuthenticatorInterceptor implements ClientHttpRequestInterceptor {
    public HttpBasicAuthenticatorInterceptor() {

    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] data, ClientHttpRequestExecution execution) throws IOException {


        return execution.execute(request, data);
    }
}
