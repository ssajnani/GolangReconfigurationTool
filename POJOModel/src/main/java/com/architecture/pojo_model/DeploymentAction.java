package com.architecture.pojo_model;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.*;
import com.google.gson.*;
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
                e.printStackTrace();
            }
        }	
	public String create(String[] info){
	    if (info.length < 3){
	    	return "Error: Please provide the namespace and the yaml file path in a string array in that order.";
	    } else {
		try {
	            V1Deployment body = (V1Deployment) Yaml.load(new File(info[1]));
		    V1Deployment result = body;
	            Boolean run = false;
	            if (info[2] != null && info[2].equals("All")){
		        run = true;
	            } else {
		    
	                result = api.createNamespacedDeployment(info[0], body, false, null, info[2]);
		    }
	            Map<String, ArrayList> model = modelService.getAllComponents(run);
		    Thread.sleep(100);
		    final V1Deployment finDep = result; 
	            if (!model.get("Deployment").stream().anyMatch(c -> {
	                V1Deployment d = (V1Deployment) c;
			return d.getMetadata().getName().equals(finDep.getMetadata().getName());
		    })) {
			return modelUpdateCreate(result, run, info[0]);
		    } else {
			return "Deployment already exists";
		    }
		} catch (Throwable e) {
		    System.out.println(e);
                    return e.toString() + Arrays.toString(e.getStackTrace());
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
	            if (info[2] != null && info[2].equals("All")){
		        run = true;
	            } else {
		        api.deleteNamespacedDeployment(info[0], info[1], body, null, null,0,null, null);
		    }
                    Map<String, ArrayList> model = modelService.getAllComponents(run);
		    modelUpdateDelete(info[0], "Deployment", run);
                    ArrayList<V1Deployment> deploymentList = model.get("Deployment");
		    int listSize = deploymentList.size();
		    Boolean found = false;
		    for (int i = 0; i < listSize; i++) {
                        V1Deployment deployment = deploymentList.get(i);
			if (deployment.getMetadata().getName().equals(info[0]) && deployment.getMetadata().getNamespace().equals(info[1])){
			    deploymentList.remove(i);
			    found = true;
			    break;
			}
                    }
		    model.put("Deployment", deploymentList);
		    modelService.setAllComponents(model, run);
		    if (found) {
		        return "Success";
		    } else {
			return "No deployment with that name exists";
		    }
		} catch (Throwable e) {
		    return Arrays.toString(e.getStackTrace());
		}
	    }
	} 
	public String update(String[] info) {
	    if (info.length < 4){
	    	return "Error: Please provide a valid deployment name, namespace, and a valid patch string in a string array in that order.";
	    } else {
		try {
		    
	            //V1Deployment body = (V1Deployment) Yaml.load(new File(info[2]));
		    ArrayList<JsonObject> arr = new ArrayList<>();
		    arr.add(((JsonElement) modelService.deserialize(info[2], JsonElement.class)).getAsJsonObject());
		    V1Deployment result = null;
	            Boolean run = false;
	            if (info[3] != null && info[3].equals("All")){
		        run = true;
	            } else {
	                result = api.patchNamespacedDeployment(info[0], info[1], arr, null, null);
		    }
		    Thread.sleep(100);
		    modelUpdateDelete(info[0], "Deployment", run);
		    modelUpdateCreate(result, run, info[1]);
		    return "Success";
		} catch (Throwable e) {
		    System.out.println(e);
		    return Arrays.toString(e.getStackTrace());
		}
	    }	
	}
	public void modelUpdateDelete(String name, String component, Boolean run){

	    try {
	        Map<String, Map<String, Map<String, ArrayList>>> relationships = modelService.getRelationships(run);
	        Map<String, ArrayList> model = modelService.getAllComponents(run);
	        ArrayList<String> owners = new ArrayList<String>();
	        owners.add(component + ":" + name);
	        while (owners.size() > 0) {
		    String owner = owners.remove(owners.size() - 1);
		    String[] parts = owner.split(":");
	            model.get(parts[0]).removeIf(n -> {
                            try {
                            Method m = n.getClass().getMethod("getMetadata");
                            V1ObjectMeta metadata = (V1ObjectMeta) m.invoke(n);
                            return metadata.getName().equals(parts[1]);
                            } catch (Exception e) {
                                return false;
                            }
                    });

		    if (relationships.get(parts[0]).get(parts[1]) != null){
		        Iterator<String> it =  relationships.get(parts[0]).get(parts[1]).keySet().iterator();
		        while (it.hasNext()) {
			    String k = it.next();
		            int size = relationships.get(parts[0]).get(parts[1]).get(k).size();
		            for (int counter = 0; counter < size; counter++) {
			        Method m = relationships.get(parts[0]).get(parts[1]).get(k).get(counter).getClass().getMethod("getMetadata");
			        V1ObjectMeta metadata = (V1ObjectMeta) m.invoke(relationships.get(parts[0]).get(parts[1]).get(k).get(counter));
			        owners.add(k + ":" + metadata.getName());	
			    }
	                };
	                relationships.get(parts[0]).remove(parts[1]);
		    }
	        }
	        modelService.setAllComponents(model, run);
	        modelService.setRelationships(relationships, run);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public String modelUpdateCreate(V1Deployment result, Boolean run, String namespace){
	    try {
	    String component = "Deployment";
	    String secondary = "ReplicaSet";
	    String tertiary = "Pod";
	    String quaternary = "Container";
	    String labels = result.getSpec().getSelector().getMatchLabels().toString().replaceAll("\\{|\\}", "");
	    Integer replicas = result.getSpec().getReplicas();
	    Map<String, ArrayList> model = modelService.getAllComponents(run);
            Map<String, Map<String, Map<String, ArrayList>>> relationships = modelService.getRelationships(run);
	    if (!model.get("Namespace").stream().anyMatch(c -> {
		V1Namespace d = (V1Namespace) c;    
	        return d.getMetadata().getName().equals(namespace);
	    })){
	    	return "No such namespace with name " + namespace + " found";
	    }
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
	        relationships.get(secondary).put(replica, new HashMap<String, ArrayList>());
	        relationships.get(secondary).get(replica).put(tertiary, podItems);
	    }

	    deploymentList.add(result);
	    modelService.setAllComponents(model, run);
	    modelService.setRelationships(relationships, run);
		return "Success";
	    } catch (Exception e) {
	    	e.printStackTrace();
		return e.getMessage();
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
		    return Arrays.toString(e.getStackTrace());
		}
	    }	
	}
}
