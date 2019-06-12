package it.inaf.iasfpa.mpm.um232hmanager;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIBitMode;
import net.sf.yad2xx.FTDIException;

public class FIFOUm232H implements ProtocolInterface {

	
	private UM232HDeviceParameters param;
	private Device dev;

	@Override
	public boolean connect() {
		try {
			dev.open();
			return true;
		} catch (FTDIException e) {

			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void disconnect() {
		try {
			dev.close();
		} catch (FTDIException e) {

			e.printStackTrace();
		}

	}

	@SuppressWarnings("null")
	@Override
	public byte[] receive() {
		try {

			byte[] buffer = null;
			int index = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (param.isSyncMode()) {
				dev.setBitMode((byte) 0x00, FTDIBitMode.FT_BITMODE_SYNC_FIFO);
			}
			while (dev.getQueueStatus() != 0) {
				dev.read(buffer);
				baos.write(buffer, index, buffer.length);
				index += buffer.length;
			}

			return baos.toByteArray();

		} catch (FTDIException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void send(byte[] sendData) {

		int npack = sendData.length / param.getDeviceBufferSize();
		int ppack = sendData.length % param.getDeviceBufferSize();

		if (param.isSyncMode()) {
			try {
				dev.setBitMode((byte) 0xFF, FTDIBitMode.FT_BITMODE_SYNC_FIFO);
			} catch (FTDIException e) {

				e.printStackTrace();
			}
		}
		if (npack > 0) {
			try {
				for (int i = 0; i < npack; i++) {
					while (dev.getStatus().getTxBytes() != 0) {
					}
					dev.write(Arrays.copyOfRange(sendData, i * param.getDeviceBufferSize(),
							(i + 1) * param.getDeviceBufferSize()));
					Thread.sleep(param.getDelaypack());
				}
			} catch (FTDIException | InterruptedException e) {

				e.printStackTrace();
			}
		}
		if (ppack > 0) {
			try {

				while (param.getDeviceBufferSize() - dev.getStatus().getTxBytes() < ppack) {
				}
				dev.write(Arrays.copyOfRange(sendData, (npack * param.getDeviceBufferSize()), ((npack) * param.getDeviceBufferSize()) + ppack));

			} catch (FTDIException e) {

				e.printStackTrace();
			}
		}

	}

	@Override
	public byte[] sendReceive(byte[] sendData) {
		// questo metodo non può essere utilizzato per fifo
		return null;
	}

	@Override
	public void setUM232HDevice(Device dev) {
		this.dev = dev;

	}

	@Override
	public void configureUM232HDevice(UM232HDeviceParameters par) {
		param = par;

	}

}
