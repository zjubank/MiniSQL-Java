package miniSQL;

public class Index {
	public String IndexName;
	public String TableName;

	public int Attri; 
	public int Size;
	public int RootNum;
	public int BlockNum = 1;
	
	public Index(String IndexName, String TableName, int Attribute){
		this.IndexName = IndexName;
		this.TableName = TableName;
		this.Attri = Attribute;
	}
}
