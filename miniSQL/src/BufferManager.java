package miniSQL;

import java.io.*;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
import java.util.Arrays;

public class BufferManager {
	static public int BlockNum = 0;
	static Buffer head = new Buffer();
	static Buffer tail = new Buffer();
	static public Buffer create(String fileName, int blockOffset){
		//To Do
		BlockNum = BlockNum + 1;
		Buffer block = new Buffer(fileName,blockOffset);
		if(head.next==null){
			head.next=block;
			block.previous=head;
		}
		else{
			tail.next=block;
			block.previous=tail;
		}
		tail = block;
		//To Do
		return block;
		
	}
	static public Buffer readBlock(String fileName, int blockOffset){
		
		Buffer thisblock = new Buffer(fileName,blockOffset);
		FileInputStream in;
		try {
			in = new FileInputStream(fileName);
			
			
			int c=-1;
			byte b[] = new byte[Buffer.Maxbyte+1];
			//System.out.println("b = "+Arrays.toString(b));
			int offset = 0;
			if (in != null) {
				try {
					while(offset <= blockOffset &&(c=in.read(b))!=-1){
						if (c < Buffer.Maxbyte) {
							for (int j = c; j < Buffer.Maxbyte ; j++)
								b[j] = '*';
						}
						offset ++;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte g[] = new byte[Buffer.Maxbyte];
				for (int j = 0; j < Buffer.Maxbyte; j++) {
					g[j] = b[j+1];
				}
				thisblock.block = g;
				thisblock.blockOffset = b[0];
				System.out.println("blocknum = " + thisblock.blockOffset);
			}
			in.close();
		}
		catch (IOException e) {
			in = null;
			e.printStackTrace();
		}
		return thisblock;
	}
	static public Buffer getblock (int blockOffset) {
		Buffer readblock = head.next;
		while (readblock != null) {
			if (readblock.blockOffset == blockOffset) 
				return readblock;
			readblock = readblock.next;
		}
		return null;
	}
	static public Buffer readRecord(String fileName, int blockOffset, int RecordLength){
		
		Buffer thisblock = new Buffer(fileName,blockOffset);
		FileInputStream in;
		try {
			 in = new FileInputStream(fileName);
		
			int c = -1;
			byte b[] = new byte[Buffer.Maxbyte];
			int offset = 0;
			if (in != null) {
				try {
					while(offset <= blockOffset && (c=in.read(b))!=-1){
						System.out.println("c = " + c);
						if (c < Buffer.Maxbyte) {
							for (int j = c; j < Buffer.Maxbyte ; j++)
								b[j] = '*';
						}
						offset ++;
						thisblock.RecordNum = c / RecordLength;
						System.out.println("RecordNum = "+thisblock.RecordNum);
					}
					thisblock.block = b;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			in.close();	
			}
		}
		catch (IOException e) {
			in = null;
			e.printStackTrace();
		}
		
		
		return thisblock;
	}
	
	public static void BufferToFile(String filename) {
		Buffer writeblock = head.next;
		File file =new File(filename);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			while (writeblock!=null) {
				System.out.println("Block No."+ writeblock.blockOffset+" "+Arrays.toString(writeblock.block));
				fos.write(writeblock.blockOffset);
				fos.write(writeblock.block);
				//System.out.println("写入成功");
				writeblock = writeblock.next;
			}
			fos.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入成功");
	}
	
	public static void remove(Buffer deleteblock) {
		Buffer loopblock = head.next;
		while (loopblock != null) {
			//System.out.print(loopblock.blockOffset);
			if (loopblock.blockOffset == deleteblock.blockOffset) {
				if (tail == deleteblock) {
					tail = loopblock.previous;
					loopblock.previous.next = null;
					break;
				}
				loopblock.previous.next = loopblock.next;
				loopblock.next.previous = loopblock.previous;
				break;
			}
			loopblock = loopblock.next;
		}
		//System.out.println("_____________________");
		/*loopblock = head.next;
		while (loopblock != null) {
			System.out.print(loopblock.blockOffset);
			loopblock = loopblock.next;
		}*/
		//System.out.println("");
	}
}
