package miniSQL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Database {
	ArrayList<Table> Tables = new ArrayList<Table>();
	public static int MaxBlock = 64;
	
	public Database(){
		System.out.println("Database Size:"+Tables.size());
	}
	
	public static void Rebuild( File f ) throws IOException
	{
		String filename = f.getName();
		String tablename = filename.substring(0, filename.indexOf("."));
//		Table temp_table = new Table(tablename);
		
//		int Index_Table = -1;
//		for( int i = 0; i < API.database.Tables.size(); i++ )
//		{
//			if( API.database.Tables.get(i).TableName.equals(tablename))
//			{
//				Index_Table = i;
//				break;
//			}
//		}
//		if( Index_Table == -1 )
//		{
			Table temp_table = RecordManager1.ReadFile(tablename);
			System.out.println("Pre:"+API.database.Tables.size());
			API.database.Tables.add( temp_table );
			System.out.println("After:"+API.database.Tables.size());
//			System.out.println(API.database.Tables.get(0).Records.get(0).Str.get(0));
			Table temp_table_2 = API.database.Tables.get(0);
//		}
//		else
//		{
//			API.database.Tables.set(Index_Table, RecordManager1.ReadFile(tablename));
//		}
	}
	
//	public static void RebuildCat(File f_cat) throws IOException
//	{
//		String filename = f_cat.getName();
//		String tablename = filename.substring(0, filename.indexOf("."));
//		Table temp_table = new Table(tablename);
//		
//		FileInputStream file = new FileInputStream(f_cat);
//		InputStreamReader fin = new InputStreamReader(file);
//		BufferedReader bin = new BufferedReader(fin);
//		String str = "";
//		
//		
//		
//		str = bin.readLine();
//		int AttriNum = Integer.parseInt(str);
//		
//		int Index_Attri = 0;
//		for( ; Index_Attri < AttriNum; Index_Attri++ )
//		{
//			str = bin.readLine(); String AttributeName = str;
//			str = bin.readLine(); int Type = Integer.parseInt(str);
//			str = bin.readLine(); int Length = Integer.parseInt(str);
//			str = bin.readLine(); int Scale = Integer.parseInt(str);
//			str = bin.readLine(); int Addit = Integer.parseInt(str);
//			str = bin.readLine(); boolean IfUnique = Boolean.parseBoolean(str);
//			str = bin.readLine(); boolean IfPrimer = Boolean.parseBoolean(str);
//			
//			Attribute temp_attri = new Attribute(AttributeName, Type, Length, Scale, Addit, IfUnique, IfPrimer);
//			switch(Type)
//			{
//			case 0: case 1: temp_attri.ScaleByte = 11; break;
//			case 2: temp_attri.ScaleByte = Scale; break;
//			default: break;
//			}
//			temp_table.Attributes.add(temp_attri);
//		}
//		API.database.Tables.add(temp_table);
//	}
//	
//	public static void RebuildRec(File f_rec) throws IOException
//	{
//		String filename = f_rec.getName();
//		String tablename = filename.substring(0, filename.indexOf("."));
//		Table temp_table = new Table(tablename);
//		
////		FileInputStream file = new FileInputStream(f_rec);
////		InputStreamReader fin = new InputStreamReader(file);
////		BufferedReader bin = new BufferedReader(fin);
////		String str = "";
////		
////		int Index_Table = -1;
////		for( ; Index_Table < API.database.Tables.size(); Index_Table++ )
////		{
////			if( API.database.Tables.get(Index_Table).TableName.equals(tablename))
////			{
////				break;
////			}
////		}
//		
//		RecordManager1.ReadFile(tablename);
//	}
}