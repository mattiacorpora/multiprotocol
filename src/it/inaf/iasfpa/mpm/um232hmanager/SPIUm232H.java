package it.inaf.iasfpa.mpm.um232hmanager;

import java.util.Arrays;

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
			int npack = sendData.length / param.getDeviceBufferSize();
			int nbytep = sendData.length % param.getDeviceBufferSize();
			if (npack > 0) {
				for (int i = 0; i < npack; i++) {
					spi.transactWrite(Arrays.copyOfRange(sendData, i * param.getDeviceBufferSize(),
							(i + 1) * param.getDeviceBufferSize()));
					Thread.sleep(param.getDelaypack());
				}

			}
			if (nbytep > 0) {
				spi.transactWrite(Arrays.copyOfRange(sendData, (npack * param.getDeviceBufferSize()),
						(npack * param.getDeviceBufferSize()) + nbytep));
				Thread.sleep(param.getDelaypack());
			}

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public byte[] receive() {
		byte[] rec = null;
		try {

			spi.assertSelect();
			rec = spi.readBits(param.getNumbRecByte() * 8);

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rec;
	}

	@Override
	public byte[] sendReceive(byte[] sendData) {

		int npack = sendData.length / param.getDeviceBufferSize();
		int nbytep = sendData.length % param.getDeviceBufferSize();
		byte[] rec = new byte[sendData.length];
		byte[] recT = null;
		try {
			if (npack > 1) {
				for (int i = 0; i < npack; i++) {

					recT = spi.transactReadWrite(Arrays.copyOfRange(sendData, i * param.getDeviceBufferSize(),
							(i + 1) * param.getDeviceBufferSize()));
					System.arraycopy(recT, 0, rec, i * param.getDeviceBufferSize(), param.getDeviceBufferSize());
					Thread.sleep(param.getDelaypack());
					
				}

			}
			if (nbytep > 0) {
				recT = spi.transactReadWrite(Arrays.copyOfRange(sendData, (npack * param.getDeviceBufferSize()),
						(npack * param.getDeviceBufferSize()) + nbytep));
				System.arraycopy(recT, 0, rec, npack*param.getDeviceBufferSize(), nbytep);
				Thread.sleep(param.getDelaypack());
			}
		} catch (FTDIException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rec;
	}

}
