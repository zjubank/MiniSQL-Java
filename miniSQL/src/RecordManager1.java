package miniSQL;

import java.io.*;

public class RecordManager1 {
	static String FileName;
	static void GenerateRecordFile(Table t) throws IOException{
		FileName=t.TableName+".rec";
		File f=new File (FileName);
		
		
		GeneFile(f);
		System.out.println("*");
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
					str=str+writeInt(Rec.Int.get(0));
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
			while (str.length()>Database.MaxBlock){
				String substr=str.substring(0,Database.MaxBlock);
				fout.write(substr);
				str=str.substring(Database.MaxBlock);
			}
//			System.out.println("Str At1: "+str);
			while (str.length()<Database.MaxBlock&&str.length()!=0){
				str=str+"*";
			}
			System.out.println("Str Final: "+str);

//			System.out.println("str="+str);
			fout.write(str);
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
		for( int i = record.length(); i < maxlength; i++)
		{
			record += "'";
		}
		return record;
	}
	
//	public void clear(int start, int length) {
//		for (int i = start; i < start + length; i++) {
//			block[i] = '*';
//		}
//	}
	
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
