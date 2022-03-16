package com.batch.app;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.batch.app.message.FurikomiExecMessage;
import com.batch.domain.BatchService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/v1/batch")
public class BatchController {
	
	@Autowired
	BatchService batchService;
	@Autowired
	FurikomiExecMessage message;
	
	@PostConstruct
	public void BatchConstruct() {
		
		//kafka
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "myconsumergroup");
		com.batch.app.GlobalValueables.consumer = new KafkaConsumer<>(properties, new StringDeserializer(), new StringDeserializer());
		
		//ObjectMapper
		com.batch.app.GlobalValueables.mapper = new ObjectMapper();
	}
	
	//1→0へのメソッド
	@RequestMapping(value = "/halt")
	public void halt() {
		batchService.endProcess();
	}
	
	//非同期にして投げっぱなしにします
	//ステータスを0にすれば自動的に終了するためです
	//監視とスレッド数制御はしない代わりに、ステータスでの制御を行います
	@RequestMapping(value = "/start")
	@Async
	@Bean
	public void start() {
		//停止ステータス以外だったら終了
		if (batchService.checkStatus0() == false) {return;}
		//ステータスを「1」に変更
		batchService.startProcess();
		this.run();
	}
	
	//実態の監視処理
	//privateなのでstartメソッドからしか呼べない形にすることで制御しています
	private void run() {
		try {
			//待機
			STANDBY: while(true) {
				//決められた時間毎にチェック
				if (batchService.checkStatus2() == false) {
					Thread.sleep(com.batch.app.GlobalValueables.waitingTimeMillis);
					System.out.println("-----stand by async exec-----");
					//プロセスが0ステータスだったらプロセス終了
					if (batchService.checkStatus0() == true) {break STANDBY;}
					continue STANDBY;
				}
				System.out.println("----- async exec started !!! -----");
				//実行、DBQでいう「2」の状態
				EXECUTING: while(true) {
					try {
						//コンシューム
						com.batch.app.GlobalValueables.consumer.subscribe(Arrays.asList(com.batch.app.GlobalValueables.topicname));
						//1秒毎に確認し、コンティニューする
						ConsumerRecords<String, String> records = com.batch.app.GlobalValueables.consumer.poll(Duration.ofSeconds(1));
						if (records == null) {
							if (batchService.checkStatus2() == false) {break EXECUTING;}
							else {
								//待機
								Thread.sleep(com.batch.app.GlobalValueables.waitingTimeMillis);
								continue EXECUTING;
							}
						}
						
						for (ConsumerRecord<String, String> record : records) {
					        //ログ代わり
							System.out.println(String.format("%s:%s", record.offset(), record.value()));
							message = com.batch.app.GlobalValueables.mapper.readValue(record.value(),FurikomiExecMessage.class);
							batchService.furikomiConsume(message);
						}
						
						//2→1へ戻っていないかチェック
						if (batchService.checkStatus2() == false) {break EXECUTING;}
						else {continue EXECUTING;}
						
					} catch (Exception e) {
						e.printStackTrace();
						//握りつぶしてバッチ処理継続
						continue EXECUTING;
					}
				}
				//待機状態に戻る
				continue STANDBY;
			}
			
			//プロセス終了
			//再度起動したい場合はもう一回叩いてもらう必要あり
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("!!!!!Unrecognized Error Happend. Please Check StackTrace and details.!!!!!");
		}
	}
	
}
