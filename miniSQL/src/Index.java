package miniSQL;

public class Index {
	public String IndexName;
	public String TableName;
	public String AttributeName;
	
	public int Attri; 
	public int Size;
	public int RootNum;
	public int BlockNum = 1;
	
	public Index(String IndexName, String TableName, String AttributeName)
	{
		this.IndexName		= IndexName;
		this.TableName		= TableName;
		this.AttributeName	= AttributeName;
	}
//	public Index(String IndexName, String TableName, int AttributeName){
//		this.IndexName = IndexName;
//		this.TableName = TableName;
//		this.Attri = AttributeName;
//	}

}
