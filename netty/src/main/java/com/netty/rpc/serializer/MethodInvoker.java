package com.netty.rpc.serializer;

import java.io.Serializable;

public class MethodInvoker implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6644047311439601478L;

	private Class clazz;
	private String method;
	private Object[] args;

	public MethodInvoker(String method, Object[] args, Class clazz) {
		super();
		this.method = method;
		this.args = args;
		this.clazz = clazz;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

}
