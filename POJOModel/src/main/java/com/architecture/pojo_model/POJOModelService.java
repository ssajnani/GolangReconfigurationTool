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
        private String[] accessibleListsCoreV1Api = {"Pod", "ConfigMap", "Service", "Endpoints", "Event", "LimitRange", "PersistentVolumeClaim", "PodTemplate", "ReplicationController", "ResourceQuota", "Secret", "ServiceAccount"};
        //private componentTypeListSize = 12;	
	private static Map<String, ArrayList> components = new HashMap<>();
	private static JsonUtil jsonUtil = new JsonUtil();
	static {
	    try{
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
		finalResults.thenAccept(result -> System.out.println("Initialized successfully."));
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
	public static void getPods(){
            try{
	    	ApiClient client = Config.defaultClient();
                Configuration.setDefaultApiClient(client);
                CoreV1Api api = new CoreV1Api();
		String component = "Pod";
		String secondary = "Container";
	        components.put(component, new ArrayList<V1Pod>());
		components.put(secondary, new ArrayList<V1Container>());
		V1PodList list = api.listPodForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Pod item : list.getItems()){
		    V1PodSpec spec = item.getSpec();
		    List<V1Container> containers = spec.getContainers();
		    for (V1Container container : containers){
			components.get(secondary).add(container);
		    }
		    components.get(component).add(item);
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
	        components.put(component, new ArrayList<V1ConfigMap>());
		V1ConfigMapList list = api.listConfigMapForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ConfigMap item : list.getItems()){
		    components.get(component).add(item);
		}
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
	        components.put(component, new ArrayList<V1Service>());
		V1ServiceList list = api.listServiceForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Service item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Endpoints>());
		V1EndpointsList list = api.listEndpointsForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Endpoints item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Event>());
		V1EventList list = api.listEventForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Event item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1LimitRange>());
		V1LimitRangeList list = api.listLimitRangeForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1LimitRange item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1PersistentVolumeClaim>());
		V1PersistentVolumeClaimList list = api.listPersistentVolumeClaimForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1PersistentVolumeClaim item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1PodTemplate>());
		V1PodTemplateList list = api.listPodTemplateForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1PodTemplate item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1ReplicationController>());
		V1ReplicationControllerList list = api.listReplicationControllerForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ReplicationController item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1ResourceQuota>());
		V1ResourceQuotaList list = api.listResourceQuotaForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ResourceQuota item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Secret>());
		V1SecretList list = api.listSecretForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Secret item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1ServiceAccount>());
		V1ServiceAccountList list = api.listServiceAccountForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ServiceAccount item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1ControllerRevision>());
		V1ControllerRevisionList list = api.listControllerRevisionForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ControllerRevision item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1DaemonSet>());
		V1DaemonSetList list = api.listDaemonSetForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1DaemonSet item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Deployment>());
		V1DeploymentList list = api.listDeploymentForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Deployment item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1ReplicaSet>());
		V1ReplicaSetList list = api.listReplicaSetForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1ReplicaSet item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1StatefulSet>());
		V1StatefulSetList list = api.listStatefulSetForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1StatefulSet item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1HorizontalPodAutoscaler>());
		V1HorizontalPodAutoscalerList list = api.listHorizontalPodAutoscalerForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1HorizontalPodAutoscaler item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Job>());
		V1JobList list = api.listJobForAllNamespaces(null,null,null,null,null,null,null,null, null);
		for (V1Job item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Node>());
		V1NodeList list = api.listNode(null,null,null,null,null,null,null,null, null);
		for (V1Node item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1PersistentVolume>());
		V1PersistentVolumeList list = api.listPersistentVolume(null,null,null,null,null,null,null,null, null);
		for (V1PersistentVolume item : list.getItems()){
		    components.get(component).add(item);;
		}
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
	        components.put(component, new ArrayList<V1Namespace>());
		V1NamespaceList list = api.listNamespace(null,null,null,null,null,null,null,null, null);
		for (V1Namespace item : list.getItems()){
		    components.get(component).add(item);;
		}
	    } catch (Exception e) {
		ArrayList<String> fail = new ArrayList<String>();
	        fail.add(e.getMessage());	
		components.put("Failed", fail);
	    }
	}
			    
			    
			    
			    
}			    
			    

