package it.inaf.iasfpa.mpm.utils;

public class DataErrorControl {

	public byte checkSum(byte[] data) {
		byte checksum = 0;
		for(int i = 0; i< data.length; i++) {
			checksum ^= data[i];
		}
		return checksum;
		
	}
	
	public byte[] crc16Calc(byte[] bytes) {

		int crc = 0xFFFF;
		int polynomial = 0x11021;

		for (byte b : bytes) {
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit)
					crc ^= polynomial;
			}
		}

		crc &= 0xffff;
		byte[] crcByte = new byte[2];
		crcByte[1] = (byte) (((0xFF00 & crc) >> 7) & 0x00FF);
		crcByte[0] = (byte) (0x00FF & crc);
		
		return crcByte;

	}
}
