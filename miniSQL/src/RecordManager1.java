package miniSQL;

import java.io.*;

public class RecordManager1 {
	static String FileName;
	static void GenerateRecordFile(Table t) throws IOException{
		FileName=t.TableName+".rec";
		File f=new File (FileName);
		
		
		GeneFile(f);
//		System.out.println("*");
//		System.out.println(1);
		String Sout;
		WriteFile(t,f);
		
	}
	
	static void GeneFile(File f) throws IOException{
		if (!f.exists()){
			f.createNewFile();
		}
	}
	
	static void WriteFile(Table t,File f) throws IOException{
		FileWriter fout=new FileWriter(f);
		int PrintLength=Database.MaxBlock;
//		System.out.println("Ready to Wtite File. Loop time: "+t.RowNum);
		for (int i=0;i<t.RowNum;i++){//RecordNumber
			String str="";

			//int i_int=0,i_float=0,i_str=0;
//			System.out.println("AttriNum="+t.AttriNum+". (RecordManager1)");
			for (int j=0;j<t.AttriNum;j++){
				Record Rec=t.Records.get(j);
				if (Rec.type==0) {
//					System.out.println("j: "+j+". i: "+i+". (RecordManager1)");
//					System.out.println("Record Int Has: "+ Rec.Int.size()+". (RecordManager1)");
					str=str+writeInt(Rec.Int.get(i));
//					System.out.println("Write Int OK!");
					System.out.println("Str At0: "+str);

				}
				else if (Rec.type==1) {
					System.out.println("Addit: "+t.Attributes.get(j).Addit);
					str=str+writeDou(Rec.Dou.get(i),t.Attributes.get(j).Addit);
					System.out.println("Str At1: "+str);

				}
				else if (Rec.type==2) {
//					System.out.println("j: "+j+". i: "+i+". (RecordManager1)");
//					System.out.println("Record Str Has: "+ Rec.Str.size()+". (RecordManager1)");
					str=str+writeString(Rec.Str.get(i), t.Attributes.get(j).Scale);
//					System.out.println("Write Str OK!");
					System.out.println("Str At2: "+str);

				}
			}
//			System.out.println("Str At0: "+str);
			
			fout.write(str);
			PrintLength=PrintLength-t.RecordLength;
			if (PrintLength==0){
				PrintLength=Database.MaxBlock;
			}
			if (PrintLength<t.RecordLength) {
				for(int ti=0;ti<PrintLength;ti++){
					fout.write("*");
					
				}
				PrintLength=Database.MaxBlock;
			}
//			System.out.println("Str At1: "+str);
//			现在不需要补全了
			
			System.out.println("Str Final: "+str);

//			System.out.println("str="+str);
		}
		fout.close();
	}
	//System.out.println(str);
	
	
	
	
	public static int readInt(byte[] record) {
		int sum = 0;

		for (int i = 1; i < 11; i++) {
			sum = sum * 10 + record[i]-'0';
		}
		
		if (record[0] > 0) 
	    return sum;
		else return -sum;
	}
	
	public static String readString(byte[] record) {
		String ans="";
		for (int i = 0; i < record.length; i++) {
//			if ()
			ans = ans + (char)record[i];
		}
		return ans;
	}
	
	public static String writeInt(int record) {
		String ans = "";
		int flag = 1;
		if (record < 0) {
			ans = ans + "0";
			record = -record;
		}
		else ans = ans + "1";
		
		int[] num = new int[11];
		for (int i = 1; i < 11; i++) {
			System.out.println(record % 10);
			num [11-i] = record % 10;
			record = record / 10;
		}
		for (int i = 1; i < 11; i++) {
			ans = ans + (char)(num[i] + 48);
		}
		return ans;
	}
	
	public static String writeDou(Double record, int addit)
	{
		//无论输入如何，小数点位数一定不会大于addit
		//所以乘以10^addit一定可以转化为整数
		int doutoint = (int) (record*Math.pow(10,addit));
		String ans = writeInt(doutoint);
		return ans;
	}
	
	public static String writeString(String record, int maxlength) {
//		System.out.println("==Record:"+record);
//		System.out.println("maxlength:"+maxlength+", r.length:"+record.length());
//		System.out.println("==Record.length:"+record.length());
//		System.out.println("==Add:"+(maxlength-record.length()));
		String prestr = "";
		for( int i = 0; i < maxlength-record.length(); i++ )
		{
//			System.out.print("*");
//			System.out.println("");
			prestr += "\1";
		}
//		System.out.println("==Prestrlength:"+prestr.length());
		record = prestr + record;
		
//		System.out.println("==>Record:");
//		for( int i = 0; i < record.length(); i++ )
//		{
//			System.out.print(record.charAt(i));
//		}
//		System.out.println("");
//		System.out.println("==>Recordlength:"+record.length());
//		System.out.println("==>Record:"+record);
		return record;
	}
	
//	public void clear(int start, int length) {
//		for (int i = start; i < start + length; i++) {
//			block[i] = '*';
//		}
//	}
	
	public static Table ReadFile(String tablename) throws IOException

	{
		Table temp_table = new Table(tablename);
		String str = "";
		
		String RFileName = tablename + ".rec";
		FileInputStream Rfile = new FileInputStream(RFileName);
		InputStreamReader Rfin = new InputStreamReader(Rfile);
		BufferedReader Rbin = new BufferedReader(Rfin);
		
		temp_table = CatalogManager.Read(tablename);
		
		//		Counter = 0;
		//鐜板湪Rec鏂囦欢鍙湁涓�琛�
		str = Rbin.readLine();
		//gai
		int AttriNum = temp_table.AttriNum;
		int RowNum = 0;
		int ReadLength=Database.MaxBlock;
		Record[] Rec=new Record[AttriNum];
		for (int i=0;i<AttriNum;i++){
			int type=temp_table.Attributes.get(i).Type;
			Rec[i]=new Record(type);
		}
		//gai_end
		if( str.isEmpty() )
		{
			System.err.println("Empty");
			Table table = new Table(tablename);
			for ( int i = 0; i < temp_table.AttriNum; i++ )
			{
				Record r = new Record(temp_table.Attributes.get(i).Type);
				table.Records.add(r);
			}
			return table;
			
		}
		while( str.length() > 0  )
		{
			RowNum++;

			
			if( str == null )
			{
				System.out.println("[Warning] Table '"+tablename+"': Record is Empty!");
					return null;		
			}
			System.out.println("AttriNum:"+temp_table.AttriNum);
			
			
			for( int i = 0; i < AttriNum; i++ )
			{
				
				switch(temp_table.Attributes.get(i).Type)
				{
				case 0:
					String temp_part_int = str.substring(0,11);
//					System.out.println("temp_part_int:"+temp_part_int);
					String positiveflag_int = temp_part_int.substring(0,1);
					String last_int = temp_part_int.substring(1, 11);
//					System.out.println("last_int:"+last_int);
					int temp_int = Integer.parseInt(last_int);
					if( positiveflag_int.equals("0") )
					{
						temp_int = - temp_int;
					}
					str = str.substring(11);
					
					System.out.println("->"+temp_int);
					System.out.println("-->"+str);
					//杩欓噷闇�瑕佸啓鍏�
					//Record temp_record_int = new Record(0);
					//temp_table.Records.add(temp_record_int);
					//temp_record_int.Int.add(temp_int);
					//temp_table.Records.set(i, temp_record_int);
					Rec[i].add(temp_int);
					break;
				case 1:
					String temp_part_dou = str.substring(0,11);
					String positiveflag_dou = temp_part_dou.substring(0, 1);
					String last_dou = temp_part_dou.substring(1, 11);
					double temp_dou = Double.parseDouble(last_dou);
					temp_dou /= 100;
					if( positiveflag_dou.equals("0") )
					{
						temp_dou = - temp_dou;
					}
					str = str.substring(temp_part_dou.length());
					
					System.out.println("->"+temp_dou);
					System.out.println("-->"+str);
					//杩欓噷闇�瑕佸啓鍏�
					//Record temp_record_dou = new Record(1);
					//temp_table.Records.add(temp_record_dou);
					//temp_record_dou.Dou.add(temp_dou);
					//temp_table.Records.set(i, temp_record_dou);
					Rec[i].add(temp_dou);
					break;
				case 2:
					String temp_part_str = str.substring(0, temp_table.Attributes.get(i).ScaleByte);
					int startpos = temp_part_str.lastIndexOf("\1");
					
//					System.out.println("temp_part_str:"+temp_part_str);
//					System.out.println("==temp_part_str:");
//					for( int ii = 0; ii < temp_part_str.length(); ii++)
//					{
//						System.out.print(temp_part_str.charAt(ii));
//					}
//					System.out.println("");
//					System.out.println("==length:"+temp_part_str.length());
//					System.out.println("==startpos\1:"+startpos);
					
					String temp_str = temp_part_str.substring(startpos+1,temp_part_str.length());
					str = str.substring(temp_part_str.length());
					
					System.out.println("==>"+temp_str);
//					System.out.println("==>temp_strlength:"+temp_str);
					System.out.println("-->Last:"+str);
					//杩欓噷闇�瑕佸啓鍏�
					//Record temp_record_str = new Record(2);
					//temp_table.Records.add(temp_record_str);
					//temp_record_str.Str.add(temp_str);
					//temp_table.Records.set(i, temp_record_str);
					Rec[i].add(temp_str);
					break;
				default:
					System.out.println("Unkown Type!");
					break;
				}
			}
			System.out.println("Length1:"+ReadLength);
			ReadLength-=temp_table.RecordLength;
			System.out.println("Length2:"+ReadLength);
			if (ReadLength<temp_table.RecordLength){
				System.out.println("Str======"+str);
				str=str.substring(ReadLength);
				ReadLength=Database.MaxBlock;
				System.out.println("Str======"+str);
			}
			System.out.println("Length3:"+ReadLength);
//			Counter++;
		}
		//gai
		for (int i=0;i<AttriNum;i++){
			temp_table.Records.add(Rec[i]);
		}
		

		//gai end
//		API.database.Tables.set(Index_Table, temp_table);
		temp_table.RowNum = RowNum;
		
		int MaxRecPerBlock = 64 / temp_table.RecordLength;
		temp_table.BlockNum = (int) Math.ceil( (double) temp_table.RowNum / (double) MaxRecPerBlock);
		System.out.println("RowNum"+temp_table.RowNum);
		System.out.println("MacRecPerBolck"+MaxRecPerBlock+", RecordLength:"+temp_table.RecordLength+", BlockNum"+temp_table.BlockNum);
		return temp_table;
	}
	
//
//	boolean ReadFile(Table t, File f) throws IOException{
//		FileInputStream file=new FileInputStream(FileName);
//		InputStreamReader fin = new InputStreamReader(file);
//		BufferedReader bin = new BufferedReader(fin);// ¥”◊÷∑˚ ‰»Î¡˜÷–∂¡»°Œƒº˛÷–µƒƒ⁄»›,∑‚◊∞¡À“ª∏ˆnew InputStreamReaderµƒ∂‘œÛ
//		String str;
//		int i=0;
//		while ((str = bin.readLine()) != null) {
//			int start=0,end=0;
//			Attribute Att=t.table.get(i);//Information that catalog contains
//			Record r=t.Rec.get(i);//The record that read from .rec
//			int type = r.type;
//			int size=Att.length;//NUMBER of element
//			int scale=Att.scale;//length for ONE element
//			if (type==0){
//				r.IntSize=size;
//			}
//			else if (type==1){
//				r.DouSize=size;
//			}
//			else if (type==2){
//				r.StrSize=size;
//			}
//			for (i=0;i<size;i++){
//				if (type==0){
//					end=start+scale;
//					String Ele=str.substring(start,end);
//					Ele.replace("*", "");
//					r.Int.add(Integer.parseInt(Ele));
//					start=end;
//				}
//				else if (type==1){
//					int addit=Att.addit;
//					end=start+scale;
//					String Ele=str.substring(start,end);
//					Ele.replace("*", "");
//					Double x=Double.parseDouble(Ele);
//					for (int temp=0;temp<addit; i++){
//						x=x/10;
//					}
//					r.Dou.add(x);
//					start=end;
//				}
//				else if (type==2){
//					end=str.indexOf("|",start);
//					String substr=str.substring(start,end); 
//					start=end;
//				}
//			}
//			
//			
//			
//		}
//		
//		return true;
//		
//	}
}
