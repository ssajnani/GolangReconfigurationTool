package com.architecture.pojo_model;
import static spark.Spark.*;
import static com.architecture.pojo_model.JsonUtil.*;
import java.util.*;
import java.lang.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class POJOModelController {
        private JSONParser parser = new JSONParser(); 

	public POJOModelController(final POJOModelService pojoService) {

		get("/all", (req, res) -> pojoService.getAllComponents(), json());
		get("/diff", (req, res) -> pojoService.getDiffOfResources(), json());
		get("/synchronize", (req, res) -> pojoService.synchronizeDryRun(), json());
		post("/setRefresh", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    Integer timeout = ((Long) json.get("timeout")).intValue();
			    if (pojoService.getTriggerRefresh()){
				return "Refresh is already activated use the route /cancelRefresh to deactivate it";
			    } else {
			        pojoService.setTriggerRefresh(true);
				new Thread(new Runnable() {
				    public void run() {
			                pojoService.refresh(timeout);
                                    }	
				}).start();
			        return "Success";
			    }
			} catch (Exception e) {
			    return e;
			}

		}, json());
		post("/cancelRefresh", (req, res) -> {
                        try{
			    pojoService.setTriggerRefresh(false);
			    return "Success";
			} catch (Exception e) {
			    return e;
			}

		}, json());

//		get("/component/:id", (req, res) -> {
//			String id = req.params(":id");
//			if (user != null) {
//				return pojoService.getComponent(id);
//			}
//			res.status(400);
//			return new ResponseError("No pojo with id '%s' found", id);
//		}, json());

		post("/create_namespaced", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    String namespace = (String) json.get("namespace");
			    String path = (String) json.get("filepath");
		 	    String type = (String) json.get("type");
		 	    String dryRun = null;
		 	    if ((String) json.get("dryRun") != null) {
			        dryRun = (String) json.get("dryRun");
			    }
			    String[] array = {namespace, path, dryRun};
			    POJOModelAction action = new POJOModelAction(type);
			    String result = action.create(array);
			    return result;
			} catch (Exception e) {
			    return e;
			}
		}, json());
		
		post("/update_namespaced", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    String namespace = (String) json.get("namespace");
			    String name = (String) json.get("name");
			    String patchString = (String) json.get("patchString");
		 	    String type = (String) json.get("type");
		 	    String dryRun = null;
		 	    if ((String) json.get("dryRun") != null) {
			        dryRun = (String) json.get("dryRun");
			    }
			    String[] array = {name, namespace, patchString, dryRun};
			    POJOModelAction action = new POJOModelAction(type);
			    String result = action.update(array);
			    return result;
			} catch (Exception e) {
			    return e;
			}
		}, json());
		
		post("/delete_namespaced", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    String namespace = (String) json.get("namespace");
			    String name = (String) json.get("name");
		 	    String type = (String) json.get("type");
		 	    String dryRun = null;
		 	    if ((String) json.get("dryRun") != null) {
			        dryRun = (String) json.get("dryRun");
			    }
			    String[] array = {name, namespace, dryRun};
			    POJOModelAction action = new POJOModelAction(type);
			    String result = action.delete(array);
			    return result;
			} catch (Exception e) {
			    return e;
			}
		}, json());

		get("/read_namespaced", (req, res) -> {
			try{
                            JSONObject json = (JSONObject) parser.parse(req.body());
                            String namespace = (String) json.get("namespace");
                            String name = (String) json.get("name");
                            String type = (String) json.get("type");
                            String dryRun = null;
                            if ((String) json.get("dryRun") != null) {
                                dryRun = (String) json.get("dryRun");
                            }
                            String[] array = {name, namespace, dryRun};
                            POJOModelAction action = new POJOModelAction
(type);
                            return action.read(array);
                        } catch (Exception e) {
                            return e;
                        }
		});
		post("/create", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    String path = (String) json.get("filepath");
		 	    String type = (String) json.get("type");
		 	    String dryRun = null;
		 	    if ((String) json.get("dryRun") != null) {
			        dryRun = (String) json.get("dryRun");
			    }
			    String[] array = {path, dryRun};
			    POJOModelAction action = new POJOModelAction(type);
			    String result = action.create(array);
			    return result;
			} catch (Exception e) {
			    return e;
			}
		}, json());
		
		post("/update", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    String name = (String) json.get("name");
			    String patchString = (String) json.get("patchString");
		 	    String type = (String) json.get("type");
		 	    String dryRun = null;
		 	    if ((String) json.get("dryRun") != null) {
			        dryRun = (String) json.get("dryRun");
			    }
			    String[] array = {name, patchString, dryRun};
			    POJOModelAction action = new POJOModelAction(type);
			    String result = action.update(array);
			    return result;
			} catch (Exception e) {
			    return e;
			}
		}, json());
		
		post("/delete", (req, res) -> {
                        try{
	                    JSONObject json = (JSONObject) parser.parse(req.body());
			    String name = (String) json.get("name");
		 	    String type = (String) json.get("type");
		 	    String dryRun = null;
		 	    if ((String) json.get("dryRun") != null) {
			        dryRun = (String) json.get("dryRun");
			    }
			    String[] array = {name, dryRun};
			    POJOModelAction action = new POJOModelAction(type);
			    String result = action.delete(array);
			    return result;
			} catch (Exception e) {
			    return e;
			}
		}, json());
		get("/read", (req, res) -> {
			try{
                            JSONObject json = (JSONObject) parser.parse(req.body());
                            String name = (String) json.get("name");
                            String type = (String) json.get("type");
                            String dryRun = null;
                            if ((String) json.get("dryRun") != null) {
                                dryRun = (String) json.get("dryRun");
                            }
                            String[] array = {name, dryRun};
                            POJOModelAction action = new POJOModelAction
(type);
                            return action.read(array);
                        } catch (Exception e) {
                            return e;
                        }
		});
//
//
//
//		put("/users/:id", (req, res) -> userService.updateUser(
//				req.params(":id"),
//				req.queryParams("name"),
//				req.queryParams("email")
//		), json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}
