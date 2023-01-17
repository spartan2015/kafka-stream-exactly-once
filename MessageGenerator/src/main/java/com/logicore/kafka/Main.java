package com.logicore.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.PaymentMessage;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

public class Main {

    static Long key = 1L;
    public static void main(String[] args) throws IOException, InterruptedException {

//        try {
//            updatePartitions("validate");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        ObjectMapper objectMapper = new ObjectMapper();
        final KafkaProducer<String, String> producer;
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("enable.idempotence", true);
        props.put("max.in.flight.requests.per.connection", 1);
//        props.put("transactional.id", "my-transactional-id");
        producer = new KafkaProducer<>(props);
//        producer.initTransactions();

        int i = 0;
        while (i<2) {
            PaymentMessage paymentMessage = generateNextMessage();
            producer.send(new ProducerRecord<>("message", paymentMessage.getKey(), paymentMessage.getValue()));
//            producer.commitTransaction();
            i++;
            Thread.sleep(10);
        }
    }


    public static void updatePartitions(String partition) throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient client = AdminClient.create(props);
        NewPartitions newPartitions = NewPartitions.increaseTo(5);
        client.createPartitions(Collections.singletonMap(partition, newPartitions)).all().get();
    }

    public static void createTopicPartitions() throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient client = AdminClient.create(props);
        NewTopic topic = new NewTopic("message", 10, (short) 1);
        client.createTopics(Collections.singleton(topic)).all().get();
    }

    public static PaymentMessage generateNextMessage() {
        String message = getRandomString();
        PaymentMessage paymentMessage = new PaymentMessage();
        paymentMessage.setKey(key.toString());
        paymentMessage.setValue(message);
        key = key + 1;
        return paymentMessage;
    }

    public static void sendKafkaMessage(String topic, String message) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        producer.send(new ProducerRecord<>(topic, message));
        producer.close();
    }

    public static String getRandomString() {
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sb.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public static void sendRequest(Object object, String url) throws IOException, InterruptedException {
        // Create an HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Convert the Java object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);

        // Create a POST request with a JSON body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Print the response status and body
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
