package com.example.nettyserver.protocol.fixedheader;

/**
 * 自定义协议用到的常量
 * 
 * @author jumee
 *
 */
public class ConstantValue {
	/**
	 * 自定义协议的开始标识
	 */
	public static final byte[] HEAD_DATA = { (byte) 0xaa, (byte) 0x55 };
	/**
	 * 自定义协议的结束标识
	 */
	public static final byte[] CRC_DATA = { (byte) 0x1c, (byte) 0x1d };
}
