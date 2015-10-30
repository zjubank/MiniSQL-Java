package miniSQL;

import java.io.IOException;
import java.util.*;

public class API {
//	private static final String String = null;
	
	public static boolean CreateDatabase( String DatabaseName )
	{
//		System.out.println("|**** Create Database ****|");
		return true;
	}
	

//Create Table －> Class Table
	//Table: String TableName
	public static boolean create_table( String tablename ) throws IOException
	{
		Table table = new Table( tablename );
		//Then store 'Table table' somewhere
		return true;
	}
	
//Loop: Add Attribute -> Class Attribute
	//Attribute: String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer
	public static boolean add_attribute( Table table, String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer  )
	{
		Attribute attri = new Attribute(AttributeName, Type, Length, Scale, Addit, IfUnique, IfPrimer);
		table.Add( attri );
		return true;
	}
	
	public static boolean CreateIndex( String IndexName, String TableName, String AttributeName )
	{
//		System.out.println("|**** Create Index ****|");
//		
		Index index = new Index( IndexName, TableName, AttributeName );
		//Then store 'Index index' somewhere
		return true;
	}
	
	public static boolean Select( String TableName, String After_Where )
	{
//		System.out.println("|**** Select ****|");
		
		return true;
	}
	
	public static boolean Update( String All_Name, String All_Cond, String Rest )
	{
//		System.out.println("|**** Update ****|");
		return true;
	}
	
	public static boolean Detele( String TableName, String After_Where )
	{
//		System.out.println("|**** Delete ****|");
		return true;
	}
	
	public static boolean DropTable( String TableName )
	{
//		System.out.println("|**** Drop ****|");
		return true;
	}
	
	public static boolean DropIndex( String IndexName )
	{
		return true;
	}
	
	public static boolean Insert( String TableName, String[] Values)
	{
//		System.out.println("|**** Insert ****|");
		return true;
	}
	
}
