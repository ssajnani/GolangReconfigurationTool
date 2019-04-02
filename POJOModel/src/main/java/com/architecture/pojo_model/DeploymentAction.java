package com.architecture.pojo_model;
import java.util.*;
import java.lang.*;
import io.kubernetes.client.util.Yaml;
import java.io.*;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.*;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import java.lang.reflect.Method;
public class DeploymentAction implements AtomicAction {
        private ApiClient client;
        private AppsV1Api api;
        private CoreV1Api core;
        private POJOModelService modelService;
        public DeploymentAction(){
            try {
               client = Config.defaultClient();
               api = new AppsV1Api();
	       core = new CoreV1Api();
               modelService = new POJOModelService();
               Configuration.setDefaultApiClient(client);
            } catch(Throwable e){
                System.out.println(e.getMessage());
            }
        }	
	public String create(String[] info){
	    if (info.length < 3){
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Deployment body = (V1Deployment) Yaml.load(new File(info[1]));
	            V1Deployment result = api.createNamespacedDeployment(info[0], body, false, null, null);
		    Thread.sleep(100);
	            Boolean run = false;
	            if (info[2].contains("All")){
		        run = true;
	            }

		    modelUpdateCreate(result, run);
		    return "Success";
		} catch (Throwable e) {
		    System.out.println(e);
		    return e.getMessage();
		}
	    }	
	}
	public String delete(String[] info) {
	    if (info.length < 3){
	    	return "Error: Please provide a valid deployment name and the namespace in a string array in that order.";
	    } else {
		try {
		    V1DeleteOptions body = new V1DeleteOptions();
	            Boolean run = false;
	            if (info[2].contains("All")){
		        run = true;
	            }
	            if (!run){
		        api.deleteNamespacedDeployment(info[0], info[1], body, null, null,0,null, null);
		    }
                    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    modelUpdateDelete(info[0], "Deployment", run);
                    ArrayList<V1Deployment> deploymentList = model.get("Deployment");
		    int listSize = deploymentList.size();
		    for (int i = 0; i < listSize; i++) {
                        V1Deployment deployment = deploymentList.get(i);
			if (deployment.getMetadata().getName() == info[0] && deployment.getMetadata().getNamespace() == info[1]){
			    deploymentList.remove(i);
			}
                    }
		    model.put("Deployment", deploymentList);
		    modelService.setAllComponents(model, run);
		    return "Success";
		} catch (Throwable e) {
		    return e.getMessage();
		}
	    }
	} 
	public String update(String[] info) {
	    if (info.length < 4){
	    	return "Error: Please provide a valid deployment name, namespace, and yaml file path in a string array in that order.";
	    } else {
		try {
		    
	            V1Deployment body = (V1Deployment) Yaml.load(new File(info[2]));
	            V1Deployment result = api.patchNamespacedDeployment(info[0], info[1], body, null, null);
		    Thread.sleep(100);
	            Boolean run = false;
	            if (info[3].contains("All")){
		        run = true;
	            }
		    modelUpdateDelete(info[0], "Deployment", run);
		    modelUpdateCreate(result, run);
		    return "Success";
		} catch (Throwable e) {
		    return e.getMessage();
		}
	    }	
	}
	public void modelUpdateDelete(String name, String component, Boolean run){

	    try {
	        Map<String, Map<String, Map<String, ArrayList>>> relationships = modelService.getRelationships(run);
	        Map<String, ArrayList> model = modelService.getAllComponents(run);
		Iterator<String> it = relationships.get(component).get(name).keySet().iterator();
		while (it.hasNext()) {
		    String k = it.next();
	            model.get(k).removeAll(relationships.get(component).get(name).get(k));
		}
	        ArrayList<String> owners = new ArrayList<String>();
	        owners.add(component + ":" + name);
	        while (owners.size() > 0) {
		    String owner = owners.remove(owners.size() - 1);
		    System.out.println(owner);
		    String[] parts = owner.split(":");
		    if (relationships.get(parts[0]).get(parts[1]) != null){
		        it =  relationships.get(parts[0]).get(parts[1]).keySet().iterator();
		        while (it.hasNext()) {
			    String k = it.next();
		            int size = relationships.get(parts[0]).get(parts[1]).get(k).size();
		            for (int counter = 0; counter < size; counter++) {
			        Method m = relationships.get(parts[0]).get(parts[1]).get(k).get(counter).getClass().getMethod("getMetadata");
			        V1ObjectMeta metadata = (V1ObjectMeta) m.invoke(relationships.get(parts[0]).get(parts[1]).get(k).get(counter));
			        owners.add(k + ":" + metadata.getName());	
		            }
	                    relationships.get(parts[0]).get(parts[1]).remove(k);
	                };
		    }
	        }
	        modelService.setAllComponents(model, run);
	        modelService.setRelationships(relationships, run);
	    } catch (Exception e) {
	        System.out.println(e);
	    }
	}
	public void modelUpdateCreate(V1Deployment result, Boolean run){
	    try {
	    String component = "Deployment";
	    String secondary = "ReplicaSet";
	    String tertiary = "Pod";
	    String quaternary = "Container";
	    String labels = result.getSpec().getSelector().getMatchLabels().toString().replaceAll("\\{|\\}", "");
	    Integer replicas = result.getSpec().getReplicas();
	    Map<String, ArrayList> model = modelService.getAllComponents(run);
            Map<String, Map<String, Map<String, ArrayList>>> relationships = modelService.getRelationships(run);
	    ArrayList<V1Deployment> deploymentList = model.get(component);
            ArrayList<V1ReplicaSet> rsList = model.get(secondary);
            ArrayList<V1Pod> podList = model.get(tertiary);
	    relationships.get(component).put(result.getMetadata().getName(), new HashMap<String, ArrayList>());
	    if (!run){
                ArrayList<V1ReplicaSet> newrsList = (ArrayList) api.listNamespacedReplicaSet(result.getMetadata().getNamespace(),true,null,null,null,labels,null,null, null, null).getItems();
	        rsList.addAll(newrsList);
	        relationships.get(component).get(result.getMetadata().getName()).put(secondary, newrsList);
	        String replica = newrsList.get(0).getMetadata().getName();
	        relationships.get(secondary).put(replica, new HashMap<String, ArrayList>());
                ArrayList<V1Pod> newPodList = (ArrayList) core.listNamespacedPod(result.getMetadata().getNamespace(),true,null,null,null,labels,null,null, null, null).getItems();
	        podList.addAll(newPodList);
                for (V1Pod item : newPodList){
                    V1PodSpec spec = item.getSpec();
                    List<V1Container> containers = spec.getContainers();
                    for (V1Container container : containers){
		        if (!model.get(quaternary).contains(container)){
                            model.get(quaternary).add(container);
                        }
                    }
                }
	        relationships.get(secondary).get(replica).put(tertiary, newPodList);
	        model.put(component, deploymentList);
	        model.put(secondary, rsList);
	        model.put(tertiary, podList);
	    } else {
		V1ReplicaSet rs = new V1ReplicaSet();
		rs.setMetadata(new V1ObjectMeta());
		rs.setSpec(new V1ReplicaSetSpec());
		rs.getMetadata().setLabels(result.getSpec().getSelector().getMatchLabels());
		rs.getMetadata().setName(result.getMetadata().getName() + "-replicaset");
		V1OwnerReference depRef = new V1OwnerReference();
		depRef.setName(result.getMetadata().getName());
		rs.getMetadata().addOwnerReferencesItem(depRef);
	        String replica = rs.getMetadata().getName();
		ArrayList<V1ReplicaSet> rsItems = new ArrayList<V1ReplicaSet>();
		ArrayList<V1Pod> podItems = new ArrayList<V1Pod>();
		rsItems.add(rs);
	        relationships.get(component).get(result.getMetadata().getName()).put(secondary, rsItems);
		int tmp = 0;
		model.get(secondary).add(rs);

		while(tmp < replicas) {
		    tmp++;	
		    V1Pod pod = new V1Pod();
		    pod.setMetadata(new V1ObjectMeta());
		    pod.setSpec(new V1PodSpec());
		    pod.getMetadata().setLabels(result.getSpec().getSelector().getMatchLabels());
		    pod.getMetadata().setName(result.getMetadata().getName() + "-pod-" + Integer.toString(tmp));
		    V1OwnerReference rsRef = new V1OwnerReference();
		    rsRef.setName(replica);
		    pod.getMetadata().addOwnerReferencesItem(depRef);
		    model.get(tertiary).add(pod);
		    podItems.add(pod);
		}
		System.out.println(rsItems);
		System.out.println(podItems);
	        relationships.get(secondary).put(replica, new HashMap<String, ArrayList>());
	        relationships.get(secondary).get(replica).put(tertiary, podItems);
	    }

	    deploymentList.add(result);
	    modelService.setAllComponents(model, run);
	    modelService.setRelationships(relationships, run);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	public Object read(String[] info) {
	    if (info.length < 3){
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
