package com.architecture.pojo_model;
public interface AtomicAction {
	String create(String[] info);
	String delete(String[] info);
	String update(String[] info);
	Object read(String[] info);
}
