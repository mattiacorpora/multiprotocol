package it.inaf.iasfpa.mpm.utils;

public class DataManager {

	public static byte[] hexStringToByte(String msg) {

		String[] req = msg.split("(?<=\\G..)");
		byte[] sendMsg = new byte[req.length];
		for (int i = 0; i < req.length; i++) {
			sendMsg[i] = (byte) (Integer.decode("0x" + req[i]) & 0x000000ff);
		}
		return sendMsg;

	}

	public static String ByteToHexString(byte[] res) {

		String file_string = "";
		int[] rec1 = new int[res.length];
		for (int i = 0; i < res.length; i++) {
			rec1[i] = (res[i] & 0xFF);
			if (rec1[i] <= 0x0F) {
				file_string += "0";
			}
			file_string += Integer.toHexString(rec1[i]);
		}
		return file_string;

	}

	public static String ByteToHexString(byte res) {

		String file_string = "";
		int rec1;
		rec1 = (res & 0xFF);
		if (rec1 <= 0x0F) {
			file_string += "0";
		}
		file_string += Integer.toHexString(rec1);
		return file_string;
	}

	public static String ByteToCharString(byte[] res) {

		String file_string = "";
		for (int i = 0; i < res.length; i++) {
			file_string += (char) res[i];
		}
		return file_string;
	}

}
