package miniSQL;

public class Index {
	public String IndexName;
	public String TableName;

	public int Attri; //索引建立在第几个attribute上面
	public int Size;
	public int RootNum; //根节点在当前Table的第几个Block上
	public int BlockNum = 1;
	
	public Index(String IndexName, String TableName, int Attribute){
		this.IndexName = IndexName;
		this.TableName = TableName;
		this.Attri = Attribute;
	}
}