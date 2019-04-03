package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class TestAdapter implements Adapter<Integer>{

    private Integer variable = 101; 
    private static final Logger logger = LoggerFactory.getLogger(TestAdapter.class);

    private final RestTemplate restTemplate;


    public TestAdapter(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<Integer> getVariable() throws InterruptedException {
        // Artificial delay of 1s for demonstration purposes
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(variable);
    }
    
    public void setVariable(Integer var) {
	variable = var;
    }
}
