package it.inaf.iasfpa.mpm.um232hmanager;


import java.util.ArrayList;

import net.sf.yad2xx.*;

public class UARTUm232H implements ProtocolInterface{

	public byte[] sndMsg;
	private Device dev;
	public ArrayList<Byte> buffer = new ArrayList<Byte>();
	public boolean eventStatus = false;
	public byte[] recMsg;
	private UM232HDeviceParameters param;

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
		
		byte parity;

		switch (param.getParity()) {
		case "None": {
			parity = FTDIConstants.FT_PARITY_NONE;
			break;

		}
		case "Even": {
			parity = FTDIConstants.FT_PARITY_EVEN;
			break;
		}
		case "Odd": {
			parity = FTDIConstants.FT_PARITY_ODD;
			break;
		}
		case "Mark": {
			parity = FTDIConstants.FT_PARITY_MARK;
			break;
		}
		case "Space": {
			parity = FTDIConstants.FT_PARITY_SPACE;
			break;
		}
		default: {
			parity = FTDIConstants.FT_PARITY_NONE;
		}
		}

		byte stopb;
		switch (param.getStopbit()) {
		case "1": {
			stopb = FTDIConstants.FT_STOP_BITS_1;
			break;
		}

		case "2": {
			stopb = FTDIConstants.FT_STOP_BITS_2;
			break;
		}
		default: {
			stopb = 1;
		}
		}
		
		try {
			dev.open();
			dev.setBaudRate(param.getBaudrate());
			dev.setDataCharacteristics((byte) param.getDatabit(), stopb, parity);
			dev.setTimeouts(50, 50);
			return true;
		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;}
	}
	
	@Override
	public void disconnect() {

		try {
			dev.close();
		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override	
	public void send(byte[] sendMessage){
		sndMsg = sendMessage;
		Thread t = new Thread(new SerialWriter());
		t.setPriority(10);
		t.start();
	}

	@Override	
	public byte[] receive() {
		buffer.clear();
		eventStatus = false;
		recMsg = null;
		Thread r = new Thread(new SerialReader());
		r.setPriority(10);
		r.start();
		int i = 1000;
		while (i != 0 && eventStatus == false) {
			try {
				Thread.sleep(10);
				i--;
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

		if (eventStatus) {
			eventStatus = false;

		} else {
			recMsg = null;

		}

		return recMsg;

	}
	
	@Override
	public byte[] sendReceive(byte[] sendMessage){

		send(sendMessage);
		byte[] r = receive();

		return r;

	}

	public class SerialWriter implements Runnable {

		public void run() {

			for (int i = 0; i < sndMsg.length; i++) {
				try {
					dev.write(sndMsg[i]);
				} catch (FTDIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public class SerialReader implements Runnable {

		@Override
		public void run() {
			try {
				byte[] bufferb = new byte[1];
				bufferb[0] = 0;
				byte pen = 0;
				boolean reading = true;
				while (reading) {
					pen = bufferb[0];
//					while(dev.getQueueStatus() == 0) {
//						
//					}
					
					dev.read(bufferb);
					buffer.add(bufferb[0]);
					if (pen == 0x0D && bufferb[0] == 0x0A) {
						reading = false;
					}

				}

				recMsg = new byte[buffer.size()];
				for (int j = 0; j < buffer.size(); j++) {
					recMsg[j] = buffer.get(j);
				}
				eventStatus = true;
				buffer.clear();

			} catch (FTDIException e) {

				e.printStackTrace();
			}
		}

	}

	




}
