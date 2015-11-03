package miniSQL;

public class Attribute {

	//By km:
	public String AttributeName="";
	
	public int Type		= 0;//int for default
	public int Length	= 0;//当前记录条数
	public int Scale	= 9;//Int最大长度(默认为9)
	public int Addit	= 4;//Double小数点位数(默认为4)
	public int BolckNum = 0;
	
	public boolean IfUnique = false;
	public boolean IfPrimer = false;
	
	public Attribute( String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer )
	{
		this.AttributeName	= AttributeName;
		this.Type			= Type;
		this.Length			= Length;
		this.Scale			= Scale;
		this.Addit			= Addit;
		this.IfUnique 		= IfUnique;
		this.IfPrimer 		= IfPrimer;
	}
	
//	By lhq
//	public boolean unique;
//	public boolean Primer;
//
//	public String name;
//	
//	public int length;
//	public int type;

}