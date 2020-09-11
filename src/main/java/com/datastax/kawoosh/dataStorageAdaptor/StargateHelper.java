package com.datastax.kawoosh.dataStorageAdaptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class StargateHelper {


    //Also used by stargate (package-private)
    static ObjectMapper mapper = new ObjectMapper();

    public static class Endpoints {
        public static String AUTH_TOKEN = String.format("%s/api/rest/v1/auth", ASTRA_URL);
        public static String COLLECTIONS = String.format("%s/api/rest/v2/namespaces/%s/collections", ASTRA_URL, ASTRA_DB_NS);
    }

    private static final String ASTRA_CLUSTER_ID = "f2aa8aba-0308-4caf-a96e-b37d9c343f94";
    private static final String ASTRA_CLUSTER_REGION = "us-east1";
    private static final String ASTRA_DB_USERNAME = "kawoosh";
    private static final String ASTRA_DB_NS = "kawoosh";
    private static final String ASTRA_DB_PASSWORD = "kawoosh";

    public static final String ASTRA_URL =
            String.format("https://%s-%s.apps.astra.datastax.com", ASTRA_CLUSTER_ID, ASTRA_CLUSTER_REGION);

    private static String authToken = "9ecb22a6-199f-4427-ab38-730834790865";

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private static HttpURLConnection astraConnect(String method, String endpoint) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        switch(method){
            case "GET":
                conn.setRequestMethod(method);
                break;
            case "PATCH":
                conn.setRequestMethod("POST");
                conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                break;
            case "PUT":
            case "POST":
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                break;
            default:
                throw new NotImplementedException("method not (yet?) implemented: "+method);
        }
        return conn;
    }

    private static void generateAuthToken() throws IOException, URISyntaxException {
        ObjectNode node = mapper.createObjectNode();
        node.put("username", ASTRA_DB_USERNAME);
        node.put("password", ASTRA_DB_PASSWORD);
        HttpPost auth = new HttpPost(new URI(Endpoints.AUTH_TOKEN));
        auth.setHeader(HttpHeaders.ACCEPT, "*/*");
        auth.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        auth.setHeader("x-cassandra-request-id", UUID.randomUUID().toString());
        auth.setHeader("x-cassandra-token", authToken);
        CloseableHttpResponse response = httpClient.execute(auth);
        authToken = mapper.readTree(response.getEntity().getContent()).get("authToken").asText();
    }



    static JsonNode insert(JsonNode data, String collectionName, String docId) throws IOException, URISyntaxException {
        if(authToken==null) generateAuthToken();
        HttpPatch insert = new HttpPatch(new URI(String.format("%s/%s/%s", Endpoints.COLLECTIONS, collectionName, docId)));
        insert.setHeader(HttpHeaders.ACCEPT, "*/*");
        insert.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        insert.setHeader("x-cassandra-request-id", UUID.randomUUID().toString());
        insert.setHeader("x-cassandra-token", authToken);
        insert.setEntity(new StringEntity(data.toString()));
        CloseableHttpResponse response = httpClient.execute(insert);
        InputStream cont = response.getEntity().getContent();
        return mapper.readTree(cont);
    }


    static JsonNode read(String collectionName, String docId) throws IOException, URISyntaxException {
        System.out.println(Endpoints.COLLECTIONS);
        HttpGet httpGet = new HttpGet(new URI(String.format("%s/%s/%s", Endpoints.COLLECTIONS, collectionName, docId)));
        httpGet.setHeader(HttpHeaders.ACCEPT, "*/*");
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpGet.setHeader("x-cassandra-request-id", UUID.randomUUID().toString());
        httpGet.setHeader("x-cassandra-token", authToken);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return mapper.readTree(response.getEntity().getContent());
    }
}
