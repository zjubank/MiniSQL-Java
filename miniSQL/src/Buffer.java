package myminisql;

public class Buffer {
	public String fileName;
	public byte[] block;
	
	//public int recordNum;
	public int blockOffset;
	public int RecordNum;
	public boolean dirtyBit;//是否被写过 
	public boolean lock;// 保护数据不被替换出去
			
	public  Buffer(String FileName,int Offset){
		fileName = FileName;
		this.blockOffset = Offset;
		dirtyBit = true;
		lock = false;
		block = new byte[4096];
		RecordNum=0;		
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
	
	public void setChar() {
		
	}
	
	public void getChar() {
		
	}
	
	public void setFloat() {
		
	}
	
	public float getFloat() {
		float value = (float) 1.0;
		return value;
	}
	
	public void insertkey(int start, byte[] key, address a) {
		setInt(start, a.blockOffset);
		setInt(start + 4, a.offset);	
		setBytes(start + 8, key);
		dirtyBit = true;
	} 
	
	public void insertkey(int start, byte[] key, int offset) {
		setInt(start, offset);
		setBytes(start + 4, key);
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
}
