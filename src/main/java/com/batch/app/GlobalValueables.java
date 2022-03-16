package com.batch.app;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GlobalValueables {
	public static KafkaConsumer<String, String> consumer;
	public static ObjectMapper mapper;
	public static String topicname = "furikomi10";
	public static final long furikomiBatchNo = 10;
	public static final long waitingTimeMillis = 10000;
	public static final long waitingTimeSeconds = 10;

}
