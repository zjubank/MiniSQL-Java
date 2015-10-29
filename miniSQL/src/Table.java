package miniSQL;

public class Table {
	public String TableName;
	
	public Attribute[] attr;
	public Attribute PrimaryKey;

	public int BlockNum;	//number of block the datas of the table occupied in the file name.table
	public int AttrNum;	//the number of attributes in the tables
	public int RecordLength;	//total length of one record, should be equal to sum(attributes[i].length)
	public int maxRecordsPerBlock;
	
	Table(String TableName, Attribute[] attr) {
		this.TableName = TableName;
		this.attr = attr;
		PrimaryKey = null;
		BlockNum = 1;
		RecordLength = 0;
		maxRecordsPerBlock = 0;
	}
	
	Table(String TableName, Attribute[] attr, Attribute PrimaryKey) {
		this.TableName = TableName;
		this.attr = attr;
		this.PrimaryKey = PrimaryKey;
		BlockNum = 1;
		RecordLength = 0;
		maxRecordsPerBlock = 0;
	}
	Table(String TableName) {
		this.TableName = TableName;
		attr = null;
		PrimaryKey = null;
		BlockNum = 1;
		RecordLength = 0;
		maxRecordsPerBlock = 0;
	}
}
