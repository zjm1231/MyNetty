package com.example.utils;

public class StringUtils {
	
	public static String byte2HexString(byte[] bytes) {
		String hex = "";
		if (bytes != null) {
			for (Byte b : bytes) {
				hex += String.format("%02X", b.intValue() & 0xFF);
			}
		}
		return hex;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		try {
			for (int i = 0; i < len; i += 2) {
				data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
			}
		} catch (Exception e) {
			System.err.println("Argument(s) for hexStringToByteArray(String s)" + "was not a hex string");
		}
		return data;
	}
	
}
