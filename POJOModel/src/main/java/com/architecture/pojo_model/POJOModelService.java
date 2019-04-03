package com.architecture.pojo_model;
import java.util.*;
import com.google.gson.*;
import java.lang.reflect.*;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import java.lang.Package;
import java.util.concurrent.CompletableFuture;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import java.lang.reflect.Method;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.*;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import com.architecture.pojo_model.JsonUtil;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

public class POJOModelService {
        private static List<String> accessibleListsCoreV1Api = Arrays.asList("Pod", "ConfigMap", "Service", "Endpoints", "Event", "LimitRange", "PersistentVolumeClaim", "PodTemplate", "ReplicationController", "ResourceQuota", "Secret", "ServiceAccount", "ControllerRevision", "DaemonSet", "Deployment", "ReplicaSet", "StatefulSet", "HorizontalPodAutoscaler", "Job", "Node", "PersistentVolume", "Namespace");
	private final static List<String> nonNamespaced = Arrays.asList("Node", "PersistentVolume", "Namespace");
	private final static List<String> batchItems = Arrays.asList("Job");
	private final static List<String> appsV1Items = Arrays.asList("ControllerRevision", "DaemonSet", "Deployment", "ReplicaSet", "StatefulSet");
	private final static List<String> autoscalingItems = Arrays.asList("HorizontalPodAutoscaler");
        private static Map<String, Map<String, Map<String, ArrayList>>> relationships = new HashMap<String, Map<String, Map<String, ArrayList>>>();
	//private componentTypeListSize = 12;	
	private static Map<String, ArrayList> components = new HashMap<>();
        private static Map<String, Map<String, Map<String, ArrayList>>> relationshipsDryRun = new HashMap<String, Map<String, Map<String, ArrayList>>>();
	//private componentTypeListSize = 12;	
	private static Map<String, ArrayList> componentsDryRun = new HashMap<>();
	private static Map<String, ArrayList> differenceComp = new HashMap<>();
	private static JsonUtil jsonUtil = new JsonUtil();
	private static Boolean triggerRefresh = false;
	static {
	    refresh(20000);
	}
	public static void setTriggerRefresh(Boolean val) {
	    triggerRefresh = val;
	}
	public static Boolean getTriggerRefresh() {
	    return triggerRefresh;
	}
	public static void refresh(int timeout) {
            while (triggerRefresh) {
                try {
                   Thread.sleep(timeout);
		   initialize();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException Exception" + e.getMessage());
                }
            }
	    initialize();
	}
	public static void initialize() {
	    try{
		
	        for (String resourceType : accessibleListsCoreV1Api) {
	            relationships.put(resourceType, new HashMap<String, Map<String, ArrayList>>());
                    components.put(resourceType, new ArrayList<>());    
	        }
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		int sizeTypes = accessibleListsCoreV1Api.size();
		for(int i = 0; i < sizeTypes; i++) {
		    String resourceType = accessibleListsCoreV1Api.get(i);
		    String apiType = getAPIType(resourceType);
		    Boolean test = !nonNamespaced.contains(resourceType);	   
		    futures.add(CompletableFuture.runAsync(() -> getResources(resourceType, test, apiType)));
		}
	        CompletableFuture[] futureResultArray = futures.toArray(new CompletableFuture[futures.size()]);
		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureResultArray);
                CompletableFuture<List<Void>> finalResults = combinedFuture
                .thenApply(voidd ->
                        futures.stream()
                                .map(future -> future.join())
                        	.collect(Collectors.toList()));
		finalResults.thenAccept(result -> {//System.out.println("Initialized successfully."));
		synchronizeDryRun();
		System.out.println("Model initialized successfully.");});
                // getIngress();
		// getNetworkPolicy();
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}	
	private static String getAPIType(String resourceType){
		    String apiType = "core";	
	    	    if (batchItems.contains(resourceType)) {
			apiType = "batch";
		    } else if (appsV1Items.contains(resourceType)){
		        apiType = "apps"; 
		    } else if (autoscalingItems.contains(resourceType)){
		        apiType = "autoscale"; 
		    }
		    return apiType;
	}
	public Map<String, ArrayList> getAllComponents(){
	    try{
	        return components;
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
		return components;
	    }
	}
	public Object deserialize(String jsonStr, Class<?> targetClass) {
	    Object obj = (new Gson()).fromJson(jsonStr, targetClass);
            return obj;
	}
	public void setAllComponents(Map<String, ArrayList> newMap){
	    components = newMap;
	}
	public Map<String, Map<String, Map<String, ArrayList>>> getRelationships(){
	    try{
		    
	        return relationships;
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
		Map<String, ArrayList> inner = new HashMap<String, ArrayList>();
		inner.put("failed", fail);
		Map<String, Map<String, ArrayList>> outer = new HashMap<String, Map<String, ArrayList>>();
                outer.put("failed", inner);
	        fail.add(e.getMessage());	
		relationships.put("Failed", outer);
		return relationships;
	    }
	}
	public void setRelationships(Map<String, Map<String, Map<String, ArrayList>>> relations){
	    relationships = relations;
	}
	public Map<String, ArrayList> getDiffOfResources() {
	    try{

	    differenceComp = new HashMap<>();
	    for (String key : components.keySet()) {
	        ArrayList tempComp = (ArrayList) components.get(key).clone();
	        ArrayList tempCompDry = (ArrayList) componentsDryRun.get(key).clone();
		ArrayList<String> resultComp = getNames(tempComp, key);
		ArrayList<String> resultDryComp = getNames(tempCompDry, key);
		ArrayList<String> resultCompCopy = (ArrayList) resultComp.clone();
		ArrayList<String> resultCompDryCopy = (ArrayList) resultDryComp.clone();
		resultCompCopy.removeAll(resultDryComp);
		resultCompDryCopy.removeAll(resultComp);
		resultComp.retainAll(resultDryComp);
		differenceComp.put("ComponentsNotDry"+key, resultCompCopy);
		differenceComp.put("DryNotComponents"+key, resultCompDryCopy);
		differenceComp.put("Common"+key, resultComp);
	    } 
	        return differenceComp;
	    } catch(Exception e) {
	        differenceComp.put("Failed: ", (ArrayList) Arrays.asList(e.getStackTrace()));
	        return differenceComp;
	    }
	}
	public ArrayList<String> getNames(ArrayList var, String key) {
		ArrayList<String> resultComp = (ArrayList) var.stream().map(thing -> {
	            try{
			if (key == "Container") {
		            Method m = thing.getClass().getMethod("getName");
	                    String name = (String) m.invoke(thing);
			    return name;
			} else {
		            Method m = thing.getClass().getMethod("getMetadata");
	                    V1ObjectMeta meta = (V1ObjectMeta) m.invoke(thing);
		            return meta.getName();
			}
		    } catch(Exception e) {
		        return "Failed";
		    }
		}).collect(Collectors.toList());
		return resultComp;
	}
	public Map<String, ArrayList> getAllComponents(Boolean dryRun){
	    try{
		if (dryRun){
		    return componentsDryRun;
		} else {
	            return components;
		}
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
		return components;
	    }
	}
	public void setAllComponents(Map<String, ArrayList> newMap, Boolean dryRun){
	    if (dryRun) {
	        componentsDryRun = newMap;
	    } else {
	        components = newMap;
	    }
	}
	public Map<String, Map<String, Map<String, ArrayList>>> getRelationships(Boolean dryRun){
	    try{
		    
		if (dryRun) {
		    return relationshipsDryRun;
	    	} else {	    
	            return relationships;
		}
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
		Map<String, ArrayList> inner = new HashMap<String, ArrayList>();
		inner.put("failed", fail);
		Map<String, Map<String, ArrayList>> outer = new HashMap<String, Map<String, ArrayList>>();
                outer.put("failed", inner);
	        fail.add(e.getMessage());	
		relationships.put("Failed", outer);
		return relationships;
	    }
	}
	public void setRelationships(Map<String, Map<String, Map<String, ArrayList>>> relations, Boolean dryRun){
	    if (dryRun){
	        relationshipsDryRun = relations;
	    } else {
		relationships = relations;
	    }
	}
	public static String synchronizeDryRun() {
	    for (String key : components.keySet()) {
	         componentsDryRun.put(key, (ArrayList) components.get(key).clone());
	    }
	    for (String key : relationships.keySet()) {
		if (!relationshipsDryRun.containsKey(key)){
		    relationshipsDryRun.put(key, new HashMap<String, Map<String, ArrayList>>());
		}
		for (String nextKey : relationships.get(key).keySet()) {
		    if (!relationshipsDryRun.get(key).containsKey(nextKey)){
		        relationshipsDryRun.get(key).put(nextKey, new HashMap<String, ArrayList>());
		    }
		    for (String finalKey : relationships.get(key).get(nextKey).keySet()){
		        if (!relationshipsDryRun.get(key).get(nextKey).containsKey(finalKey)){
		            relationshipsDryRun.get(key).get(nextKey).put(finalKey, (ArrayList) relationships.get(key).get(nextKey).get(finalKey).clone());
		        }
		    
		    }
		}
	    }
	    return "Success";
	}
	public static void getResources(String set, Boolean namespaced, String apiType){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
		Object api = getAPI(apiType);
		//AppsV1Api api = new AppsV1Api();
		String apiFunction = getAPIFunction(set, namespaced);
		Method method = null;
		if (nonNamespaced.contains(set)){
		    method = api.getClass().getMethod(apiFunction, Boolean.class, String.class, String.class, String.class, String.class, Integer.class, String.class, Integer.class, Boolean.class);
		} else {
		    method = api.getClass().getMethod(apiFunction, String.class, String.class, Boolean.class, String.class, Integer.class, String.class, String.class, Integer.class, Boolean.class);
		}
		Object test = method.invoke(api, null, null, null, null, null, null, null, null, null);
		
		Method m = test.getClass().getMethod("getItems");
		Object here = m.invoke(test);
		ArrayList<Object> neww = (ArrayList) here;
		
		for (Object obj : neww) {
		    components.get(set).add(castObject(obj.getClass(), obj));
		    
		    addReferences(obj, set);
		    if (set == "Pod") {
			addContainersForPods(obj);
		    }
		}	
	    } catch (Exception e) {
		System.out.println(set + " Failed");
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
	private static void addReferences(Object obj, String type) {
	    try {	    
		    Method metadata = obj.getClass().getMethod("getMetadata");
		    V1ObjectMeta meta = (V1ObjectMeta) metadata.invoke(obj);

		    List<V1OwnerReference> references = meta.getOwnerReferences();
		    if (references != null) {
			for (V1OwnerReference reference : references) {
			    String kind = reference.getKind();
			    String name = reference.getName();
			    if (!relationships.get(kind).containsKey(name))
{
                                relationships.get(kind).put(name, new HashMap<String, ArrayList>());
                            }	
                            if (!relationships.get(kind).get(name).containsKey(type)) {
                                relationships.get(kind).get(name).put(type, new ArrayList<V1Pod>());
                            }
                            relationships.get(kind).get(name).get(type).add(castObject(obj.getClass(), obj)); 
                        }
		    }
	    } catch(Exception e) {
		System.out.println(e.getMessage());
	    }
	}
	private static void addContainersForPods(Object obj) {
	    try{
		    String type = "Container"; 

		    Method getSpec = obj.getClass().getMethod("getSpec");
		    V1PodSpec spec = (V1PodSpec) getSpec.invoke(obj);
		    List<V1Container> containers = spec.getContainers();
                    for (V1Container container : containers){
			if (!components.containsKey(type)) {
			    components.put(type, new ArrayList<V1Container>());
			}
                        if (!components.get(type).contains(container)){
                            components.get(type).add(container);
                        }
                    }
	    } catch(Exception e) {
		   
		    System.out.println(e);
	    }
	}
	private static Object getAPI(String type) {
		Object api = null;
		if (type == "core") {
		    api = new CoreV1Api();
		} else if (type == "apps") {
		    api = new AppsV1Api();
		} else if (type == "batch") {
		    api = new BatchV1Api();
		} else if (type == "autoscale"){
		    api = new AutoscalingV1Api();
		}
		return api;
	}
	private static String getAPIFunction(String type, Boolean namespaced) {
	    String func = "";
	    if (namespaced) {
		func = "list" + type + "ForAllNamespaces";
	    } else {
		func = "list" + type;
	    }
	    return func;
	}
        private static <T> T castObject(Class<T> clazz, Object object) {
            return (T) object;
        }
} 
