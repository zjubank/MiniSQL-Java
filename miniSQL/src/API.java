package miniSQL;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.table.TableStringConverter;

public class API {
//	private static final String String = null;
//	private static Exception Excep= new Exception();
	public static Database database = new Database();

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
		int Index_Table = -1;// = database.Tables.indexOf(tablename);
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName.equals(tablename) )
			{
				Index_Table = i;
			}
		}
		for( int i = 0; i < database.Tables.get(Index_Table).AttriNum; i++ )
		{
			if( database.Tables.get(Index_Table).Attributes.get(i).AttributeName.equals(AttributeName))
			{
				System.out.println("Attribute Already Existed!");
				return false;
			}
		}
//		System.out.println("Index = "+Index);
		if( Index_Table > -1 )
		{
			if( IfUnique || IfPrimer )
			{
				for( Attribute attr : database.Tables.get(Index_Table).Attributes )
				{
					if( attr.AttributeName == AttributeName)
					{
						System.out.println("Cannot insert two same value in a unique or primer attribute!");
					}
				}
			}
			Table temp_table = database.Tables.get(Index_Table);
			Record record = new Record(Type);
			
			temp_table.Add(tmp_attri);
			temp_table.Records.add(record);
			switch(Type)
			{
				case 0: case 1: temp_table.RecordLength += 11; break;
				case 2: temp_table.RecordLength += Scale;
				default: break;
			}
			
//			System.out.println("===Attri At:"+(temp_table.Attributes.size()-1)+"; Type:"+temp_table.Attributes.get(temp_table.Attributes.size()-1).Type);
			database.Tables.set(Index_Table, temp_table );
			database.Tables.get(Index_Table).Print();
			
			
			return true;
		}

		return false;
	}
	
	public static boolean CreateIndex( String IndexName, String TableName, String AttributeName ) throws IOException
	{
//		System.out.println("|**** Create Index ****|");
		int Index_Table = -1;
		for( int i = 0; i < database.Tables.size(); i++)
		{
			if( database.Tables.get(i).TableName.equals(TableName) )
			{
				Index_Table = i;
			}
		}
		if( Index_Table == -1 )
		{
			Exception.TableIndexError();
			return false;
		}
		
		int Attribute = -1;
		for( int i = 0; i < database.Tables.get(Index_Table).AttriNum; i++ )
		{
			if( database.Tables.get(Index_Table).Attributes.get(i).AttributeName.equals(AttributeName))
			{
				Attribute = i;
			}
		}
		if( Attribute == -1 )
		{
			Exception.AttriIndexError();
			return false;
		}
		
		Index indexInfo = new Index( IndexName, TableName, Attribute, database.Tables.get(Index_Table).Attributes.get(Attribute).ScaleByte);
		IndexManager.createIndex(database.Tables.get(Index_Table),indexInfo);
		
		Table temp_table = database.Tables.get(Index_Table);
		temp_table.AddIndex(indexInfo);
		
		Attribute temp_attri = temp_table.Attributes.get(Attribute);
		temp_attri.HasIndex = true;
		
		temp_table.Attributes.set(Attribute, temp_attri);
		database.Tables.set(Index_Table, temp_table);
		
		System.out.println("Index Created!");
		return true;
	}
	
	public static boolean Select( String TableName, WhereList wherelist ) throws IOException
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
//				output_table.Records.clear();
				OutputTable(output_table);
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
					
					System.out.println("=>AttriNum:"+output_table.AttriNum);
					for( int Index_Attri = 0; Index_Attri < output_table.AttriNum; Index_Attri++ )
					{
						Attribute temp_attri = output_table.Attributes.get(Index_Attri);
						if( output_table.Records.isEmpty() )
						{
							OutputTable(output_table);
							return true;
						}
						Record temp_record = output_table.Records.get(Index_Attri); 
						
						for( int Index_item = 0; Index_item < temp_attri.Length; Index_item++ )
						{

							if( output_table.Attributes.get(Index_Attri).HasIndex == true )
							//Use Index
							{
								System.out.println("Index on this attribute!");
								Table empty_table = new Table(database.Tables.get(Index_Table).TableName);
								int Index_Index = -1;
								for( Index_Index = -1; Index_Index < database.Tables.get(Index_Table).Indexs.size(); Index_Index++ )
								{
									if(database.Tables.get(Index_Table).Indexs.get(Index_Index).IndexName.equals(lvar))
									{
										break;
									}
								}
								Index temp_index = database.Tables.get(Index_Table).Indexs.get(Index_Index);
								
								int Type = database.Tables.get(Index_Table).Attributes.get(Index_Attri).Type;
								byte[] Record1 = null;
								byte[] Record2 = null;
								switch(sign)
								{
								case "=":
									if( Type == 0 )
									{
										Record1 = Buffer.writeInt(Integer.parseInt(rvar)); //rvar(int) to byte[]
										Record2 = Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
										Record2 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString(rvar);//rvar(string) to byte[]
										Record2 = Buffer.writeString(rvar);//rvar(string) to byte[]
									}
									ArrayList<address> add_0 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println("=:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_0 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									break;
								case "<>":
									if( Type == 0 )
									{
										Record1 =  Buffer.writeInt(-999999999);//-999999999 to byte[]
										Record2 =  Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat(-999999999);//-9999999.99 to byte[]
										Record2 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString("\1");//\1 to byte[]
										Record2 = Buffer.writeString(rvar);//rvar(float) to byte[]
									}
									ArrayList<address> add_1_0 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println("<>_>:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_1_0 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									if( Type == 0 )
									{
										Record1 = Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
										Record2 = Buffer.writeInt(999999999);//999999999 to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
										Record2 = Buffer.writeFloat(999999999);//9999999.99 to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString(rvar);//rvar(string) to byte[]
										Record2 = Buffer.writeString("\127");//????(a bit string) to byte[]
									}
									ArrayList<address> add_1_1 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println("<>_<:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_1_1 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									break;
								case "<":
									if( Type == 0 )
									{
										Record1 = Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
										Record2 = Buffer.writeInt(999999999);//999999999 to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
										Record2 = Buffer.writeFloat(999999999);//9999999.99 to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString(rvar);//rvar(string) to byte[]
										Record2 = Buffer.writeString("\127");//????(a bit string) to byte[]
									}
									ArrayList<address> add_2 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println("<:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_2 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									break;
								case ">":
									if( Type == 0 )
									{
										Record1 =  Buffer.writeInt(-999999999);//-999999999 to byte[]
										Record2 =  Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat(-999999999);//-9999999.99 to byte[]
										Record2 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString("\1");//\1 to byte[]
										Record2 = Buffer.writeString(rvar);//rvar(float) to byte[]
									}
									ArrayList<address> add_3 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println(">:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_3 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									break;
								case "<=":
									if( Type == 0 )
									{
										Record1 = Buffer.writeInt(Integer.parseInt(rvar)); //rvar(int) to byte[]
										Record2 = Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
										Record2 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString(rvar);//rvar(string) to byte[]
										Record2 = Buffer.writeString(rvar);//rvar(string) to byte[]
									}
									ArrayList<address> add_4_0 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println("<=_=:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_4_0 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									if( Type == 0 )
									{
										Record1 = Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
										Record2 = Buffer.writeInt(999999999);//999999999 to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
										Record2 = Buffer.writeFloat(999999999);//9999999.99 to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString(rvar);//rvar(string) to byte[]
										Record2 = Buffer.writeString("\127");//????(a bit string) to byte[]
									}
									ArrayList<address> add_4_1 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println("<=_<:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_4_1 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									break;
								case ">=":
									if( Type == 0 )
									{
										Record1 = Buffer.writeInt(Integer.parseInt(rvar)); //rvar(int) to byte[]
										Record2 = Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
										Record2 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString(rvar);//rvar(string) to byte[]
										Record2 = Buffer.writeString(rvar);//rvar(string) to byte[]
									}
									ArrayList<address> add_5_0 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println(">=_=:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_5_0 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									if( Type == 0 )
									{
										Record1 =  Buffer.writeInt(-999999999);//-999999999 to byte[]
										Record2 =  Buffer.writeInt(Integer.parseInt(rvar));//rvar(int) to byte[]
									}
									else if ( Type == 1 )
									{
										Record1 = Buffer.writeFloat(-999999999);//-9999999.99 to byte[]
										Record2 = Buffer.writeFloat( (int)( Double.parseDouble(rvar)*100 ) );//rvar(float) to byte[]
									}
									else if ( Type == 2 )
									{
										Record1 = Buffer.writeString("\1");//\1 to byte[]
										Record2 = Buffer.writeString(rvar);//rvar(float) to byte[]
									}
									ArrayList<address> add_5_1 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
									
									System.out.println(">=_>:Record1:"+Record1+", Record2"+Record2);
									for( address ad : add_5_1 )
									{
										System.out.println(ad.blockOffset+","+ad.offset);
									}
									
									break;
								}
							}
							else

							{
								switch(sign)
								{
								case "=": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) != Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) != Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && !temp_record.Str.get(Index_item).equals(rvar)) )
											{
												temp_record.drop(Index_item, temp_record.type);
												System.out.println("Selecteded At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case "<>": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) == Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) == Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).equals(rvar)) )
											{
												temp_record.drop(Index_item, temp_record.type);
												System.out.println("Selecteded At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case "<":
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) >= Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) >= Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>=0) )
											{
												temp_record.drop(Index_item, temp_record.type);
												System.out.println("Selecteded At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case ">":
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) <= Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) <= Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<=0) )
											{
												temp_record.drop(Index_item, temp_record.type);
												System.out.println("Selecteded At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case "<=": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) > Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) > Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>0) )
											{
												temp_record.drop(Index_item, temp_record.type);
												System.out.println("Dropped At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case ">=": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) < Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) < Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<0) )
											{
												temp_record.drop(Index_item, temp_record.type);
												System.out.println("Selecteded At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								}
							}//else
						}//for
						
						output_table.Attributes.set(Index_Attri, temp_attri);
						output_table.Records.set(Index_Attri, temp_record);
					}//for
				}//for
				database.Tables.set(Index_Table, output_table);
			}//else
			OutputTable(output_table);
		}//if
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
			System.out.print(output_table.Attributes.get(Index_Attri).AttributeName+"\t");
		}
		System.out.println("");
		
		for( int Index_Row = 0; Index_Row < output_table.RowNum; Index_Row++ )
		{
			for( int Index_Attri = 0; Index_Attri < output_table.AttriNum; Index_Attri++ )
			{
				int Type = output_table.Attributes.get(Index_Attri).Type;
				output_table.Records.get(Index_Attri).print(Index_Row, Type);
//				System.out +\t
			}
			System.out.println("");
		}
	}
	
	public static boolean Update( String All_Name, String All_Cond, String Rest )
	{
//		System.out.println("|**** Update ****|");
		return true;
	}
	
	public static boolean Detele( String TableName, WhereList wherelist ) throws IOException
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
				
				boolean[] deleteflag = new boolean[temp_table.RowNum];
				for( boolean ini : deleteflag )
				{
					ini = false;
				}
				//wherelist.lvars.size()次筛选，每次筛选都要遍历所有Attri，以及Attri中的item
				//一个函数确定是否符合wherelist规定，如果为否直接删除
				
				//对WhereList中所有条件对查验
				for( int Index_Vars = 0; Index_Vars < wherelist.lvars.size(); Index_Vars++ )
				{
					String lvar = wherelist.lvars.get(Index_Vars);
					String rvar = wherelist.rvars.get(Index_Vars);
					String sign = wherelist.signs.get(Index_Vars);
					
					//对每个Attribute从上到下搜索
					for( int Index_Attri = 0; Index_Attri < temp_table.AttriNum; Index_Attri++ )
					{
						Attribute temp_attri = temp_table.Attributes.get(Index_Attri);
						Record temp_record = temp_table.Records.get(Index_Attri);
						
						//从上到下搜索部分
						for( int Index_item = 0; Index_item < temp_attri.Length; Index_item++ )
						{
							switch(sign)
							{
							case "=": 
//								System.out.println("Match condition:"+lvar+sign+rvar);
//								System.out.println("At:"+Index_Attri+","+Index_item);
//								System.out.println("This Attri:"+temp_attri.AttributeName);
//								System.out.println("lvar:"+lvar);
								if( temp_attri.AttributeName.equals(lvar) ){
									System.out.println("Match lvar at Attri:"+Index_Attri+", Row:"+Index_item);
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) == Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) == Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).equals(rvar)) )
										{
											deleteflag[Index_item] = true;
											System.out.println("Delete at Attri:"+Index_Attri+", Row:"+Index_item);
//											boolean result = temp_record.drop(Index_item, temp_record.type);
//											System.out.println(result);
//											System.out.println("Dropped "+rvar+" Typed "+temp_record.type);
//											System.out.println("At Attri:"+Index_Attri+", Row:"+Index_item);
										}
								}
								break;
							case "<>": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) != Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) != Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && !temp_record.Str.get(Index_item).equals(rvar)) )
										{
											deleteflag[Index_item] = true;
											System.out.println("Delete at Attri:"+Index_Attri+", Row:"+Index_item);
//											temp_record.drop(Index_item, temp_record.type);
//											System.out.println("Dropped "+rvar+" Typed "+temp_record.type);
//											System.out.println("At Attri:"+Index_Attri+", Row:"+Index_item);
										}
								}
								break;
							case "<":
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) < Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) < Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<0) )
										{
											deleteflag[Index_item] = true;
											System.out.println("Delete at Attri:"+Index_Attri+", Row:"+Index_item);

//											temp_record.drop(Index_item, temp_record.type);
//											System.out.println("Dropped "+rvar+" Typed "+temp_record.type);
//											System.out.println("At Attri:"+Index_Attri+", Row:"+Index_item);
										}
								}
								break;
							case ">":
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) > Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) > Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>0) )
										{
											deleteflag[Index_item] = true;
											System.out.println("Delete at Attri:"+Index_Attri+", Row:"+Index_item);

//											temp_record.drop(Index_item, temp_record.type);
//											System.out.println("Dropped "+rvar+" Typed "+temp_record.type);
//											System.out.println("At Attri:"+Index_Attri+", Row:"+Index_item);
										}
								}
								break;
							case "<=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) <= Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) <= Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<=0) )
										{
											deleteflag[Index_item] = true;
											System.out.println("Delete at Attri:"+Index_Attri+", Row:"+Index_item);

//											temp_record.drop(Index_item, temp_record.type);
//											System.out.println("Dropped "+rvar+" Typed "+temp_record.type);
//											System.out.println("At Attri:"+Index_Attri+", Row:"+Index_item);
										}
								}
								break;
							case ">=": 
								if( temp_attri.AttributeName.equals(lvar) ){
									if( (temp_record.type==0 && temp_record.Int.get(Index_item) >= Integer.parseInt(rvar) ) ||
										(temp_record.type==1 && temp_record.Dou.get(Index_item) >= Double.parseDouble(rvar) ) ||
										(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>=0) )
										{
											deleteflag[Index_item] = true;
											System.out.println("Delete at Attri:"+Index_Attri+", Row:"+Index_item);

//											temp_record.drop(Index_item, temp_record.type);
//											System.out.println("Dropped "+rvar+" Typed "+temp_record.type);
//											System.out.println("At Attri:"+Index_Attri+", Row:"+Index_item);
										}
								}
								break;
							}
						}
//						temp_table.Attributes.set(Index_Attri, temp_attri);
//						temp_table.Records.set(Index_Attri, temp_record);
					}
				}
				
				Table new_table = new Table(TableName);
				new_table.AttriNum = temp_table.AttriNum;
				for( int i = 0; i < temp_table.AttriNum; i++ )
				{
					Attribute new_Attri=temp_table.Attributes.get(i);
					System.out.println("kmtest"+new_Attri.AttributeName);
					new_table.Attributes.add(new_Attri);
					System.out.println("=>"+new_table.Attributes.get(i).AttributeName);

				}
				for( int i = 0; i < temp_table.RowNum; i++ )
				{
					if( deleteflag[i] == false )
					{
						new_table.Records.add(temp_table.Records.get(i));
						new_table.RowNum++;
					}
				}
				
				int MaxRecsPerBlock = 64 / temp_table.RecordLength;
				temp_table.BlockNum = temp_table.RowNum / MaxRecsPerBlock;
				
				database.Tables.set(Index_Table, new_table);
				System.out.println(new_table);
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
			File Rfile = new File(TableName+".rec");
			File Cfile = new File(TableName+".cat");
			if( Rfile.exists() )
			{
				Rfile.delete();
			}
			if( Cfile.exists() )
			{
				Cfile.delete();
			}
		}
		else
		{
			Exception.DropError();
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
			System.out.println("=ValuesNo."+Index_ValueNo+". Valuse:"+Values[Index_ValueNo]);
			System.out.println("=Type: "+database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).Type);
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
					temp_table.Records.set(Index_ValueNo,temp_record);
					temp_table.Attributes.set(Index_ValueNo, temp_attri);
					
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
					temp_table.Records.set(Index_ValueNo,temp_record);
					temp_table.Attributes.set(Index_ValueNo, temp_attri);
					
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
						Exception.InsertError();
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
		
		int MaxRecsPerBlock = 64 / temp_table.RecordLength;
		temp_table.BlockNum = temp_table.RowNum / MaxRecsPerBlock;

		RecordManager1.GenerateRecordFile(temp_table);
		
		RecordManager1.ReadFile(temp_table.TableName);
		
		return true;
	}
	
}
