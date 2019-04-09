package it.inaf.iasfpa.mpm.um232hmanager;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;

import net.sf.yad2xx.*;

public class UM232HCommon {

	public ArrayList<Integer> getAvaiblePorts() {

		ArrayList<Integer> map = new ArrayList<Integer>();
		Device[] devs;

		try {
			devs = FTDIInterface.getDevices();
			// for (Device device : devs) {
			// if(!device.isOpen()) {
			// map.add(device.getIndex());
			// }
			// }
			for (int i = 0; i < devs.length; i++) {
				if (!devs[i].isOpen()) {
					map.add(i);
				}
			}
		} catch (FTDIException e) {
			map = null;
			e.printStackTrace();
		}

		return map;

	}

	public void programEeprom(Device dev, String mode) {

		File dump;

		switch (mode) {
		case "UART":
			dump = new File("./eeprom_dump/UART.bin");
			break;
		case "SPI":
			dump = new File("./eeprom_dump/fifo245.bin");
			break;
		case "I2C":
			dump = new File("./eeprom_dump/fifo245.bin");
			break;
		case "FIFO245":
			dump = new File("./eeprom_dump/fifo245.bin");
			break;
		case "CPU FIFO":
			dump = new File("./eeprom_dump/cpufifo.bin");
			break;
		case "FT128":
			dump = new File("./eeprom_dump/ft128.bin");
			break;
		case "OPTO ISOLATE":
			dump = new File("./eeprom_dump/optoisolate.bin");
			break;
		default:
			dump = new File("./eeprom_dump/UART.bin");
			break;

		}

		int[] data = dumpToIntArray(dump);
		try {
			dev.open();
			for (int i = 0; i < 128; i++) {
				dev.writeEE(i, data[i]);
			}
			dev.close();

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int[] dumpToIntArray(File dump) {
		int[] data = new int[128];
		try {
			byte[] src = Files.readAllBytes(dump.toPath());
			if (src.length == 256) {
				ByteBuffer bb = ByteBuffer.allocate(src.length);
				bb.put(src);
				bb.order(ByteOrder.BIG_ENDIAN);
				bb.position(0);
				for (int i = 0; i < src.length / 2; i++) {
					data[i] = (int) bb.getShort();

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return data;

	}

}
