package com.architecture.PolicyAction;

public class TestNginxPlan implements PolicyPlan {
    private ActionPlan actions = new NginxActionPlan();

    public List<Variable> getVariables() {

    }	
    public Boolean evaluatePolicy();
    public void enactActionPlan();
}
