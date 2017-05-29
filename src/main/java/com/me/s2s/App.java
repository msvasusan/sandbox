package com.me.s2s;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Hello world!
 *
 */
public class App {

    private final static String MY_URL = "https://my.url";

    public static void main( String[] args ) throws Exception {
        System.out.println(getToken());
        //sendPost();
    }

    private static String getToken() throws Exception {
        //Reference: https://stackoverflow.com/questions/30250102/any-apache-httpclient-4-4-example-for-trust-self-signed-certificates

        SSLContext sslcontext = SSLContexts.custom ().loadTrustMaterial(
                                                            null,
                                                            new TrustStrategy() {
                                                                public boolean isTrusted (X509Certificate[] chain, String authType ) throws CertificateException {
                                                                    return true;
                                                                }
                                                            })
                                                    .build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory (sslcontext, null, null, new NoopHostnameVerifier());

        HttpClient httpClient = HttpClients
                .custom()
                .setSSLSocketFactory ( sslConnectionSocketFactory )
                .build();

        HttpPost httpPost = new HttpPost(MY_URL);
        httpPost.addHeader("Content-Type", "application/json");
        StringEntity input = new StringEntity("{\"username\":\"a_user\",\"password\":\"a_password\"}");
        input.setContentType("application/json");
        httpPost.setEntity(input);

        HttpResponse response1 = httpClient.execute(httpPost);

        String responseBody = EntityUtils.toString(response1.getEntity());
        System.out.println(responseBody);

        JSONObject responseJSONObj = new JSONObject(responseBody);
        return responseJSONObj.getString("token");
    }

    private static int sendPost() throws Exception {

        System.out.println("send post started .....");

        String inputLine;
        System.setProperty("ssl.truststore.location",
                "/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.121-0.b13.29.amzn1.x86_64/jre/lib/security/cacerts");
        System.setProperty("ssl.truststore.password", "changeit");
        System.setProperty("ssl.enabled.protocols", "TLSv1.2");

        System.setProperty("security.protocol", "SASL_SSL");
        SSLContext sslcontext = SSLContexts.custom().useProtocol("TLSv1.1").build();
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpPost httpPost = new HttpPost(MY_URL);
        httpPost.addHeader("Content-Type", "application/json");

        final HttpClientBuilder httpClientBuilder = HttpClients.custom();

        CloseableHttpClient httpClient = httpClientBuilder.setSSLSocketFactory(factory).build();

        StringEntity input = new StringEntity("{\"username\":\"a_user\",\"password\":\"a_password\"}");
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response1 = httpClient.execute(httpPost);
        String resStr = EntityUtils.toString(response1.getEntity());
        System.out.println(resStr);
        return 0;

    }

}
