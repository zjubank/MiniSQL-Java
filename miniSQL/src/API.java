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
			if( database.Tables.get(i).TableName.equals(tablename) )
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
			if( database.Tables.get(i).TableName.equals(tablename) )
			{
				Index = i;
			}
		}
//		System.out.println("Index = "+Index);
		if( Index > -1 )
		{
			Table temp_table = database.Tables.get(Index);
			temp_table.Add(tmp_attri);
			database.Tables.set(Index, temp_table );
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
		int Index = -1;// = database.Tables.indexOf(tablename);
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName.equals(TableName) )
			{
				Index = i;
			}
		}
		if( Index > -1 )
		{
			database.Tables.remove(Index);
		}
		else
		{
			Excep.DropError();
		}
		return true;
	}
	
	public static boolean DropIndex( String IndexName )
	{
		return true;
	}
	
	public static boolean Insert( String TableName, String[] Values) throws NumberFormatException
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
		Table temp_table = database.Tables.get(Index_Table);
		while( Values[Index_ValueNo] != null )
		{
			//表已经匹配，和Values对应的Record（也就是Attribute）依次赋值
			switch(database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).Type)
			{
				//0:int, 1:float, 2:String
				case 0:
				{
					int temp_int = 0;
					try{
						temp_int = Integer.parseInt(Values[Index_ValueNo]);
					}
					catch( NumberFormatException e )
					{
						return false;
					}
					
					Record temp_record = temp_table.Records.get(Index_ValueNo);
					Attribute temp_attri = temp_table.Attributes.get(Index_ValueNo);
					
					if (!temp_record.add(temp_int))
					{
						//Excep
						return false;
					}
					temp_attri.Length++;
					
					temp_table.Records.set(Index_ValueNo,temp_record);
					temp_table.Attributes.set(Index_ValueNo, temp_attri);
					
					break;
				}
				case 1:
				{
					double temp_double = 0;
					try{
						temp_double = Double.parseDouble(Values[Index_ValueNo]);
					}
					catch( NumberFormatException e )
					{
						return false;
					}
					
					Record temp_record = temp_table.Records.get(Index_ValueNo);
					Attribute temp_attri = temp_table.Attributes.get(Index_ValueNo);
					
					if (!temp_record.add(temp_double))
					{
						//Excep
						return false;
					}
					temp_attri.Length++;
					
					temp_table.Records.set(Index_ValueNo,temp_record);
					temp_table.Attributes.set(Index_ValueNo, temp_attri);
					
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
						Record temp_record = temp_table.Records.get(Index_ValueNo);
						Attribute temp_attri = temp_table.Attributes.get(Index_ValueNo);
						if (!temp_record.add(Values[Index_ValueNo])){
							//Excep
							return false;
						}
						temp_attri.Length++;
						temp_table.Records.set(Index_ValueNo,temp_record);
						temp_table.Attributes.set(Index_ValueNo, temp_attri);
					}
					else
						Excep.InsertError();
					break;
				}
				default: break;
			}
			
		}
		Index_ValueNo++;
		database.Tables.set(Index_ValueNo, temp_table);
		return true;
	}
	
}
