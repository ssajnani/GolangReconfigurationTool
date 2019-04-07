package com.architecture.pojo_model;
import java.util.stream.Collectors;
import com.google.gson.*;
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
	    if (info.length < 3){
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
		    V1Pod body = (V1Pod) Yaml.load(new File(info[1]));
		    V1Pod result = body;
		    Boolean run = false;
                    if (info[2] != null && info[2].equals("All")){
                        run = true;
                    } else {
                        result = api.createNamespacedPod(info[0], body, null, null, null);
		    }
		    Map<String, ArrayList> model = modelService.getAllComponents(run);
                    ArrayList<V1Pod> podList = model.get("Pod");
		    podList.add(result);
		    model.put("Pod", podList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid pod name and the namespace in a string array in that order.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
                    Boolean run = false;
		    if (info[2] != null && info[2].equals("All")){
                        run = true;
                    } else {
			try {
	                    api.deleteNamespacedPod(info[0], info[1], body, null, null, null,null, null);
			} catch(Exception e) {
                            
			}
		    }
                    Map<String, ArrayList> model = modelService.getAllComponents(run);
                    ArrayList<V1Pod> podList = model.get("Pod");
		    int listSize = podList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Pod pod = podList.get(i);
			if (pod.getMetadata().getName().equals(info[0]) && pod.getMetadata().getNamespace().equals(info[1])){
			    List<V1OwnerReference> references = pod.getMetadata().getOwnerReferences();
			    if (references.size() > 0) {
				    return "Cannot delete pod because it is owned by: " + references.stream().map( n -> n.toString()).collect( Collectors.joining( "," ));
			    }
			    podList.remove(i);
			    break;
			}
                    }
		    model.put("Pod", podList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	} 
	public String update(String[] info) {
	    if (info.length < 4){
	    	return "Error: Please provide a valid pod name, namespace, and patch string in a string array in that order.";
	    } else {
		try {
                    ArrayList<JsonObject> arr = new ArrayList<>();
                    arr.add(((JsonElement) modelService.deserialize(info
[2], JsonElement.class)).getAsJsonObject());
	            V1Pod result = api.patchNamespacedPod(info[0], info[1], arr, null, info[3]);
                    Boolean run = false;
                    if (info[3] != null && info[3].equals("All")){
                        run = true;
                    }
                    Map<String, ArrayList> model = modelService.getAllComponents(run);
                    ArrayList<V1Pod> podList = model.get("Pod");
		    System.out.println(result.getMetadata().getName());
		    int listSize = podList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Pod pod = podList.get(i);
			if (pod.getMetadata().getName() == info[0] && pod.getMetadata().getNamespace() == info[1]){
			    podList.set(i, result);
			}
                    }
		    model.put("Pod", podList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Exception e) {
		    return e.getMessage();
		}
	    }	
	}
	public Object read(String[] info) {
	    if (info.length < 3){
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
