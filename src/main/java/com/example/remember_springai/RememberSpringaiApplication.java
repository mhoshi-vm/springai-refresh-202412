package com.example.remember_springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class RememberSpringaiApplication {


	public static void main(String[] args) {
		SpringApplication.run(RememberSpringaiApplication.class, args);
	}

}


@Configuration
class ChatConfig{
	@Bean
	ChatClient chatClient(ChatClient.Builder builder){
		return builder.build();
	}

	@Bean
	CommandLineRunner demo(VectorStore vectorStore){
		return (args) -> {
			List<Document> documents = List.of(
					new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
					new Document("The World is Big and Salvation Lurks Around the Corner"),
					new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

			// Add the documents to PGVector
			vectorStore.add(documents);

			// Retrieve documents similar to a query
			List<Document> results = vectorStore.similaritySearch(SearchRequest.query("Spring").withTopK(5));
		};
	}
}

@RestController
class ChatController{
	ChatClient chatClient;

	VectorStore vectorStore;

	public ChatController(ChatClient chatClient, VectorStore vectorStore) {
		this.chatClient = chatClient;
		this.vectorStore = vectorStore;
	}

	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<String> generation(
			@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		return this.chatClient.prompt()
				.user(message)
				.advisors(new QuestionAnswerAdvisor(vectorStore))
				.stream()
				.content();
	}


}

