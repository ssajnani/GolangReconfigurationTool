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
public class NamespaceAction implements AtomicAction {
	private ApiClient client;
	private CoreV1Api api;
	private POJOModelService modelService;
	public NamespaceAction(){
	    try {
               client = Config.defaultClient();
	       api = new CoreV1Api();
	       modelService = new POJOModelService();
	       Configuration.setDefaultApiClient(client);
	    } catch(Exception e){
		System.out.println(e.getMessage());
	    }
	}
	public String create(String[] info){
	    if (info.length < 2){
	    	return "Error: Please provide the yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Namespace body = (V1Namespace) Yaml.load(new File(info[0]));
	            V1Namespace result = api.createNamespace(body, null, null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Namespace> namespaceList = model.get("Namespace");
		    namespaceList.add(result);
		    model.put("Namespace", namespaceList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid namespace in a string array.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
	            api.deleteNamespace(info[0], body, null, null, null, null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Namespace> namespaceList = model.get("Namespace");
		    int listSize = namespaceList.size();
		    for (int i = 0; i < listSize; i++) {
			V1Namespace namespace = namespaceList.get(i);
			if (namespace.getMetadata().getName() == info[0]){
			    namespaceList.remove(i);
			}
	            }
		    model.put("Namespace", namespaceList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	} 
	public String update(String[] info) {
	    if (info.length < 4){
	    	return "Error: Please provide a valid namespace name and yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Namespace body = (V1Namespace) Yaml.load(new File(info[1]));
	            V1Namespace result = api.patchNamespace(info[0], body, null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Namespace> namespaceList = model.get("Namespace");
		    int listSize = namespaceList.size();
		    for (int i = 0; i < listSize; i++) {
			V1Namespace namespace = namespaceList.get(i);
			if (namespace.getMetadata().getName() == info[0]){
			    namespaceList.set(i, result);
			}
	            }
		    model.put("Namespace", namespaceList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public Object read(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid namespace name and namespace in a string array in that order.";
	    } else {
		try {
	            Yaml yaml = new Yaml();
	            V1Namespace result = api.readNamespace(info[0], null, null, null);
		    return result;
		} catch (ApiException e) {
		    return e.getMessage();
		}
	    }	
	}
}
