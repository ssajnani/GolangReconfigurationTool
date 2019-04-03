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
import java.lang.reflect.Method;
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
	            V1Namespace result = api.createNamespace(body, null, null, info[1]);
		    System.out.println(result.getMetadata());
		    Boolean run = false;
		    if (info[1] != null && info[1].equals("All")){
			run = true;
		    }
		    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    ArrayList<V1Namespace> namespaceList = model.get("Namespace");
		    namespaceList.add(result);
		    model.put("Namespace", namespaceList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    e.printStackTrace();
		    return "Failed";
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 2){
	    	return "Error: Please provide a valid namespace in a string array.";
	    } else {
		Boolean run = false;
		if (info[1] != null && info[1].equals("All")){
		    run = true;
		} else {
		    try {
		        V1DeleteOptions body = new V1DeleteOptions();
	                api.deleteNamespace(info[0], body, null, null, null, null, null);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		
		try{
		    Map<String, ArrayList> model = modelService.getAllComponents();
		    ArrayList<V1Namespace> namespaceList = model.get("Namespace");
                    Map<String, Map<String, Map<String, ArrayList>>> relationships = modelService.getRelationships(run);
		    int listSize = namespaceList.size();
		    Boolean found = false;
		    for (int i = 0; i < listSize; i++) {
			V1Namespace namespace = namespaceList.get(i);
			if (namespace.getMetadata().getName().equals(info[0])){
			    namespaceList.remove(i);
			    found = true;
			    break;
			}
	            }
		    for (String key : relationships.get("Namespace").keySet()) {
			if (key.equals(info[0])){
			    relationships.get("Namespace").remove(info[0]);
			}
		    }
		    updateDeleteModel(info[0], run);
		    if (found) {
		       model.put("Namespace", namespaceList);
		       modelService.setAllComponents(model);
		       return "Success";
		    } else {
		       return "Unable to find the given namespace: " + info[0];
		    }
		} catch(Exception e) {
		    return "Failed";
		}
	    }	
	} 
	public String update(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid namespace name, and patch string in a string array in that order.";
	    } else {
		try {
		    ArrayList<JsonObject> arr = new ArrayList<>();
	            arr.add(((JsonElement) modelService.deserialize(info[1], JsonElement.class)).getAsJsonObject());
		    V1Namespace result = api.patchNamespace(info[0], arr, null, info[2]);
		    Boolean run = false;
		    if (info[2] != null && info[2].equals("All")){
		        run = true;
		    }
		    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    ArrayList<V1Namespace> namespaceList = model.get("Namespace");
		    int listSize = namespaceList.size();
		    for (int i = 0; i < listSize; i++) {
			V1Namespace namespace = namespaceList.get(i);
			if (namespace.getMetadata().getName() == info[0]){
			    namespaceList.set(i, result);
			}
	            }
		    model.put("Namespace", namespaceList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public void updateDeleteModel(String namespace, Boolean run) {
	    try {
	    
            Map<String, ArrayList> model = modelService.getAllComponents(run);
            Map<String, Map<String, Map<String, ArrayList>>> relationships = modelService.getRelationships(run);
            List<String> keyWONamespace = Arrays.asList("Container", "Namespace", "PersistentVolume", "Node");
	
	    for (String key : model.keySet()) {
	        if (!keyWONamespace.contains(key)) {

		    model.get(key).removeIf(n -> {
			    try {
		            Method m = n.getClass().getMethod("getMetadata");
			    V1ObjectMeta metadata = (V1ObjectMeta) m.invoke(n);
			    
			    if (metadata.getNamespace().equals(namespace)){
				relationships.get(key).remove(metadata.getName());
			    } 
			    return metadata.getNamespace().equals(namespace);
			    } catch (Exception e) {
				return false;
			    }
		    }); 
		}

	    }
	    } catch (Exception e) {
		e.printStackTrace();
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
