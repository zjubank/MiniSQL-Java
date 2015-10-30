package miniSQL;

import java.io.*;
import java.util.ArrayList;

public class Table {
	String TableName	= "";
	int ListNum			= 0;
	ArrayList<Attribute> table = new ArrayList<Attribute>();
	
	public Table(String tname)
	{
		this.TableName	= tname;
		this.ListNum	= 0;
	}
	
	boolean Add( Attribute attri ){
		//这块要放到interpreter或api中
		switch (ty.toLowerCase()){
			case "int": 	attri.Type=0; attri.Addit=-1;break;
			case "float": 	attri.Type=1; attri.Addit=add;break;
			case "string":	attri.Type=2; attri.Addit=-1;break; 
			default: return false;
		}
		//对就是上面这块
		
		for (int i=0; i<table.size();i++){
			if (table.get(i).AttributeName.equals(attri.AttributeName)){
				return false;
			}
		}
		
		table.add(attri);
		ListNum++;
		return true;//success
	}
	
	public boolean Delete(String name){
		for (int i=0; i<table.size();i++){
			if (table.get(i).AttributeName.equals(name)){
				table.remove(i);
				ListNum--;
				return true;//Success
			}
		}
		//Excep.
		
		return false;
	}
	
	public boolean PrintCreate() throws IOException
	{
		String filepath = TableName + ".cat";
		System.out.println("|**** Create Filepath:"+filepath);
		File file = new File(filepath);
		if( !file.exists() )
		{
			file.createNewFile();
		}
		PrintInsert();
		return true;
	}
	
	public boolean PrintInsert() throws IOException{
		String FileName=TableName+".cat";
		System.out.println("|**** Inserc Filepath:"+FileName);
		FileWriter fout=new FileWriter(FileName);
		fout.write(ListNum+"\n");
		for (int i=0;i<ListNum;i++){
			fout.write(table.get(i).AttributeName+"\n");
			fout.write(table.get(i).Length+"\n");
			fout.write(table.get(i).Type+"\n");
			fout.write(table.get(i).IfUnique+"\n");
			fout.write(table.get(i).IfPrimer+"\n");
		}
		fout.close();
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
