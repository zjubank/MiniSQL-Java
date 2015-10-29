package miniSQL;

import java.io.*;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;

public class BufferManager {
	static public int BlockNum = 0;
	
	static public Buffer create(String fileName, int blockOffset){
		//To Do
		BlockNum = BlockNum + 1;
		Buffer block = new Buffer(fileName,blockOffset);
		//To Do
		return block;
		
	}
	static public Buffer readBlock(String fileName, int blockOffset){
		
		Buffer thisblock = new Buffer(fileName,blockOffset);
		FileInputStream in;
		try {
			 in = new FileInputStream(fileName);
		}
		catch (IOException e) {
			in = null;
			e.printStackTrace();
		}
		int c=-1;
		byte b[] = new byte[4096];
		int offset = 0;
		if (in != null) {
		try {
			while((c=in.read(b))!=-1 && offset <= blockOffset){
				System.out.println(c);
				if (c < 4096) {
					for (int j = c; j < 4096 ; j++)
						b[j] = '*';
				}
				offset ++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		thisblock.block = b;
		return thisblock;
	}
}
