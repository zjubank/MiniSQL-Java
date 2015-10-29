package miniSQL;

import java.io.*;
import java.util.ArrayList;

public class Table {
	String TableName="";
	int ListNum=0;
	ArrayList<Attribute> table=new ArrayList<Attribute>();
	boolean Create(String tname){
		TableName=tname;
		ListNum=0;
		return true;
	}
	
	boolean Add(boolean Uni, boolean Pri, String ty, String name, int scale, int add){
		Attribute temp = new Attribute();
		temp.unique=Uni;
		temp.Primer=Pri;
		temp.name=name;
		temp.scale=scale;
		temp.length=0;
		switch (ty.toUpperCase()){
			case "INT": 	temp.type=0; temp.addit=-1;break;
			case "FLOAT": 	temp.type=1; temp.addit=add;break;
			case "STRING":	temp.type=2; temp.addit=-1;break; 
			default: return false;
		}
		
		for (int i=0; i<table.size();i++){
			if (table.get(i).name.equals(temp.name)){
				return false;
			}
		}
		
		table.add(temp);
		ListNum++;
		return true;//success
	}
	
	
	
	
	boolean Delete(String name){
		for (int i=0; i<table.size();i++){
			if (table.get(i).name.equals(name)){
				table.remove(i);
				ListNum--;
				return true;//Success
			}
		}
		//Excep.
		
		return false;
	}
	
	boolean Print() throws IOException{
		String FileName=TableName+".cat";
		FileWriter fout=new FileWriter(FileName);
		fout.write(ListNum+"\n");
		for (int i=0;i<ListNum;i++){
			fout.write(table.get(i).name+"\n");
			fout.write(table.get(i).length+"\n");
			fout.write(table.get(i).type+"\n");
			fout.write(table.get(i).unique+"\n");
			fout.write(table.get(i).Primer+"\n");
		}
		return true;//success
	}
	
	boolean Read() throws IOException{
		String FileName=TableName+".cat";
		FileReader fin=new FileReader(FileName);
		BufferedReader bf= new BufferedReader(fin);
		String Stemp;
		Stemp=bf.readLine();
		ListNum=Integer.parseInt(Stemp);
		for (int i=0;i<ListNum;i++){
			boolean Uni=false,Pri=false;
			int len=0, sca=9, ty=0, add=-1;
			String n;
			n=bf.readLine();
			
			Stemp=bf.readLine();
			len=Integer.parseInt(Stemp);
			
			Stemp=bf.readLine();
			ty=Integer.parseInt(Stemp);
			
			Stemp=bf.readLine();
			if (Stemp.equalsIgnoreCase("true"))
				Uni=true;
			
			Stemp=bf.readLine();
			if (Stemp.equalsIgnoreCase("true"))
				Pri=true;
		}
		return true;//success
	}
//	By lhq
//	public String TableName;
//	
//	public Attribute[] attr;
//	public Attribute PrimaryKey;
//
//	public int BlockNum;	//number of block the datas of the table occupied in the file name.table
//	public int AttrNum;	//the number of attributes in the tables
//	public int RecordLength;	//total length of one record, should be equal to sum(attributes[i].length)
//	public int maxRecordsPerBlock;
//	
//	Table(String TableName, Attribute[] attr) {
//		this.TableName = TableName;
//		this.attr = attr;
//		PrimaryKey = null;
//		BlockNum = 1;
//		RecordLength = 0;
//		maxRecordsPerBlock = 0;
//	}
//	
//	Table(String TableName, Attribute[] attr, Attribute PrimaryKey) {
//		this.TableName = TableName;
//		this.attr = attr;
//		this.PrimaryKey = PrimaryKey;
//		BlockNum = 1;
//		RecordLength = 0;
//		maxRecordsPerBlock = 0;
//	}
//	Table(String TableName) {
//		this.TableName = TableName;
//		attr = null;
//		PrimaryKey = null;
//		BlockNum = 1;
//		RecordLength = 0;
//		maxRecordsPerBlock = 0;
//	}
}
