package it.inaf.iasfpa.mpm.um232hmanager;

import java.util.ArrayList;

import net.sf.yad2xx.mpsse.SpiMode;

public class UM232HDeviceParameters {
	
	private int clockrate;
	private int baudrate;
	private String parity;
	private int databit;
	private String stopbit;
	private int numbRecByte;
	private int slaveAddress;
	private SpiMode spimode;
	private boolean ackreceive;
	private boolean csStatus;
	private ArrayList<Integer> listSlaveAddress;
	private boolean syncMode;
	
	public boolean isSyncMode() {
		return syncMode;
	}
	public void setSyncMode(boolean syncMode) {
		this.syncMode = syncMode;
	}
	public int getClockrate() {
		return clockrate;
	}
	public void setClockrate(int clockrate) {
		this.clockrate = clockrate;
	}
	
	public int getBaudrate() {
		return baudrate;
	}
	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}
	
	public String getParity() {
		return parity;
	}
	public void setParity(String parity) {
		this.parity = parity;
	}
	
	public int getDatabit() {
		return databit;
	}
	
	public void setDatabit(int databit) {
		this.databit = databit;
	}
	
	public String getStopbit() {
		return stopbit;
	}
	public void setStopbit(String stopbit) {
		this.stopbit = stopbit;
	}
	
	public int getNumbRecByte() {
		return numbRecByte;
	}
	public void setNumbRecByte(int numbRecByte) {
		this.numbRecByte = numbRecByte;
	}
	
	public int getSlaveAddress() {
		return slaveAddress;
	}
	public void setSlaveAddress(int slaveAddress) {
		this.slaveAddress = slaveAddress;
	}
	
	public SpiMode getSpimode() {
		return spimode;
	}
	public void setSpimode(SpiMode spimode) {
		this.spimode = spimode;
	}
	
	public boolean isAckreceive() {
		return ackreceive;
	}
	public void setAckkreceive(boolean ackNackreceive) {
		this.ackreceive = ackNackreceive;
	}
	
	public boolean isCsStatus() {
		return csStatus;
	}
	public void setCsStatus(boolean csStatus) {
		this.csStatus = csStatus;
	}
	
	public ArrayList<Integer> getListSlaveAddress(){
		return listSlaveAddress;
	}
	public void setListSlaveAddress(ArrayList<Integer> list) {
		this.listSlaveAddress = list;
	}
}
