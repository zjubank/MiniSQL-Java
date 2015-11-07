package miniSQL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class IndexManager {
	public static BufferManager buffer;
	
	IndexManager(BufferManager buffer){
		this.buffer=buffer;
	}

	private static byte[] getIndexAttribute(Table tableinfor, Index indexinfor, byte[] row){
		System.out.println(indexinfor.Attri);
		int begin = 0, end = 0;
		for(int i = 0; i <= indexinfor.Attri; i++){ 
			begin = end;
			end += tableinfor.Attributes.get(i).ScaleByte;
			System.out.println("Attr length = " + tableinfor.Attributes.get(i).ScaleByte);
			System.out.println(begin + "|" + end);
		}
		
		byte[] colValue=new byte[end-begin];
		for(int j = 0; j < end-begin; j++){ //返回该子字符串，即为需要插入的索引字符串
			colValue[j] = row[begin + j];
		}
		return colValue;
	}
	
	//创建索引
	public static void createIndex(Table tableInfo,Index indexInfo) throws IOException{ //需要API提供表和索引信息结构  	
		System.out.println(tableInfo.TableName);
		System.out.println(tableInfo.RecordLength);
		System.out.println(tableInfo.BlockNum);
		System.out.println(indexInfo.Attri);
		System.out.println(indexInfo.Size);
        	BplusTree thisTree = new BplusTree(indexInfo); //创建一棵新树
        	//开始正式建立索引
        	String filename=tableInfo.TableName+".rec"; 
			System.out.println("blocknum = "+tableInfo.BlockNum);
        	try{   	
        		for(int blockOffset= 0; blockOffset< tableInfo.BlockNum; blockOffset++){
        			Buffer thisblock = BufferManager.readRecord(filename, blockOffset, tableInfo.RecordLength);       			
        			for(int offset = 0; offset < thisblock.RecordNum ; offset++){
        				int position = offset * tableInfo.RecordLength; 
        				byte[] Record = thisblock.getBytes(position, tableInfo.RecordLength); //读取表中的每条记录
        				System.out.println(Arrays.toString(Record));
        				byte[] key = getIndexAttribute(tableInfo, indexInfo, Record); //找出索引值
        				System.out.println(Arrays.toString(key));
        			//	System.out.println(thisTree.Root.block[0]);
        				address a = new address();
        				a.blockOffset = blockOffset;
        				a.offset = offset;
        				thisTree.insert(key, a); //插入树中
        				//System.out.println(" root = "+ indexInfo.RootNum + " block num = "+indexInfo.BlockNum);	
        				BufferManager.BufferToFile(indexInfo.IndexName+".index");
        			}
        		}
        	}catch(NullPointerException e){
        		System.err.println("must not be null for key.");
        	}
        	catch(IndexOutOfBoundsException e){
        		System.err.println("the index has not been created.");
        	}
        	
        	System.out.println("Successfully");
	}
	
	public static void insertrecord(Table tableInfo, Index indexInfo, byte [] Record, address a) {
		byte[] key = getIndexAttribute(tableInfo, indexInfo, Record);
		BplusTree thisTree = new BplusTree(indexInfo,buffer,indexInfo.RootNum); //插入树中
		thisTree.insert(key, a);
		BufferManager.BufferToFile(indexInfo.IndexName+".index");
		System.out.println("Blocknum after insert = " + indexInfo.BlockNum);
		
	}
	
 /*	public static address searchrecord(Table tableInfo, Index indexInfo, byte [] Record) {
		byte[] key = getIndexAttribute(tableInfo, indexInfo, Record);
		BplusTree thisTree = new BplusTree(indexInfo,buffer,indexInfo.RootNum);
		address a = thisTree.search(key);
		return a;
	}*/
	
	public static ArrayList<address> searchrecord(Table tableInfo, Index indexInfo, byte [] key1, byte[] key2) {
		System.out.println(Arrays.toString(key1));
		System.out.println(Arrays.toString(key2));
		
		BplusTree thisTree = new BplusTree(indexInfo,buffer,indexInfo.RootNum);
		ArrayList<address> a = thisTree.search(key1, key2);
		for (int i = 0; i<a.size(); i++) {
			System.out.println("blockOffset = "+a.get(i).blockOffset+" offset = " + a.get(i).offset);
		}
		return a;
	}
	
	public static void deleterecord(Table tableInfo, Index indexInfo, byte[] Record) {
		byte[] key = getIndexAttribute(tableInfo, indexInfo, Record);
		BplusTree thisTree = new BplusTree(indexInfo,buffer,indexInfo.RootNum); //插入树中
		thisTree.delete(key);
		BufferManager.BufferToFile(indexInfo.IndexName+".index");
	}
	
	public static void dropindex(String indexname) {
		
		//String filename = indexInfo.IndexName;
		File fs = new File(indexname + ".index");
		fs.delete();
		fs = new File(indexname+".txt");
		fs.delete();
		System.out.println("the index has been deleted!");
	}
	
	public static Index rebulidIndex (Table tableInfo,String indexname) {
		FileInputStream fos;
		try {
			fos = new FileInputStream(indexname+".txt");
			byte[] b = new byte[11];
			fos.read(b);
			int attr = Buffer.readInt(b);
			fos.read(b);
			int size = Buffer.readInt(b);
			fos.read(b);
			int blocknum = Buffer.readInt(b);
			fos.read(b);
			int rootnum = Buffer.readInt(b);
			System.out.println("attr = "+attr + "size = "+size +"blocknum = "+blocknum+"rootnum = "+rootnum);

			Index indexInfo = new Index(indexname, tableInfo.TableName, attr,size,blocknum,rootnum);
			for (int i = 0; i < indexInfo.BlockNum ; i++) {
				Buffer thisblock = BufferManager.readBlock(indexInfo.IndexName+".index", i);
				if(BufferManager.head.next==null){
					BufferManager.head.next=thisblock;
					thisblock.previous=BufferManager.head;
				}
				else{
					BufferManager.tail.next=thisblock;
					thisblock.previous=BufferManager.tail;
				}
				BufferManager.tail = thisblock;
			}
			return indexInfo;
		//	BplusTree thisTree = new BplusTree(indexInfo, buffer, indexInfo.RootNum); //插入树中
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Index readindex(String tablename, String indexname) {
		FileInputStream fos;

		try {
			fos = new FileInputStream(indexname+".txt");
			byte[] b = new byte[11];
			fos.read(b);
			int attr = Buffer.readInt(b);
			fos.read(b);
			int size = Buffer.readInt(b);
			fos.read(b);
			int blocknum = Buffer.readInt(b);
			fos.read(b);
			int rootnum = Buffer.readInt(b);
			System.out.println("attr = "+attr + "size = "+size +"blocknum = "+blocknum+"rootnum = "+rootnum);

			Index indexInfo = new Index(indexname, tablename, attr,size,blocknum,rootnum);
			return indexInfo;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

