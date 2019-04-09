package it.inaf.iasfpa.mpm.um232hmanager;

import net.sf.yad2xx.Device;

public interface ProtocolInterface {

		public boolean connect();
		public void disconnect();
		public byte[] receive();
		public void send(byte[] sandData);
		public byte[] sendReceive(byte[] sendData);
		public void setUM232HDevice(Device dev);
		public void configureUM232HDevice(UM232HDeviceParameters par);
		
}
