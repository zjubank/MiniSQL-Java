package miniSQL;

import java.io.IOException;
import java.util.*;

import javax.swing.table.TableStringConverter;

public class API {
//	private static final String String = null;
	public static Database database = new Database();
	private static Exception Excep= new Exception();
	
	public static boolean CreateDatabase( String DatabaseName )
	{
//		System.out.println("|**** Create Database ****|");
		return true;
	}
	

//Create Table －> Class Table
	//Table: String TableName
	public static boolean create_table( String tablename ) throws IOException
	{
		for( int i = 0; i < database.Tables.size(); i++ )
		{
			if( database.Tables.get(i).TableName == tablename )
				System.out.println("Table Already Existed!");
				return false;
		}
		Table tmp_table = new Table(tablename);
//		System.out.println("===Tablename: "+tablename);
		database.Tables.add( tmp_table );
//		System.out.println("===Inedx: "+ database.Tables.indexOf(tmp_table));
		//Then store 'Table table' somewhere
		return true;
	}
	
//Loop: Add Attribute -> Class Attribute
	//Attribute: String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer
	public static boolean add_attribute( String tablename, String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer  )
	{
		
		Attribute tmp_attri = new Attribute( AttributeName, Type, Length, Scale, Addit, IfUnique, IfPrimer);
//		System.out.println("New OK!");
		int Index = -1;// = database.Tables.indexOf(tablename);
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName == tablename )
			{
				Index = i;
			}
		}
//		System.out.println("Index = "+Index);
		if( Index > -1 )
		{
			database.Tables.get(Index).Add( tmp_attri );
			return true;
		}
		return false;
	}
	
	public static boolean CreateIndex( String IndexName, String TableName, String AttributeName )
	{
//		System.out.println("|**** Create Index ****|");
//		
		Index index = new Index( IndexName, TableName, AttributeName );
		//Then store 'Index index' somewhere
		//Use IndexManager
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
		int Index_Table = -1;
		for( int Index_num = 0; Index_num < database.Tables.size(); Index_num++)
		{
			if( database.Tables.get(Index_num).TableName.equals( TableName) )
			{
				Index_Table = Index_num;
			}
		}
		
		int Index_ValueNo = 0;
		Table temp=database.Tables.get(Index_Table);
		int i=0;
		while( Values[Index_ValueNo] != null )
		{
			//表已经匹配，和Values对应的Record（也就是Attribute）依次赋值
			switch(database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).Type)
			{
				//0:int, 1:float, 2:String
				case 0:
				{
					
					//Check if values is int
					break;
				}
				case 1:
				{
					//Check if values is float
					break;
				}
				case 2:
				{
					//Check if values is String
					if( Values[Index_ValueNo].charAt(0) == '\'' && Values[Index_ValueNo].charAt(Values[Index_ValueNo].length()-1) == '\''){
						Values[Index_ValueNo] = Values[Index_ValueNo].replaceAll("\'", "");
						System.out.println(Values[Index_ValueNo]);
//						int Index_Record = database.Tables.get(Index).Attributes.get(Index_ValueNo).Length;
						//同一个Index_ValueNo的Records，永远只会add同一个类型的值
						Record tempRecord=temp.Records.get(i);
						if (!tempRecord.add(Values[Index_ValueNo])){
							//Excep
						};
						
						temp.Records.set(i,tempRecord);
					}
					else
						Excep.InsertError();
					i++;
					break;
				}
				default: break;
			}
			
		}
		
		//赋值成功后Attribute.Length++
		database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).Length++;
		return true;
	}
	
}
