package it.uk.co.mattwhitaker.atlassian.jiraserveragileextended.admin;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import static org.junit.Assert.*;


public class AdminServletFuncTrdTest {

    CloseableHttpClient httpClient;
    String baseUrl;
    String servletUrl;

    @Before
    public void setup() {
        httpClient = HttpClientBuilder.create().build();
        baseUrl = System.getProperty("baseurl");
        servletUrl = baseUrl + "/plugins/servlet/jiraagileextended/admin";
    }

    @After
    public void tearDown() {
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSomething() throws IOException {
        HttpGet httpget = new HttpGet(servletUrl);

        // Create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpClient.execute(httpget, responseHandler);
        assertTrue(null != responseBody && !"".equals(responseBody));
    }
}
