package com.architecture.pojo_model;
import java.util.*;
import io.kubernetes.client.util.Yaml;
import java.io.*;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.*;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
public class DeploymentAction implements AtomicAction {
        private ApiClient client;
        private AppsV1Api api;
        private POJOModelService modelService;
        public DeploymentAction(){
            try {
               client = Config.defaultClient();
               api = new AppsV1Api();
               modelService = new POJOModelService();
               Configuration.setDefaultApiClient(client);
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }	
	public String create(String[] info){
	    if (info.length < 2){
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Deployment body = (V1Deployment) Yaml.load(new File(info[1]));
	            V1Deployment result = api.createNamespacedDeployment(info[0], body, null, null, null);
		    V1PodTemplateSpec spec = result.getSpec().getTemplate();
		    System.out.println(spec);
		    Map<String, ArrayList> model = modelService.getAllComponents();
                    ArrayList<V1Deployment> deploymentList = model.get("Deployment");
		    deploymentList.add(result);
		    model.put("Deployment", deploymentList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid deployment name and the namespace in a string array in that order.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
	            api.deleteNamespacedDeployment(info[0], info[1], body, null, null,null,null, null);
                    Map<String, ArrayList> model = modelService.getAllComponents();
                    ArrayList<V1Deployment> deploymentList = model.get("Deployment");
		    int listSize = deploymentList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Deployment deployment = deploymentList.get(i);
			if (deployment.getMetadata().getName() == info[0] && deployment.getMetadata().getNamespace() == info[1]){
			    deploymentList.remove(i);
			}
                    }
		    model.put("Deployment", deploymentList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }
	} 
	public String update(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid deployment name, namespace, and yaml file path in a string array in that order.";
	    } else {
		try {
	            Yaml yaml = new Yaml();
	            V1Deployment body = (V1Deployment) Yaml.load(new File(info[2]));
	            V1Deployment result = api.patchNamespacedDeployment(info[0], info[1], body, null, null);
                    Map<String, ArrayList> model = modelService.getAllComponents();
                    ArrayList<V1Deployment> deploymentList = model.get("Deployment");
		    int listSize = deploymentList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Deployment deployment = deploymentList.get(i);
			if (deployment.getMetadata().getName() == info[0] && deployment.getMetadata().getNamespace() == info[1]){
			    deploymentList.set(i, result);
			}
                    }
		    model.put("Deployment", deploymentList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public Object read(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid deployment name and namespace in a string array in that order.";
	    } else {
		try {
	            Yaml yaml = new Yaml();
	            V1Deployment result = api.readNamespacedDeployment(info[0], info[1], null, null, null);
		    return result;
		} catch (ApiException e) {
		    return e.getMessage();
		}
	    }	
	}
}
