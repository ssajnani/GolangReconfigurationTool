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
public class PodAction implements AtomicAction {
        private ApiClient client;
        private CoreV1Api api;
        private POJOModelService modelService;
        public PodAction(){
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
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
		    V1Pod body = (V1Pod) Yaml.load(new File(info[1]));
	            V1Pod result = api.createNamespacedPod(info[0], body, null, null, null);
		    Map<String, ArrayList> model = modelService.getAllComponents();
                    ArrayList<V1Pod> podList = model.get("Pod");
		    podList.add(result);
		    model.put("Pod", podList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid pod name and the namespace in a string array in that order.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
	            api.deleteNamespacedPod(info[0], info[1], body, null, null, null,null, null);
                    Map<String, ArrayList> model = modelService.getAllComponents();
                    ArrayList<V1Pod> podList = model.get("Pod");
		    int listSize = podList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Pod pod = podList.get(i);
			if (pod.getMetadata().getName() == info[0] && pod.getMetadata().getNamespace() == info[1]){
			    podList.remove(i);
			}
                    }
		    model.put("Pod", podList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    if (e.getMessage().contains("java.lang.IllegalStateException: Expected a string but was BEGIN_OBJECT")) {
		        return "Success";
		    } else {
			return e.getMessage();
		    }
		}
	    }	
	} 
	public String update(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid pod name, namespace, and yaml file path in a string array in that order.";
	    } else {
		try {
		    V1Pod body = (V1Pod) Yaml.load(new File(info[2]));
	            V1Pod result = api.patchNamespacedPod(info[0], info[1], body, null, null);
                    Map<String, ArrayList> model = modelService.getAllComponents();
                    ArrayList<V1Pod> podList = model.get("Pod");
		    int listSize = podList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Pod pod = podList.get(i);
			if (pod.getMetadata().getName() == info[0] && pod.getMetadata().getNamespace() == info[1]){
			    podList.set(i, result);
			}
                    }
		    model.put("Pod", podList);
		    modelService.setAllComponents(model);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public Object read(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid pod name and namespace in a string array in that order.";
	    } else {
		try {
	            Yaml yaml = new Yaml();
	            V1Pod result = api.readNamespacedPod(info[0], info[1], null, null, null);
		    return result;
		} catch (ApiException e) {
		    return e.getMessage();
		}
	    }	
	}
}
