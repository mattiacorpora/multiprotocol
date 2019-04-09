package it.inaf.iasfpa.mpm.um232hmanager;

import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.mpsse.Spi;

public class SPIUm232H implements ProtocolInterface {

	private Spi spi;
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
		this.spi = new Spi(dev, param.getClockrate(), param.getSpimode(), param.isCsStatus());
		boolean connectionStatus = false;
		try {
			spi.open();
			connectionStatus = true;
		} catch (FTDIException e) {
			e.printStackTrace();

		}
		return connectionStatus;
	}

	@Override
	public void disconnect() {
		spi.close();
	}

	@Override
	public void send(byte[] sendData) {
		try {
			spi.transactWrite(sendData);

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public byte[] receive() {
		try {
			spi.assertSelect();
			spi.readBits(param.getNumbRecByte() * 8);
			spi.clearSelect();
		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] sendReceive(byte[] sendData) {
		byte[] rec = null;
		try {

			rec = spi.transactReadWrite(sendData);

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rec;
	}

}
