package com.architecture.PolicyAction;

public interface PolicyPlan {
    public List<Variable> getVariables();
    public Boolean evaluatePolicy();
    public void enactActionPlan();
}
