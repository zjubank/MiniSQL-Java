package miniSQL;

public class Attribute {

	//By km:
	public String AttributeName="";
	
	public int Type		= 0;//int for default
	public int Length	= 0;
	public int Scale	= 9;//default as 9
	public int Addit	=-1;//default as -1
	
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