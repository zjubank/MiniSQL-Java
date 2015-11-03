package miniSQL;

import java.io.*;
import java.util.ArrayList;

public class Table {
	String TableName	= "";
	int AttriNum		= 0;
	int RowNum			= 0;
	int BlockNum		= 0;
	ArrayList<Attribute>	Attributes	= new ArrayList<Attribute>();
	ArrayList<Record>		Records		= new ArrayList<Record>();
	
	public Table(String tname) throws IOException
	{
		this.TableName	= tname;
		this.AttriNum	= 0;
		PrintCreate();
	}
	
	boolean Add( Attribute attri ){
		for (int i=0; i<Attributes.size();i++){
			if (Attributes.get(i).AttributeName.equals(attri.AttributeName)){
				return false;
			}
		}
		
		Attributes.add(attri);
		AttriNum++;
		return true;//success
	}
	
	//删除单条记录
	public boolean Delete(String name){
		for (int i=0; i<Attributes.size();i++){
			if (Attributes.get(i).AttributeName.equals(name)){
				Attributes.remove(i);
				AttriNum--;
				return true;//Success
			}
		}
		//Excep.
		
		return false;
	}
	
	public boolean PrintCreate() throws IOException
	{
		String filepath = TableName + ".cat";
//		System.out.println("|**** Create Filepath:"+filepath);
		File file = new File(filepath);
		if( !file.exists() )
		{
			file.createNewFile();
		}
		return true;
	}
	
	public boolean Print() throws IOException{
		String FileName=TableName+".cat";
//		System.out.println("|**** Inserc Filepath:"+FileName);
		FileWriter fout=new FileWriter(FileName);
		int x=0;
		for (int i=0;i<AttriNum;i++){
			x += this.Attributes.get(i).Scale;
			
		}
		x = (int) Math.ceil((double)x/Database.MaxBlock);
		this.BlockNum = x;
		fout.write(AttriNum+"\n");
		for (int i=0;i<AttriNum;i++){
			fout.write(Attributes.get(i).AttributeName+"\n");
			fout.write(Attributes.get(i).Length+"\n");
			fout.write(Attributes.get(i).Type+"\n");
			fout.write(Attributes.get(i).IfUnique+"\n");
			fout.write(Attributes.get(i).IfPrimer+"\n");
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
		AttriNum=Integer.parseInt(Stemp);
		for (int i=0;i<AttriNum;i++){
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
	
	
	
	void InsertElement(int index,String s){
		
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
