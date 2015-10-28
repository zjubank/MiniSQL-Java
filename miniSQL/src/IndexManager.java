package myminisql;

import java.io.File;

public class IndexManager {
	public static BufferManager buf;
	
	IndexManager(BufferManager buffer){
		buf=buffer;
	}

private static byte[] getColumnValue(Table  tableinfor,Index  indexinfor, byte[] row){
		
		int s_pos = 0, f_pos = 0;	
		for(int i= 0; i <= indexinfor.Attri; i++){ 
			s_pos = f_pos;
			f_pos+=tableinfor.attr[i].length;
		}
		byte[] colValue=new byte[f_pos-s_pos];
		for(int j=0;j<f_pos-s_pos;j++){//返回该子字符串，即为需要插入的索引字符串
			colValue[j]=row[s_pos+j];
		}
		return colValue;
	}
	
	//创建索引
	public static void createIndex(Table tableInfo,Index indexInfo){ //需要API提供表和索引信息结构
      	       	
        	BplusTree thisTree=new BplusTree(indexInfo); //创建一棵新树
        	
        	//开始正式建立索引
        	String filename=tableInfo.TableName+".table";       	
        	try{   	
        		for(int blockOffset=0; blockOffset< tableInfo.BlockNum; blockOffset++){
        			Buffer thisblock = BufferManager.readBlock(filename,blockOffset);
        			for(int offset =0; offset < thisblock.RecordNum ; offset++){
        				int position = offset*tableInfo.RecordLength; 
        				byte[] Record = thisblock.getBytes(position, tableInfo.RecordLength); //读取表中的每条记录
        				//if(Record.isEmpty()) break;
        				System.out.println(Record);
        				byte[] key=getColumnValue(tableInfo,indexInfo,Record); //找出索引值
        				System.out.println(key);
        				address a = new address();
        				a.blockOffset = blockOffset;
        				a.offset = offset;
        				thisTree.insert(key, a); //插入树中
        			}
        		}
        	}catch(NullPointerException e){
        		System.err.println("must not be null for key.");
        	}
        	catch(Exception e){
        		System.err.println("the index has not been created.");
        	}
        	
        	System.out.println("创建索引成功！");
	}
}
