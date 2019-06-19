package it.inaf.iasfpa.mpm.utils;

public class SingleCommandLog {
	
	private final String istant;
	private final String threadSrc;
	private final String request;
	private final String response;
	
	public SingleCommandLog(String istant, String threadSrc, String request, String response) {
		
		this.istant = istant;
		this.threadSrc = threadSrc;
		this.request = request;
		this.response = response;
	}

	public String getIstant() {
		return istant;
	}

	public String getThreadSrc() {
		return threadSrc;
	}

	public String getRequest() {
		return request;
	}

	public String getResponse() {
		return response;
	}
	
	
}
