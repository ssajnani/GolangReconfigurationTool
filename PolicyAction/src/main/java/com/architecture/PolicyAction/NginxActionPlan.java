package com.architecture.PolicyAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

public interface ActionPlan{

    public ArrayList<Action> getActions(ArrayList<Variables> variables);
    public String enactSteps();
}
