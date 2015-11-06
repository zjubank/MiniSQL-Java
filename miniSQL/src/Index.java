package miniSQL;

public class Index {
	public String IndexName;
	public String TableName;

	public int Attri; //索引建立在第几个attribute上面
	public int Size;
	public int RootNum; //根节点在当前Table的第几个Block上
	public int BlockNum = 1;
	
	public Index(String IndexName, String TableName, int Attribute, int Size){
		this.IndexName = IndexName;
		this.TableName = TableName;
		this.Attri = Attribute;
		this.Size = Size;
	}
	public Index(String indexname, String tableName, int attr, int size, int blocknum, int rootnum) {
		this.IndexName = indexname;
		this.TableName = tableName;
		this.Attri = attr;
		this.Size = size;
		this.BlockNum = blocknum;
		this.RootNum = rootnum;
	}
}