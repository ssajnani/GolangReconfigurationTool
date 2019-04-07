package com.architecture.PolicyAction;
import static spark.Spark.*;
import static com.architecture.PolicyAction.JsonUtil.*;
import java.util.*;
import java.lang.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class PolicyActionController {
        private JSONParser parser = new JSONParser(); 

	public PolicyActionController(final PolicyActionService policyAction) {

		get("/all", (req, res) -> policyAction.getAllPolicies(), json());
		//post("/delete", (req, res) -> {
                //        try{
	        //            JSONObject json = (JSONObject) parser.parse(req.body());
		//	    String name = (String) json.get("name");
		// 	    String type = (String) json.get("type");
		// 	    String dryRun = null;
		// 	    if ((String) json.get("dryRun") != null) {
		//	        dryRun = (String) json.get("dryRun");
		//	    }
		//	    String[] array = {name, dryRun};
		//	    PolicyActionAction action = new PolicyActionAction(type);
		//	    String result = action.delete(array);
		//	    return result;
		//	} catch (Exception e) {
		//	    return e;
		//	}
		//}, json());

		after((req, res) -> {
			res.type("application/json");
		});

		exception(IllegalArgumentException.class, (e, req, res) -> {
			res.status(400);
			res.body(toJson(new ResponseError(e)));
		});
	}
}
