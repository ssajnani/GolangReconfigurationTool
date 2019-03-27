package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shared.ServerResponse;
import shared.TimedResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface URL<T> {


    public CompletableFuture<TimedResponse<T>> getVariable() throws InterruptedException, ExecutionException;
}
