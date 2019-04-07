package com.architecture.PolicyAction;
public class Action {
	
    String operation = null;
    String name = null;
    String namespace = null;
    String filepath = null;
    String patchString = null;
    String type = null;

    public Action(String operation, String name, String type, String namespace, String filepath, String patchString) {
	this.operation = operation;
	this.name = name;
	this.namespace = namespace;
	this.filepath = filepath;
	this.patchString = patchString;
	this.type = type;
    }

    public String getOperation() {
	return this.operation;
    }
    public void setOperation(String operation) {
	this.operation = operation;
    }
    public String getName() {
	return this.name;
    }
    public void setName(String name) {
	this.name = name;
    }
    public String getType() {
	return this.type;
    }
    public void setType(String Type) {
	this.type = Type;
    }
    
    public String getNamespace() {
	return this.namespace;
    }
    public void setNamespace(String namespace) {
	this.namespace = namespace;
    }
    public String getFilepath() {
	return this.filepath;
    }
    public void setFilepath(String filepath) {
	this.filepath = filepath;
    }
    public String getPatchString() {
	return this.patchString;
    }
    public void setPatchString(String patchString) {
	this.patchString = patchString;
    }
    
}
