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
		database.Tables.add( tmp_table );
//		RecordManager1.GenerateRecordFile(tmp_table);
		RecordManager1.GenerateRecordFile(tmp_table);
		//Then store 'Table table' somewhere
		return true;
	}
	
//Loop: Add Attribute -> Class Attribute
	//Attribute: String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer
	public static boolean add_attribute( String tablename, String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer  ) throws IOException
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
			

			Record record = new Record(Type);
			temp_table.Records.add(record);
//			System.out.println("===Attri At:"+(temp_table.Attributes.size()-1)+"; Type:"+temp_table.Attributes.get(temp_table.Attributes.size()-1).Type);
			database.Tables.set(Index, temp_table );
			database.Tables.get(Index).Print();
			
			
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
	
	public static boolean Select( String TableName, WhereList wherelist )
	{
//		System.out.println("|**** Select ****|");
		int Index_Table = -1;// = database.Tables.indexOf(tablename);
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName.equals(TableName) )
			{
				Index_Table = i;
			}
		}
		if( Index_Table > -1 )
		{
			Table output_table = database.Tables.get(Index_Table);
			
			if( wherelist == null ){
				output_table.Records.clear();
//				temp_table.Attributes.LengthClear();
				return true;
			}
			else{
				//wherelist.lvars.size()次筛选，每次筛选都要遍历所有Attri，以及Attri中的item
				//一个函数确定是否符合wherelist规定，如果为否直接删除
				for( int Index_Vars = 0; Index_Vars < wherelist.lvars.size(); Index_Vars++ )
				{
					String lvar = wherelist.lvars.get(Index_Vars);
					String rvar = wherelist.rvars.get(Index_Vars);
					String sign = wherelist.signs.get(Index_Vars);
					for( int Index_Attri = 0; Index_Attri < output_table.AttriNum; Index_Attri++ )
					{
						Attribute temp_attri = output_table.Attributes.get(Index_Attri);
						Record temp_record = output_table.Records.get(Index_Attri); 
						for( int Index_item = 0; Index_item < temp_attri.Length; Index_item++ )
						{
							switch(sign)
							{
							case "=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) != Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) != Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && !temp_record.Str.get(Index_item).equals(rvar)) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case "<>": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) == Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) == Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).equals(rvar)) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case "<":
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) >= Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) >= Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>=0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case ">":
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) <= Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) <= Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<=0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case "<=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) > Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) > Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case ">=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) < Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) < Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							}
						}
						output_table.Attributes.set(Index_Attri, temp_attri);
						output_table.Records.set(Index_Attri, temp_record);
					}
				}
				database.Tables.set(Index_Table, output_table);
			}
			OutputTable(output_table);
		}
		return true;
	}
	
	public static Table FormatTable( Table table )
	{
		return table;
	}
	
	public static void OutputTable( Table output_table )
	{
		output_table = FormatTable( output_table );
		
		for( int Index_Attri = 0; Index_Attri < output_table.AttriNum; Index_Attri++ )
		{
			System.out.print(output_table.Attributes.get(Index_Attri).AttributeName);
		}
		System.out.println("");
		
		for( int Index_Row = 0; Index_Row < output_table.RowNum; Index_Row++ )
		{
			int Type = output_table.Attributes.get(Index_Row).Type;
			for( int Index_Attri = 0; Index_Attri < output_table.AttriNum; Index_Attri++ )
			{
				output_table.Records.get(Index_Attri).print(Index_Attri, Type);
			}
			System.out.println("");
		}
	}
	
	public static boolean Update( String All_Name, String All_Cond, String Rest )
	{
//		System.out.println("|**** Update ****|");
		return true;
	}
	
	public static boolean Detele( String TableName, WhereList wherelist )
	{
//		System.out.println("|**** Delete ****|");
		int Index_Table = -1;// = database.Tables.indexOf(tablename);
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName.equals(TableName) )
			{
				Index_Table = i;
			}
		}
		
		if( Index_Table > -1 )
		{
			Table temp_table = database.Tables.get(Index_Table);
			
			if( wherelist == null ){
				temp_table.Records.clear();
//				temp_table.Attributes.LengthClear();
				return true;
			}
			else{
				//wherelist.lvars.size()次筛选，每次筛选都要遍历所有Attri，以及Attri中的item
				//一个函数确定是否符合wherelist规定，如果为否直接删除
				for( int Index_Vars = 0; Index_Vars < wherelist.lvars.size(); Index_Vars++ )
				{
					String lvar = wherelist.lvars.get(Index_Vars);
					String rvar = wherelist.rvars.get(Index_Vars);
					String sign = wherelist.signs.get(Index_Vars);
					for( int Index_Attri = 0; Index_Attri < temp_table.AttriNum; Index_Attri++ )
					{
						Attribute temp_attri = temp_table.Attributes.get(Index_Attri);
						Record temp_record = temp_table.Records.get(Index_Attri); 
						for( int Index_item = 0; Index_item < temp_attri.Length; Index_item++ )
						{
							switch(sign)
							{
							case "=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) == Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) == Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).equals(rvar)) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case "<>": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) != Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) != Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && !temp_record.Str.get(Index_item).equals(rvar)) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case "<":
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) < Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) < Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case ">":
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) > Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) > Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case "<=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) <= Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) <= Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<=0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							case ">=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) >= Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) >= Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>=0) )
										temp_record.drop(Index_item, temp_record.type);
								}
								break;
							}
						}
						temp_table.Attributes.set(Index_Attri, temp_attri);
						temp_table.Records.set(Index_Attri, temp_record);
					}
				}
				database.Tables.set(Index_Table, temp_table);
			}
		}
		return true;
	}
	
	public static boolean DropTable( String TableName ) 
	{
//		System.out.println("|**** Drop ****|");
		int Index_Table = -1;// = database.Tables.indexOf(tablename);
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName.equals(TableName) )
			{
				Index_Table = i;
			}
		}
		if( Index_Table > -1 )
		{
			database.Tables.remove(Index_Table);
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
	
	public static boolean Insert( String TableName, String[] Values) throws NumberFormatException, IOException
	{
//		System.out.println("|**** Insert ****|");
		int Index_Table = -1;
		for( int Index_num = 0; Index_num < database.Tables.size(); Index_num++)
		{
//			System.out.println("Table:"+database.Tables.get(Index_num).TableName);
//			System.out.println("LookingFor:"+TableName);
			if( database.Tables.get(Index_num).TableName.equals( TableName) )
			{
				Index_Table = Index_num;
			}
		}
		if( Index_Table == -1 )
		{
			return false;
		}
//		System.out.println("Index_Table:"+Index_Table);
		
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
//					System.out.println("Insert Int: "+temp_int);
					Record temp_record = temp_table.Records.get(Index_ValueNo);
					Attribute temp_attri = temp_table.Attributes.get(Index_ValueNo);
					
					if (!temp_record.Int.add(temp_int))
					{
						//Excep
						return false;
					}
					System.out.println("Add Int Success: "+temp_int);
					temp_attri.Length++;
//					System.out.println("Records Value:"+temp_table.Records.get(Index_ValueNo).Int.get(0));
					temp_table.Records.add(Index_ValueNo,temp_record);
					temp_table.Attributes.add(Index_ValueNo, temp_attri);
					
					break;
				}
				case 1:
				{
					System.out.println("Catch Double!");
					double temp_double = 0;
					try{
						temp_double = Double.parseDouble(Values[Index_ValueNo]);
						System.out.println("Read double: "+temp_double);
					}
					catch( NumberFormatException e )
					{
						System.out.println("Cannot read double!");
						return false;
					}
					
					Record temp_record = temp_table.Records.get(Index_ValueNo);
					Attribute temp_attri = temp_table.Attributes.get(Index_ValueNo);
					
					if (!temp_record.Dou.add(temp_double))
					{
						//Excep
						System.out.println("Add Double False!");
						return false;
					}
					System.out.println("Add Double Success: "+temp_double);
					temp_attri.Length++;
					temp_table.Records.add(Index_ValueNo,temp_record);
					temp_table.Attributes.add(Index_ValueNo, temp_attri);
					
					break;
				}
				case 2:
				{
					//Check if values is String
					if( Values[Index_ValueNo].charAt(0) == '\'' && Values[Index_ValueNo].charAt(Values[Index_ValueNo].length()-1) == '\''){
						Values[Index_ValueNo] = Values[Index_ValueNo].replaceAll("\'", "");
//						System.out.println(Values[Index_ValueNo]);
//						int Index_Record = database.Tables.get(Index).Attributes.get(Index_ValueNo).Length;
						//同一个Index_ValueNo的Records，永远只会add同一个类型的值
//						System.out.println("Index_ValueNo:"+Index_ValueNo);
						Record temp_record = temp_table.Records.get(Index_ValueNo);
						Attribute temp_attri = temp_table.Attributes.get(Index_ValueNo);
						if( temp_table.Records.isEmpty() ){
							//excep()
							return false;
						}
						if (!temp_record.Str.add(Values[Index_ValueNo])){
							//Excep
//							System.out.println(3);
							return false;
						}
						System.out.println("Add String Success: "+Values[Index_ValueNo]);
						temp_attri.Length++;
						temp_table.Records.set(Index_ValueNo,temp_record);
						temp_table.Attributes.set(Index_ValueNo, temp_attri);
					}
					else
						Excep.InsertError();
					break;
				}
				default: 
					System.out.println("Type not match!");
					break;
			}
			Index_ValueNo++;			
		}
		//因为一次只加一行
		temp_table.RowNum++;
		database.Tables.set(Index_Table, temp_table);
		System.out.println("Table Added OK!");
//		System.out.println("!!!"+database.Tables.get(Index_Table).Records.get(0).Str.get(0));
		RecordManager1.GenerateRecordFile(temp_table);
		return true;
	}
	
}
