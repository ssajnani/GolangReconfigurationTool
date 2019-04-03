package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Qualifier;
import shared.ServerResponse;
import shared.TimedResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class TestURL implements URL<Integer> {

    private final Adapter adapter;

    @Autowired
    public TestURL(@Qualifier("intAdapter2") Adapter adapter) {
        this.adapter = adapter;
    }

    @RequestMapping("/integer2")
    public CompletableFuture<TimedResponse<Integer>> getVariable() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        ServerResponse response = new ServerResponse();
        return adapter.getVariable()
                .thenApply(var -> {
                    response.setData(var);
                    response.setTimeMs(System.currentTimeMillis() - start);
                    response.setCompletingThread(Thread.currentThread().getName());
                    return response;
                });
    }

}
