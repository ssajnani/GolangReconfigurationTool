package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shared.ServerResponse;
import shared.TimedResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class GitHubController {

    private final GitHubLookupService lookupService;

    @Autowired
    public GitHubController(GitHubLookupService lookupService) {
        this.lookupService = lookupService;
    }

    @RequestMapping("/user/{name}")
    public CompletableFuture<TimedResponse<User>> findUser(@PathVariable(value = "name") String name) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        ServerResponse response = new ServerResponse();
        return lookupService.findUser(name)
                .thenApply(user -> {
                    response.setData(user);
                    response.setTimeMs(System.currentTimeMillis() - start);
                    response.setCompletingThread(Thread.currentThread().getName());
                    return response;
                });
    }

}
