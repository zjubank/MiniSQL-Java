package miniSQL;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

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
	
	public static void GETSNAME (String tablename, String sname, int x) throws IOException{
		int index_table = -1;
		for( int i = 0; i < database.Tables.size();i++)
		{
			if( database.Tables.get(i).TableName.equals(tablename))
			{
				System.out.println("TABLE FIND!");
				index_table = i;
				break;
			}
		}
		Table t=database.Tables.get(index_table);
		int index_attri = -1;
		for (int i=0;i<t.AttriNum;i++){
			if (sname.equals(t.Attributes.get(i).AttributeName)){
				Attribute a = t.Attributes.get(i);
				if (x==1)
				{
					a.IfUnique=true;
					System.out.println("Unique: "+sname);
				}
				if (x==2) 
				{
					a.IfUnique=true;
					a.IfPrimer=true;
					System.out.println("Pri: "+sname);
				}
				t.Attributes.set(i, a);
				index_attri = i;
			}
		}
		database.Tables.set(index_table, t);
		System.out.println(database.Tables.get(index_table).Attributes.get(index_attri).IfUnique);
		CatalogManager.Print(t);
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
		RecordManager1.GenerateRecordFile(tmp_table);
		//Then store 'Table table' somewhere
		return true;
	}
	
//Loop: Add Attribute -> Class Attribute
	//Attribute: String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer
	public static boolean add_attribute( String tablename, String AttributeName, int Type, int Length, int Scale, int Addit, boolean IfUnique, boolean IfPrimer  ) throws IOException
	{
		boolean thisunique = false, thisprimer = false;
		
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
		if( Index_Table == -1 )
		{
			System.err.println("[ERROR] No such table!");
			return false;
		}
		for( int i = 0; i < database.Tables.get(Index_Table).AttriNum; i++ )
		{
			if( database.Tables.get(Index_Table).Attributes.get(i).AttributeName.equals(AttributeName))
			{
				System.out.println("Attribute Already Existed!");
				return false;
			}
			thisunique = database.Tables.get(Index_Table).Attributes.get(i).IfUnique;
			thisprimer = database.Tables.get(Index_Table).Attributes.get(i).IfPrimer;
		}
		tmp_attri.IfPrimer = thisprimer;
		tmp_attri.IfUnique = thisunique;
		
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
			
//			System.out.println("===Attri At:"+(temp_table.Attributes.size()-1)+"; Type:"+temp_table.Attributes.get(temp_table.Attributes.size()-1).Type);
			database.Tables.set(Index_Table, temp_table );
//			System.out.println("########RecordLength:"+temp_table.RecordLength);
			CatalogManager.Print( database.Tables.get(Index_Table) );
			
			return true;
		}

		return false;
	}
	
	public static boolean AutoIndex( String TableName ) throws IOException
	{
		int Index_Table = -1;
		String AttriName = "";
		for( int i = 0; i < database.Tables.size(); i++ )
		{
			if( database.Tables.get(i).TableName.equals(TableName))
			{
				Index_Table = i;
				break;
			}
		}
		if( Index_Table == -1 )
		{
			return false;
		}
		else
		{
			for( int i = 0; i < database.Tables.get(Index_Table).Attributes.size(); i++)
			{
				if( database.Tables.get(Index_Table).Attributes.get(i).IfPrimer )
				{
					AttriName = database.Tables.get(Index_Table).Attributes.get(i).AttributeName;
					break;
				}
			}
			CreateIndex(TableName, TableName, AttriName);
		}
		return true;
	}
	
	public static boolean CreateIndex( String IndexName, String TableName, String AttributeName ) throws IOException
	{
		System.out.println("|**** Create Index ****|");
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
		
		//Attribute：建在第几个attribute上么
		Index indexInfo = new Index( IndexName, TableName, Attribute, database.Tables.get(Index_Table).Attributes.get(Attribute).ScaleByte);
		System.out.println("IndexName = " + IndexName + "TableName = " + TableName+"Attribute = "+ Attribute + "Size = " + database.Tables.get(Index_Table).Attributes.get(Attribute).ScaleByte);
		
		IndexManager.createIndex(database.Tables.get(Index_Table),indexInfo);
		
		Table temp_table = database.Tables.get(Index_Table);
		temp_table.AddIndex(indexInfo);
		
		Attribute temp_attri = temp_table.Attributes.get(Attribute);
		temp_attri.HasIndex = true;
		temp_table.HasIndex++;
		
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
			Table emptytable = new Table(output_table.TableName);

			boolean[] deleteflag = new boolean[output_table.RowNum];
			for( boolean b : deleteflag )
			{
				b = false;
			}
			
			address output_address = new address();
			if( output_table.Records.isEmpty() )
			{
				System.out.println("==>Empty Table:");
				OutputTable(output_table);
				return true;
			}	
			
			if( wherelist == null ){
//				output_table.Records.clear();
				OutputTable(output_table);
				return true;
			}
			else{
				//逐个条件判断
				//判断完一个条件后将add和output_address取交集
				for( int Index_Vars = 0; Index_Vars < wherelist.lvars.size(); Index_Vars++ )
				{
					String lvar = wherelist.lvars.get(Index_Vars);
					String rvar = wherelist.rvars.get(Index_Vars);
					String sign = wherelist.signs.get(Index_Vars);
					
					System.out.println("=>AttriNum:"+output_table.AttriNum);
					
					//逐个Attribute判断
					//如果Attribute的name不匹配lvar，直接检索下一个Attribute
					//如果匹配了
						//先看这个Attribute有没有Index，如果有直接调
						//如果没有Index，再在这个Attribute下查找Record，匹配rvar
					for( int Index_Attri = 0; Index_Attri < output_table.AttriNum; Index_Attri++ )
					{
						if( !output_table.Attributes.get(Index_Attri).AttributeName.equals(lvar) )
						{
							continue;
						}
						else
						{
							System.out.println("==lvar at:"+output_table.Attributes.get(Index_Attri).AttributeName);
						}
						
						

						if( output_table.Attributes.get(Index_Attri).HasIndex == true )
						{
							Attribute temp_attri = output_table.Attributes.get(Index_Attri);
							Record temp_record = output_table.Records.get(Index_Attri); 
							
							int Index_Index = Index_Attri;//由上面那个if,在这个attri查到有index，那自然编号就一样
							ArrayList<address> add = new ArrayList<address>();
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
									Record1 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(string) to byte[]
									Record2 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(string) to byte[]
								}
								System.out.println("=:Record1:"+Record1+", Record2"+Record2);

								ArrayList<address> add_0 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
								for (int i = 0; i < output_table.RowNum; i++) {
									deleteflag[i] = true;
								}
								for( address ad : add_0 )
								{
									System.out.println("==>add_0");
									System.out.println("===>Finall Address: ( "+ad.blockOffset+", "+ad.offset+" )");
									System.out.println("output_table.RecordLength = "+output_table.RecordLength);
									
									deleteflag[ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset] = false;
								}
								
								
								break;
							case "<>":
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
									Record1 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(string) to byte[]
									Record2 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(string) to byte[]
								}
								System.out.println("=:Record1:"+Record1+", Record2"+Record2);

								ArrayList<address> add_1 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
								
								for (int i = 0; i < output_table.RowNum; i++) {
									deleteflag[i] = false;
								}
								for( address ad : add_1 )
								{
									System.out.println("==>add_1");
									System.out.println("===>Finall Address: ( "+ad.blockOffset+", "+ad.offset+" )");
									System.out.println("output_table.RecordLength = "+output_table.RecordLength);
			
									deleteflag[ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset] = true;
								}
								
								
								break;
							case ">":
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
									Record1 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(string) to byte[]
									byte[] temp_b = new byte[temp_attri.ScaleByte];
									for (int i = 0; i < temp_b.length; i++) {
										temp_b[i] = 127;
									}
									Record2 = temp_b;
									//Record2 = Buffer.writeString("\127",temp_attri.ScaleByte);//????(a bit string) to byte[]
								}
								System.out.println("<:Record1:"+Record1+", Record2"+Record2);
								
								ArrayList<address> add_2 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
								
								for( address ad : add_2 )
								{
									add.add(ad);
								}
								
								for (int i = 0; i < output_table.RowNum; i++) {
									deleteflag[i] = true;
								}
								int total = 0;
								for( address ad : add_2 )
								{
									System.out.println("==>add_2");
									System.out.println("===>Finall Address: ( "+ad.blockOffset+", "+ad.offset+" )");
									if (total > 0)
										deleteflag[ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset] = false;
									total = total +1;
								}
								
								break;
							case "<":
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
									byte[] temp_b = new byte[temp_attri.ScaleByte];
									for (int i = 0; i < temp_b.length; i++) {
										temp_b[i] = 1;
									}
									Record1 = temp_b;
									//Record1 = Buffer.writeString("\1",temp_attri.ScaleByte);//\1 to byte[]
									Record2 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(float) to byte[]
								}
								System.out.println(">:Record1:"+Record1+", Record2"+Record2);
								
								ArrayList<address> add_3 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
								
								for( address ad : add_3 )
								{
									add.add(ad);
								}
								
								for (int i = 0; i < output_table.RowNum; i++) {
									deleteflag[i] = true;
								}
								int last = -1;
								for( address ad : add_3 )
								{
									System.out.println("add_3");
									System.out.println("===>Finall Address: ( "+ad.blockOffset+", "+ad.offset+" )");
									System.out.println("outputtable.RowNum = " + output_table.RowNum);
									
									deleteflag[ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset] = false;
									last = ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset;
								}
								if (last != -1)
									deleteflag[last] = true;
								break;
							case ">=":
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
									Record1 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(string) to byte[]
									byte[] temp_b = new byte[temp_attri.ScaleByte];
									for (int i = 0; i < temp_b.length; i++) {
										temp_b[i] = 127;
									}
									Record2 = temp_b;//????(a bit string) to byte[]
								}
								System.out.println("<=_<:Record1:"+Arrays.toString(Record1)+", Record2"+Arrays.toString(Record2));
								
								ArrayList<address> add_4 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
								
								for (int i = 0; i < output_table.RowNum; i++) {
									deleteflag[i] = true;
								}
								for( address ad : add_4 )
								{
									System.out.println("==>add_4");
									System.out.println("===>Finall Address: ( "+ad.blockOffset+", "+ad.offset+" )");
			
									System.out.println("deleteflag = "+(ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset));
									deleteflag[ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset] = false;
								
								}
								
								break;
							case "<=":
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
									byte[] temp_b = new byte[temp_attri.ScaleByte];
									for (int i = 0; i < temp_b.length; i++) {
										temp_b[i] = 1;
									}
									Record1 = temp_b;
									//Record1 = Buffer.writeString("\1",temp_attri.ScaleByte);//\1 to byte[]
									Record2 = Buffer.writeString(rvar,temp_attri.ScaleByte);//rvar(float) to byte[]
								}
								System.out.println(">=_>:Record1:"+Record1+", Record2"+Record2);
								
								ArrayList<address> add_5 = IndexManager.searchrecord(output_table, temp_index, Record1, Record2);
								for (int i = 0; i < output_table.RowNum; i++) {
									deleteflag[i] = true;
								}
								for( address ad : add_5 )
								{
									System.out.println("add_5");
									System.out.println("===>Finall Address: ( "+ad.blockOffset+", "+ad.offset+" )");
									
									deleteflag[ad.blockOffset*(Buffer.Maxbyte/output_table.RecordLength)+ad.offset] = false;
								
								}
								
								break;
							}
						}
						//without index
						//对于没有index的情况
						//wherelist.lvars.size()次筛选，每次筛选都要遍历所有Attri，以及Attri中的item
						//一个函数确定是否符合wherelist规定，如果为否直接删除
						else
						{				
							Attribute temp_attri = output_table.Attributes.get(Index_Attri);
							Record temp_record = output_table.Records.get(Index_Attri); 
							int SubLengthTmp = 0;
							//逐条记录查找
							for( int Index_item = 0; Index_item < output_table.RowNum; Index_item++ )
							{
								System.out.println("Check Index:"+Index_item+". Type:"+temp_record.type);
								switch(sign)
								{
								case "=": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) != Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) != Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && (!temp_record.Str.get(Index_item).equals(rvar)) ) 
											)
											{
												deleteflag[Index_item] = true;
												System.out.println("Dropped At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case "<>": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) == Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) == Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).equals(rvar)) )
											{
												deleteflag[Index_item] = true;
												System.out.println("Dropped At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case "<":
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) >= Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) >= Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>=0) )
											{
												deleteflag[Index_item] = true;
												System.out.println("Dropped Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case ">":
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) <= Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) <= Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)<=0) )
											{
												deleteflag[Index_item] = true;
												System.out.println("Dropped At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								case "<=": 
									if( temp_attri.AttributeName.equals(lvar) ){
										if( (temp_record.type==0 && temp_record.Int.get(Index_item) > Integer.parseInt(rvar) ) ||
											(temp_record.type==1 && temp_record.Dou.get(Index_item) > Double.parseDouble(rvar) ) ||
											(temp_record.type==2 && temp_record.Str.get(Index_item).compareTo(rvar)>0) )
											{
												deleteflag[Index_item] = true;
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
												deleteflag[Index_item] = true;
												System.out.println("Dropped At Index:"+Index_item+". Type:"+temp_record.type);
											}
									}
									break;
								}//switch
							}//for ( index_item < Attri.Length )
							
}
//							output_table = emptytable;
//							output_table.Attributes.set(Index_Attri, temp_attri);
//							output_table.Records.set(Index_Attri, temp_record);
						}//else(HasIndex == false) 这个Attribute没有index的情况
					}//for ( index_attri < table.AttriNum ) 每一个条件的所有Attribute都判断（跳）完之后
					
				}//for ( vars ) 所有条件都判断完之后
				
			for( int index_attri = 0 ; index_attri < output_table.AttriNum; index_attri++)
			{
				emptytable.Attributes.add(output_table.Attributes.get(index_attri));
				emptytable.AttriNum++;
			}
			Record[] Rec = new Record[output_table.AttriNum];
			for (int i=0;i<output_table.AttriNum;i++){
				Rec[i]=new Record(output_table.Attributes.get(i).Type);
			}
			for( int index_record = 0; index_record < output_table.RowNum; index_record++ )
			{
				if( deleteflag[index_record] == false )
				{
					for (int i=0;i<output_table.AttriNum;i++){
						int type=output_table.Records.get(i).type;
						switch (type){
							case 0:Rec[i].add(output_table.Records.get(i).Int.get(index_record));break;
							case 1:Rec[i].add(output_table.Records.get(i).Dou.get(index_record));break;
							case 2:Rec[i].add(output_table.Records.get(i).Str.get(index_record));break;
						}
					}
					emptytable.RowNum++;
				}
			}
			for (int i=0;i<output_table.AttriNum;i++){
				emptytable.Records.add(Rec[i]);
			
				
			}//else ( wherelist != null )
			OutputTable(emptytable);
		}//if(表存在）（只要在这个大括号里就都有output_table）
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
						for( int Index_item = 0; Index_item < temp_table.RowNum; Index_item++ )
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
				
				Table emptytable = new Table(TableName);
				for( int index_attri = 0 ; index_attri < temp_table.AttriNum; index_attri++)
				{
					emptytable.BlockNum = temp_table.BlockNum;
					emptytable.RecordLength = temp_table.RecordLength;
					emptytable.Attributes.add(temp_table.Attributes.get(index_attri));
					emptytable.AttriNum++;
				}
				Record[] Rec = new Record[temp_table.AttriNum];
				for (int i=0;i<temp_table.AttriNum;i++){
					Rec[i]=new Record(temp_table.Attributes.get(i).Type);
				}
				for( int index_record = 0; index_record < temp_table.RowNum; index_record++ )
				{
					if( deleteflag[index_record] == false )
					{
						for (int i=0;i<temp_table.AttriNum;i++){
							int type=temp_table.Records.get(i).type;
							switch (type){
								case 0:Rec[i].add(temp_table.Records.get(i).Int.get(index_record));break;
								case 1:Rec[i].add(temp_table.Records.get(i).Dou.get(index_record));break;
								case 2:Rec[i].add(temp_table.Records.get(i).Str.get(index_record));break;
							}
						}
						emptytable.RowNum++;
					}
				}
				for (int i=0;i<temp_table.AttriNum;i++){
					emptytable.Records.add(Rec[i]);
				}
//				new_table.AttriNum = temp_table.AttriNum;
//				for( int i = 0; i < temp_table.AttriNum; i++ )
//				{
//					Attribute new_Attri=temp_table.Attributes.get(i);
//					System.out.println("kmtest"+new_Attri.AttributeName);
//					new_table.Attributes.add(new_Attri);
//					System.out.println("=>"+new_table.Attributes.get(i).AttributeName);
//
//				}
//				for( int i = 0; i < temp_table.Records.size(); i++ )
//				{
//					if( deleteflag[i] == false )
//					{
//						new_table.Records.add(temp_table.Records.get(i));
//						new_table.RowNum++;
//					}
//				}
				
//				int MaxRecsPerBlock = 64 / temp_table.RecordLength;
//				temp_table.BlockNum = (int) Math.ceil( (double)temp_table.RowNum / (double) MaxRecsPerBlock);
				System.out.println("RowNum:"+emptytable.RowNum);
				database.Tables.set(Index_Table, emptytable);
				RecordManager1.GenerateRecordFile(emptytable);
//				System.out.println(new_table);
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
			database.Tables.remove(Index_Table);
		}
		else
		{
			Exception.DropError();
		}
		DropIndex( TableName );
		return true;
	}
	
	public static boolean DropIndex( String IndexName )
	{
		/*int Index_Table = -1;
		int Index_Attri = -1;
		for ( int i = -1; i < database.Tables.size(); i++ )
		{
			if( database.Tables.get(i).HasIndex > 0 )
			{
				for( int j = -1; j < database.Tables.get(i).Attributes.size(); j++ )
				{
					if( database.Tables.get(i).Attributes.get(j).HasIndex )
					{
						Index_Table = i;
						Index_Attri = j;
					}
				}
			}
			else
				continue;
		}
		
		if( Index_Table == -1 || Index_Attri == -1 )
		{
			System.err.println("[ERROR] No such index!");
			return false;
		}
		else
		{*/
			IndexManager.dropindex(IndexName);
			return true;
		//}
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
			System.out.println("[ERROR] No such Table!");
			return false;
		}
//		System.out.println("Index_Table:"+Index_Table);
		
		int Index_ValueNo = 0;
		System.out.println("#######Get RecLen="+database.Tables.get(Index_Table).RecordLength);
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
					
					if( database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).IfPrimer || database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).IfUnique )
					{
						for( int i : database.Tables.get(Index_Table).Records.get(Index_ValueNo).Int)
						{
							if( i == temp_int )
							{
								System.out.println("[ERROR] The same value has already in this unique attribute!");
								return false;
							}
						}
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
					
					if( database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).IfPrimer || database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).IfUnique )
					{
						for( Double i : database.Tables.get(Index_Table).Records.get(Index_ValueNo).Dou)
						{
							if( i == temp_double )
							{
								System.out.println("[ERROR] The same value has already in this unique attribute!");
								return false;
							}
						}
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
						
						if( database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).IfPrimer || database.Tables.get(Index_Table).Attributes.get(Index_ValueNo).IfUnique )
						{
							for( String i : database.Tables.get(Index_Table).Records.get(Index_ValueNo).Str)
							{
								if( i.equals(Values[Index_ValueNo]) )
								{
									System.out.println("[ERROR] The same value has already in this unique attribute!");
									return false;
								}
							}
						}
						
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
		
		System.out.println("RecordLength:"+temp_table.RecordLength);
		int MaxRecsPerBlock = 64 / temp_table.RecordLength;
		temp_table.BlockNum = (int) Math.ceil( (double)temp_table.RowNum / (double) MaxRecsPerBlock);

		RecordManager1.GenerateRecordFile(temp_table);
		
//		RecordManager1.ReadFile(temp_table.TableName);
		
		return true;
	}
	
}
