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
public class ServiceAction implements AtomicAction {
	private ApiClient client;
	private CoreV1Api api;
	private POJOModelService modelService;
	public ServiceAction() {
	    try {
	        client = Config.defaultClient();
	        api = new CoreV1Api();
	        modelService = new POJOModelService();
	        Configuration.setDefaultApiClient(client);
	    } catch (Exception e) {
		System.out.println(e.getMessage());
	    }
	}
	public String create(String[] info){
	    if (info.length < 2){
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Service body = (V1Service) Yaml.load(new File(info[1]));
	            V1Service result = api.createNamespacedService(info[0], body, null, null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Service> serviceList = model.get("Service");
		    serviceList.add(result);
		    model.put("Service", serviceList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid service name and the namespace in a string array in that order.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
	            api.deleteNamespacedService(info[0], info[1], body, null, null, null,null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Service> serviceList = model.get("Service");
		    int listSize = serviceList.size();
		    for (int i = 0; i < listSize; i++) {
			V1Service service = serviceList.get(i);
			if (service.getMetadata().getName() == info[0] && service.getMetadata().getNamespace() == info[1]){
			    serviceList.remove(i);
			}
		    }
		    model.put("Service", serviceList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (ApiException e) {
		    return e.getMessage();
		}
	    }	
	} 
	public String update(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid service name, namespace, and yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Service body = (V1Service) Yaml.load(new File(info[2]));
	            V1Service result = api.patchNamespacedService(info[0], info[1], body, null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Service> serviceList = model.get("Service");
		    int listSize = serviceList.size();
		    for (int i = 0; i < listSize; i++) {
			V1Service service = serviceList.get(i);
			if (service.getMetadata().getName() == info[0] && service.getMetadata().getNamespace() == info[1]){
			    serviceList.set(i, result);
			}
		    }
		    model.put("Service", serviceList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public Object read(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid service name and namespace in a string array in that order.";
	    } else {
		try {
	            Yaml yaml = new Yaml();
	            V1Service result = api.readNamespacedService(info[0], info[1], null, null, null);
		    return result;
		} catch (ApiException e) {
		    return e.getMessage();
		}
	    }	
	}
}
