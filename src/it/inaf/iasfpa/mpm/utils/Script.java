package it.inaf.iasfpa.mpm.utils;

public class Script {
	private SingleCommand[] monitoring;
	private SingleCommand[] control;
	
	
	public SingleCommand[] getMonitoring() {
		return monitoring;
	}
	public void setMonitoring(SingleCommand[] monitoring) {
		this.monitoring = monitoring;
	}
	public SingleCommand[] getControl() {
		return control;
	}
	public void setControl(SingleCommand[] control) {
		this.control = control;
	}

}
