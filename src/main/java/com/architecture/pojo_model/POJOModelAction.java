package com.architecture.pojo_model;
public class POJOModelAction {
    AtomicAction action;
    String kubeType;
    public POJOModelAction(String type) {
	kubeType = type;
        if (type.equals("Pod")){
	    action = new PodAction();
	} else if (type.equals("Deployment")){
	    action = new DeploymentAction();
	} else if (type.equals("Service")) {
	    action = new ServiceAction();
	} else if (type.equals("Namespace")) {
	    action = new NamespaceAction();
	}
    }
    public String create(String[] variables) {
    	String result = action.create(variables);
	return result;
    }
    public String delete(String[] variables) {
    	String result = action.delete(variables);
	return result;
    }
    public String update(String[] variables) {
    	String result = action.update(variables);
	return result;
    }
    public Object read(String[] variables) {
    	Object result = action.read(variables);
	return result;
    }
}
