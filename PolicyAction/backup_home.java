package com.architecture.pojo_model;
import java.util.*;
import java.lang.reflect.*;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import java.lang.Package;
import java.util.concurrent.CompletableFuture;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.*;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import com.architecture.pojo_model.JsonUtil;

public class POJOModelService {
        private static String[] accessibleListsCoreV1Api = {"Pod", "ConfigMap", "Service", "Endpoints", "Event", "LimitRange", "PersistentVolumeClaim", "PodTemplate", "ReplicationController", "ResourceQuota", "Secret", "ServiceAccount", "ControllerRevision", "DaemonSet", "Deployment", "ReplicaSet", "StatefulSet", "HorizontalPodAutoscaler", "Job", "Node", "PersistentVolume", "Namespace"};
        private static Map<String, Map<String, Map<String, ArrayList>>> relationships = new HashMap<String, Map<String, Map<String, ArrayList>>>();
	//private componentTypeListSize = 12;	
	private static Map<String, ArrayList> components = new HashMap<>();
	private static JsonUtil jsonUtil = new JsonUtil();
	static {
	    try{
		
	        for (String resourceType : accessibleListsCoreV1Api) {
	            relationships.put(resourceType, new HashMap<String, Map<String, ArrayList>>());
	        }
		List<CompletableFuture<Void>> futures = new ArrayList<>();
	        futures.add(CompletableFuture.runAsync(() -> getPods()));
	        futures.add(CompletableFuture.runAsync(() -> getConfigMaps()));
	        futures.add(CompletableFuture.runAsync(() -> getServices()));
	        futures.add(CompletableFuture.runAsync(() -> getEndpoints()));
	        futures.add(CompletableFuture.runAsync(() -> getEvents()));
	        futures.add(CompletableFuture.runAsync(() -> getLimitRanges()));
	        futures.add(CompletableFuture.runAsync(() -> getPersistentVolumeClaims()));
	        futures.add(CompletableFuture.runAsync(() -> getPodTemplates()));
	        futures.add(CompletableFuture.runAsync(() -> getReplicationControllers()));
	        futures.add(CompletableFuture.runAsync(() -> getSecrets()));
	        futures.add(CompletableFuture.runAsync(() -> getServiceAccounts()));
	        futures.add(CompletableFuture.runAsync(() -> getControllerRevisions()));
	        futures.add(CompletableFuture.runAsync(() -> getDaemonSets()));
	        futures.add(CompletableFuture.runAsync(() -> getDeployments()));
	        futures.add(CompletableFuture.runAsync(() -> getReplicaSets()));
	        futures.add(CompletableFuture.runAsync(() -> getStatefulSets()));
	        futures.add(CompletableFuture.runAsync(() -> getHorizontalPodAutoscaler()));
	        futures.add(CompletableFuture.runAsync(() -> getJobs()));
	        futures.add(CompletableFuture.runAsync(() -> getNodes()));
	        futures.add(CompletableFuture.runAsync(() -> getPersistentVolumes()));
	        futures.add(CompletableFuture.runAsync(() -> getNamespaces()));
	        CompletableFuture[] futureResultArray = futures.toArray(new CompletableFuture[futures.size()]);
		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureResultArray);
                CompletableFuture<List<Void>> finalResults = combinedFuture
                .thenApply(voidd ->
                        futures.stream()
                                .map(future -> future.join())
                        	.collect(Collectors.toList()));
		finalResults.thenAccept(result -> //System.out.println("Initialized successfully."));
		System.out.println("Model initialized successfully."));
                // getIngress();
		// getNetworkPolicy();
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
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
	public static void getPods(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "Pod";
		String secondary = "Container";
		components.put(secondary, new ArrayList<V1Container>());
		V1PodList list = api.listPodForAllNamespaces(null,null,null,null,null,null,null,null, null);
	        components.put(component, (ArrayList) list.getItems());
		for (V1Pod item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Pod>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		    V1PodSpec spec = item.getSpec();
		    List<V1Container> containers = spec.getContainers();
		    for (V1Container container : containers){
		        if (!components.get(secondary).contains(container)){
			    components.get(secondary).add(container);
			}
		    }
		}
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getConfigMaps(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "ConfigMap";
		V1ConfigMapList list = api.listConfigMapForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ConfigMap item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1ConfigMap>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	
	public static void getServices(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "Service";
		V1ServiceList list = api.listServiceForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Service item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Service>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public static void getEndpoints(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "Endpoints";
		V1EndpointsList list = api.listEndpointsForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Endpoints item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Endpoints>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getEvents(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "Event";
		V1EventList list = api.listEventForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Event item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Event>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public static void getLimitRanges(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "LimitRange";
		V1LimitRangeList list = api.listLimitRangeForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1LimitRange item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1LimitRange>());

			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public static void getPersistentVolumeClaims(){
            try {
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "PersistentVolumeClaim";
		V1PersistentVolumeClaimList list = api.listPersistentVolumeClaimForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1PersistentVolumeClaim item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1PersistentVolumeClaim>());

			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public static void getPodTemplates(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "PodTemplate";
		V1PodTemplateList list = api.listPodTemplateForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1PodTemplate item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1PodTemplate>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getReplicationControllers(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "ReplicationController";
		V1ReplicationControllerList list = api.listReplicationControllerForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ReplicationController item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1ReplicationController>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
	public static void getResourceQuotas(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "ResourceQuota";
		V1ResourceQuotaList list = api.listResourceQuotaForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ResourceQuota item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1ResourceQuota>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getSecrets(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "Secret";
		V1SecretList list = api.listSecretForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Secret item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Secret>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
			    
	public static void getServiceAccounts(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "ServiceAccount";
		V1ServiceAccountList list = api.listServiceAccountForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ServiceAccount item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1ServiceAccount>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getControllerRevisions(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();
		String component = "ControllerRevision";
		V1ControllerRevisionList list = api.listControllerRevisionForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ControllerRevision item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1ControllerRevision>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getDaemonSets(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();
		String component = "DaemonSet";
		V1DaemonSetList list = api.listDaemonSetForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1DaemonSet item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1DaemonSet>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getDeployments(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();
		String component = "Deployment";
		V1DeploymentList list = api.listDeploymentForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Deployment item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Deployment>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
	
	public static void getReplicaSets(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();
		String component = "ReplicaSet";
		V1ReplicaSetList list = api.listReplicaSetForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ReplicaSet item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1ReplicaSet>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getStatefulSets(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AppsV1Api api = new AppsV1Api();
		String component = "StatefulSet";
		V1StatefulSetList list = api.listStatefulSetForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1StatefulSet item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1StatefulSet>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getHorizontalPodAutoscaler(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                AutoscalingV1Api api = new AutoscalingV1Api();
		String component = "HorizontalPodAutoscaler";
		V1HorizontalPodAutoscalerList list = api.listHorizontalPodAutoscalerForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1HorizontalPodAutoscaler item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1HorizontalPodAutoscaler>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public static void getJobs(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                BatchV1Api api = new BatchV1Api();
		String component = "Job";
		V1JobList list = api.listJobForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Job item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Job>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    /*
	public void getIngress(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
		ExtensionsV1beta1Api api = new ExtensionsV1beta1Api();
		String component = "Ingress";
	        components.put(component, new ArrayList());
		V1IngressList list = api.listIngressForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Ingress item : list.getItems()){
		    components.get(component).add(item);;
		}
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public void getNetworkPolicy(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
		ExtensionsV1beta1Api api = new ExtensionsV1beta1Api();
		String component = "NetworkPolicy";
	        components.put(component, new ArrayList());
		V1NetworkPolicyList list = api.listNetworkPolicyForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1NetworkPolicy item : list.getItems()){
		    components.get(component).add(item);;
		}
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    */
	public static void getNodes(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();
		String component = "Node";
		V1NodeList list = api.listNode(null,null,null,null,null,null,null,null, null);
		for (V1Node item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Node>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
	public static void getPersistentVolumes(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();
		String component = "PersistentVolume";
		V1PersistentVolumeList list = api.listPersistentVolume(null,null,null,null,null,null,null,null, null);
		for (V1PersistentVolume item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1PersistentVolume>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
	public static void getNamespaces(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();
		String component = "Namespace";
		V1NamespaceList list = api.listNamespace(null,null,null,null,null,null,null,null, null);
		for (V1Namespace item : list.getItems()){
		    List<V1OwnerReference> references = item.getMetadata().getOwnerReferences();
		    if (references != null){
		    for (V1OwnerReference reference : references) {
			String kind = reference.getKind();
			String name = reference.getName();
			if (!relationships.get(kind).containsKey(name)) {
			    relationships.get(kind).put(name, new HashMap<String, ArrayList>());	
			} 
			if (!relationships.get(kind).get(name).containsKey(component)) {
			    relationships.get(kind).get(name).put(component, new ArrayList<V1Namespace>());
			}
                        relationships.get(kind).get(name).get(component).add(item);
		    }
			}
		}
	        components.put(component, (ArrayList) list.getItems());
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
			    
			    
}			    
			    

