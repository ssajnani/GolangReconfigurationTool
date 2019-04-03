package com.architecture.pojo_model;
import java.util.*;
import io.kubernetes.client.util.Yaml;
import java.io.*;
import com.google.gson.*;
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
	    if (info.length < 3){
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Service body = (V1Service) Yaml.load(new File(info[1]));
	            V1Service result = body;
                    Boolean run = false;
                    if (info[2] != null && info[2].equals("All")){
                        run = true;
                    } else { 
	                result = api.createNamespacedService(info[0], body, null, null, null);
		    }
		    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    ArrayList<V1Service> serviceList = model.get("Service");
		    serviceList.add(result);
		    model.put("Service", serviceList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid service name and the namespace in a string array in that order.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
                    Boolean run = false;
                    if (info[2] != null && info[2].equals("All")){
                        run = true;
                    } else { 
	                api.deleteNamespacedService(info[0], info[1], body, null, null, null,null, null);
		    }
		    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    ArrayList<V1Service> serviceList = model.get("Service");

		    int listSize = serviceList.size();
		    Boolean found = false;
		    for (int i = 0; i < listSize; i++) {
			V1Service service = serviceList.get(i);
			if (service.getMetadata().getName() == info[0] && service.getMetadata().getNamespace() == info[1]){
			    serviceList.remove(i);
			    found = true;
			    break;
			}
		    }
		    if (!found) {
			return "Service " + info[0] + " does not exist.";
		    } else {
		        model.put("Service", serviceList);
		        modelService.setAllComponents(model, run);
		        return "Success";
		    }
		} catch (ApiException e) {
		    return e.getMessage();
		}
	    }	
	} 
	public String update(String[] info) {
	    if (info.length < 4){
	    	return "Error: Please provide a valid service name, namespace, and patch string in a string array in that order.";
	    } else {
		try {
		    ArrayList<JsonObject> arr = new ArrayList<>();
                    arr.add(((JsonElement) modelService.deserialize(info[2], JsonElement.class)).getAsJsonObject());
	            V1Service result = api.patchNamespacedService(info[0], info[1], arr, null, info[3]);
                    Boolean run = false;
                    if (info[3] != null && info[3].equals("All")){
                        run = true;
                    } 	
		    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    ArrayList<V1Service> serviceList = model.get("Service");
		    int listSize = serviceList.size();
		    for (int i = 0; i < listSize; i++) {
			V1Service service = serviceList.get(i);
			if (service.getMetadata().getName().equals(info[0]) && service.getMetadata().getNamespace().equals(info[1])){
			    serviceList.set(i, result);
			}
		    }
		    model.put("Service", serviceList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public Object read(String[] info) {
	    if (info.length < 3){
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
