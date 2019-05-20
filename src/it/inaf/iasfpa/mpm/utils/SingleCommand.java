package it.inaf.iasfpa.mpm.utils;

public class SingleCommand {
	
	private final String cmd;
	private final String wait;
	private final String rt;
	private final String msgtype;
	private final String address;
	private final String msg;
	private final String crc;
	private final String rOpt;
	
	public SingleCommand(String cmd, String wait, String rt, String msgtype, String msg, String rOpt, String address, String crc) {
		this.cmd = cmd;
		this.wait = wait;
		this.rt = rt;
		this.msgtype = msgtype;
		this.msg = msg;
		this.crc = crc;
		this.address = address;	
		this.rOpt = rOpt;
	}

	public String getCmd() {
		return cmd;
	}

	public String getWait() {
		return wait;
	}

	public String getRt() {
		return rt;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public String getMsg() {
		return msg;
	}

	public String getCrc() {
		return crc;
	}

	public String getAddress() {
		return address;
	}

	public String getrOpt() {
		return rOpt;
	}
	
}

