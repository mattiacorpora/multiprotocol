package it.inaf.iasfpa.mpm.um232hmanager;

import java.util.ArrayList;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.mpsse.I2C;

public class I2CUm232H implements ProtocolInterface {

	private I2C i2c;
	private UM232HDeviceParameters param;
	private Device dev;
	
	@Override
	public void setUM232HDevice(Device dev) {
		this.dev = dev;
		
	}
	
	@Override
	public void configureUM232HDevice(UM232HDeviceParameters par) {
		this.param = par;
		
	}
	
	
	@Override
	public boolean connect() {
		i2c = new I2C(dev, param.getClockrate());
		boolean connectionStatus = false;
		try {
			i2c.open();
			i2c.delay(100);
			connectionStatus = true;
		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		param.setListSlaveAddress(scanI2Cbus(0, 127));
		return connectionStatus;
	}
	
	@Override
	public void disconnect() {
		i2c.close();
	}
	
	@Override
	public void send(byte[] sendData) {
		try {

			i2c.transactWrite(param.getSlaveAddress(), sendData);
			i2c.delay(100);

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public byte[] receive() {
		byte[] receivedData;
		if(param.isAckreceive()) {
			receivedData = readDataAck();
		} else {
			receivedData = readDataNack();
		}
		return receivedData;
	}
	
	@Override
	public byte[] sendReceive(byte[] sendData) {
		// i2c non prevede sand/receive
		return null;
	}

		

	private byte[] readDataAck() {
		byte[] readData = null;
		try {

			readData = i2c.transactRead(param.getSlaveAddress(), param.getNumbRecByte());
			i2c.delay(100);

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return readData;

	}

	private byte[] readDataNack() {
		byte[] b = new byte[1];
		try {

			i2c.delay(100);
			i2c.start();
			i2c.writeAddress(param.getSlaveAddress(), true);
			b[0] = i2c.readWithNak();
			i2c.stop();
			i2c.delay(100);

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return b;

	}

	
	public  ArrayList<Integer> scanI2Cbus(int startAddress, int endAddress) {

		ArrayList<Integer> slaveAddress = new ArrayList<Integer>();
		
		try {
			
			i2c.delay(100);
			
			for (int i = startAddress; i < endAddress; i++) {
				i2c.start();
				if (i2c.writeAddress((byte) i, true)) {
					slaveAddress.add(i);
				}

				i2c.stop();
				i2c.delay(100);
				
			}
			

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return slaveAddress;

	}

	
	



}
