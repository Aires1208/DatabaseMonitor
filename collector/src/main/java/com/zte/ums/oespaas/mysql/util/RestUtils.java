package com.zte.ums.oespaas.mysql.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class RestUtils {
    private static final Logger logger = LoggerFactory.getLogger(RestUtils.class);

    public static String getGetResponseBody(String url, int successCode) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        return getResponseBody(httpGet, successCode);
    }

    public static boolean checkGetResponseCode(String url, int successCode) {
        HttpGet httpGet = new HttpGet(url);
        return checkResponseCode(httpGet, successCode);
    }

    public static String getPostResponseBody(String url, String json, int successCode){
        HttpPost httpPost = createHttpPost(url, json);
        return getResponseBody(httpPost, successCode);
    }

    public static boolean checkPostResponseCode(String url, String json, int successCode){
        HttpPost httpPost = createHttpPost(url, json);
        return checkResponseCode(httpPost, successCode);
    }

    public static String getPutResponseBody(String url, Map<String, String> params, int successCode) {
        HttpPut httpPut = createHttpPut(url, params);
        return getResponseBody(httpPut, successCode);
    }

    public static boolean checkPutResponseCode(String url, Map<String, String> params, int successCode) {
        HttpPut httpPut = createHttpPut(url, params);
        return checkResponseCode(httpPut, successCode);
    }

    public static String getDeleteResponseBody(String url, int successCode) {
        HttpDelete httpDelete = new HttpDelete(url);
        return getResponseBody(httpDelete, successCode);
    }

    public static boolean checkDeleteResponseCode(String url, int successCode) {
        HttpDelete httpDelete = new HttpDelete(url);
        return checkResponseCode(httpDelete, successCode);
    }

    private static HttpPost createHttpPost(String url, String json){
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity stringEntity = new StringEntity(json);
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return httpPost;
    }

    private static HttpPut createHttpPut(String url, Map<String, String> params){
        HttpPut httpPut = new HttpPut(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        try {
            httpPut.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }

        return httpPut;
    }

    private static String getResponseBody(HttpRequestBase request, int successCode) {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);

        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return "";
    }

    private static boolean checkResponseCode(HttpRequestBase requestBase, int successCode) {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);

        try {
            HttpResponse response = httpClient.execute(requestBase);
            if (response.getStatusLine().getStatusCode() == successCode) {
                return true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return false;
    }
}
