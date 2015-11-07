package miniSQL;

import java.util.Arrays;

public class Buffer {
	public String fileName;
	public byte[] block;
	public static final int Maxbyte = 64;
	//public int recordNum;
	public int blockOffset;
	public int RecordNum;
	public boolean dirtyBit;//是否被写过 
	public boolean lock;// 保护数据不被替换出去
	public Buffer previous;	
	public Buffer next;
	
	public  Buffer(String FileName,int Offset){
		fileName = FileName;
		this.blockOffset = Offset;
		dirtyBit = true;
		lock = false;
		block = new byte[Maxbyte];
		RecordNum=0;	
		this.clear(0, Maxbyte);
	}
	
	public  Buffer(){
		fileName = null;
		previous = null;
		next = null;
		dirtyBit = false;
		lock = false;
		block = new byte[Maxbyte];
		this.clear(0, Maxbyte);
	}
	public static int readInt(byte[] record) {
		int sum = 0;

		for (int i = 1; i < 11; i++) {
			sum = sum * 10 + record[i]-'0';
		}
		
		if (record[0] >= 0) 
	    return sum;
		else return -sum;
	}
	
	public static String readString(byte[] record) {
		String ans="";
		for (int i = 0; i < record.length; i++) {
			ans = ans + (char)record[i];
		}
		return ans;
	}
	
	public static byte[] writeInt(int record) {
		byte[] ans = new byte[11];
		if (record < 0) {
			ans[0] = '0';
			record = -record;
		}
		else ans[0] = '1';
		
		for (int i = 1; i < 11; i++) {
			System.out.println(record % 10);
			ans [11-i] = (byte) (record % 10 + 48);
			record = record / 10;
		}
		return ans;
	}
	
	public static byte[] writeString(String record, int stringlength) {
		byte[] result = new byte[stringlength];
		int complement = stringlength - record.length();
		for (int i = 0; i < complement; i++) {
			result[i] = 1;
		}
		System.out.println("==Record In:"+record);
		for( int i = complement; i < stringlength; i++)
		{
			System.out.println("i"+i+",byte:"+(byte) (record.charAt(i - complement)));
			result[i] = (byte) (record.charAt(i - complement));
		}
		System.out.println("==>Out:"+Arrays.toString(result));
		return result;
	}
	
	public void clear(int start, int length) {
		for (int i = start; i < start + length; i++) {
			block[i] = '*';
		}
	}
	public void setInt(int start, int value) {
		 for(int i = 0; i < 4; i++){
			   block[start + i] = (byte)(value >> 8 * (3 - i) & 0xFF);
		 }
		 dirtyBit=true;
	}
	
	public int getInt(int start) {
		int value = 0;
		for( int i = 0; i < 4; i++){
			 value += (block[start + i] & 0xFF) << (8 * (3 - i));
		}
		return value;
	}
	
	public static double readFloat(byte[] record) {
		double sum = 0;
		for (int i = 1; i < 7; i++) {
			sum = sum*10 + record[i]-'0';
		}
		for (int i = 8; i< 11; i++) {
			sum = sum*10 + record[i] - '0';
		}
		sum = sum *1.0 /1000;
		if (record[0] >= 0) 
	    return sum;
		else return -sum;
	}
	
	public static byte[] writeFloat(int record) {
		byte[] ans = new byte[11];
		if (record < 0) {
			ans[0] = '0';
			record = - record;
		}
		else ans[0] = '1';
		
		char[] num = new char[11];
		for (int i = 1; i < 4; i++) {
			System.out.println(record % 10);
			num [11-i] = (char)(record % 10 + '0');
			record = record / 10;
		}
		num[7] = '.';
		for (int i = 5; i < 11; i++) {
			System.out.println(record % 10);
			num [11-i] = (char)(record % 10 + '0');
			record = record / 10;
		}
		for (int i = 1; i < 11; i++) {
			ans[i] = (byte) num[i];
		}
		return ans;
	}
	
	public void insertkey(int start, byte[] key, address a) {
		setInt(start, a.blockOffset);
		setInt(start + 4, a.offset);	
		setBytes(start + 8, key);
		System.out.println("leaf : blockOffset = "+a.blockOffset+" offset = "+a.offset+" key = "+Arrays.toString(key));
		dirtyBit = true;
	} 
	
	public void insertkey(int start, byte[] key, int leftoffset,  int rightoffset) {
		setInt(start, leftoffset);
		setBytes(start + 4, key);
		setInt(start + 4 + key.length, rightoffset);
		//System.out.println("inner = "+offset+Arrays.toString(key));
		dirtyBit = true;
	}
	public  void setBytes(int start, byte[] b){
		for( int i = 0; i < b.length; i++){
			block[start + i] = b[i];
		}
		dirtyBit = true;
	}
	
	public  byte[] getBytes(int start, int length){
		byte[] b = new byte[length];
		for(int i = 0; i < length; i++){
			b[i] = block[start + i];
		}
		return b;
	}
	
	public void print() {
		System.out.println(Arrays.toString(block));
	}
}
