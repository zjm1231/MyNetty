package com.example.nettyserver.protocol.fixedheader;

import java.util.Arrays;

/**
 * <pre>
 *  
 * 自定义的协议 
 *  数据包格式 
 * +——--------——+——---------——+——---------——+——---------——+——--------——+ 
 * | 协议开始标识|    功能位      |   数据长度     |   数据内容     |  结束标识     |
 * +——--------——+——---------——+——---------——+——---------——+——--------——+ 
 * 1.协议开始标志head_data，16进制表示为0xAA,0x55  【2字节】
 * 2.功能位  【1字节】
 * 2.传输数据的长度contentLength  【4字节】
 * 2.传输数据的内容
 * 3.结束标识，2字节，16进制表示为0X1C,0X1D  【2字节】
 * </pre>
 * 
 * 1.byte 是字节数据类型 ，是有符号型的，占1个字节；大小范围为-128—127 。 2.char 是字符数据类型 ，是无符号型的，占2字节(Unicode码 ）；大小范围 是0—65535 ； char是一个16位二进制的Unicode字符，JAVA用char来表示一个字符 。(char 表示中文单字占连个字节,表示英文字符占一个字节)
 * 3.int是整型数据类型，是有符号型的，占用4个字节，大小范围为：-2147483648到2147483647 4.long / double 8个字节 ，float 4个字节 5.short 2个字节
 */
public class FixedHeaderProtocol {

	/**
	 * 消息的开头的信息标志
	 */
	private byte[] head_data = ConstantValue.HEAD_DATA;
	/**
	 * 功能位
	 */
	private byte control;
	/**
	 * 消息的长度
	 */
	private int contentLength;
	/**
	 * 消息的内容
	 */
	private byte[] content;

	/**
	 * 消息结束的标识位
	 */
	private byte[] crc_data = ConstantValue.CRC_DATA;

	/**
	 * 用于初始化，FixedHeaderProtocol
	 * 
	 * @param control
	 *            协议里面，功能位
	 * @param contentLength
	 *            协议里面，消息数据的长度
	 * @param content
	 *            协议里面，消息的数据内容
	 */
	public FixedHeaderProtocol(byte control, int contentLength, byte[] content) {
		this.control = control;
		this.contentLength = contentLength;
		this.content = content;
	}

	public byte[] getHead_data() {
		return head_data;
	}

	public void setHead_data(byte[] head_data) {
		this.head_data = head_data;
	}

	public byte getControl() {
		return control;
	}

	public void setControl(byte control) {
		this.control = control;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public byte[] getCrc_data() {
		return crc_data;
	}

	public void setCrc_data(byte[] crc_data) {
		this.crc_data = crc_data;
	}

	@Override
	public String toString() {
		return "FixedHeaderProtocol [head_data=" + Arrays.toString(head_data) + ", control=" + control + ", contentLength=" + contentLength + ", content=" + Arrays.toString(content) + ", crc_data="
				+ Arrays.toString(crc_data) + "]";
	}

}
