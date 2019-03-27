package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class StringAdapter implements Adapter<String>{

    private String variable = "test123"; 
    private static final Logger logger = LoggerFactory.getLogger(StringAdapter.class);

    private final RestTemplate restTemplate;

    public StringAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<String> getVariable() throws InterruptedException {
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(variable);
    }
    
    public void setVariable(String var) {
	variable = var;
    }
}
