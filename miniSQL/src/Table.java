package miniSQL;

import java.io.*;
import java.util.ArrayList;

public class Table {
	String TableName	= "";
	int AttriNum		= 0;
	int RowNum			= 0;
	int BlockNum		= 0;
	int RecordLength	= 0;//一个table里所有attri的长度
	int HasIndex		= 0;
	ArrayList<Attribute>	Attributes	= new ArrayList<Attribute>();
	ArrayList<Record>		Records		= new ArrayList<Record>();
	ArrayList<Index>		Indexs		= new ArrayList<Index>();
	
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
		this.RecordLength += attri.ScaleByte;
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
	
	
	public void AddIndex(Index indexinfo)
	{
		this.Indexs.add(indexinfo);
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
