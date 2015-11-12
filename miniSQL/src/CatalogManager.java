package miniSQL;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class CatalogManager {
	
	public static boolean Print( Table t ) throws IOException{
		String FileName=t.TableName+".cat";
//		System.out.println("|**** Inserc Filepath:"+FileName);
		FileWriter fout=new FileWriter(FileName);
		int x=0;
		for (int i=0;i<t.AttriNum;i++){
			x += t.Attributes.get(i).Scale;
			
		}
		x = (int) Math.ceil((double)x/Database.MaxBlock);
		t.BlockNum = x;
		fout.write(t.AttriNum+"\n");
		for (int i=0;i<t.AttriNum;i++){
			fout.write(t.Attributes.get(i).AttributeName+"\n");
			fout.write(t.Attributes.get(i).Type+"\n");
			fout.write(t.Attributes.get(i).Length+"\n");
			fout.write(t.Attributes.get(i).Scale+"\n");
			fout.write(t.Attributes.get(i).Addit+"\n");
			fout.write(t.Attributes.get(i).IfUnique+"\n");
			fout.write(t.Attributes.get(i).IfPrimer+"\n");
		}
		fout.close();
		return true;//success
	}
	
	public static Table Read( String tablename ) throws IOException{
		Table temp_table = new Table(tablename);
		
		String CFileName = tablename + ".cat";
		FileInputStream Cfile = new FileInputStream(CFileName);
		InputStreamReader Cfin = new InputStreamReader(Cfile);
		BufferedReader Cbin = new BufferedReader(Cfin);
		String str = "";
		
		
		int Counter = -1;
		while( (str = Cbin.readLine()) != null )
		{
//			System.out.println("CReadFile: "+str);
			if( Counter == -1 )
			{
				temp_table.AttriNum = Integer.parseInt(str);
//				System.out.println("AttriNum:"+temp_table.AttriNum);
				Counter++;
				continue;
			}
			
//			str = Cbin.readLine();
			String AttriName = str;
//			System.out.println("AttriName:"+AttriName);
			
			str = Cbin.readLine();
			int Type = Integer.parseInt(str);
//			System.out.println("Type:"+Type);
			
			str = Cbin.readLine();
			int Length = Integer.parseInt(str);
//			System.out.println("Length:"+Length);
			
			str = Cbin.readLine();
			int Scale = Integer.parseInt(str);
//			System.out.println("Scale:"+Scale);
			
			str = Cbin.readLine();
			int Addit = Integer.parseInt(str);
//			System.out.println("Addit:"+Addit);
			
			str = Cbin.readLine();
			boolean IfUnique = Boolean.parseBoolean(str);
//			System.out.println("IfUnique:"+IfUnique);
			
			str = Cbin.readLine();
			boolean IfPrimer = Boolean.parseBoolean(str);
//			System.out.println("IfPrimer:"+IfPrimer);
			
			System.out.println("Tablename:"+tablename+", Name:"+AttriName+", Type:"+Type+", Length:"+Length+", Scale:"+Scale+", Addit:"+Addit+", IfUni:"+IfUnique+", IfPri:"+IfPrimer);

			Attribute temp_attri = new Attribute(AttriName, Type, Length, Scale, Addit, IfUnique, IfPrimer);
			switch(Type)
			{
			case 0: case 1: temp_attri.ScaleByte = 11; break;
			case 2: temp_attri.ScaleByte = Scale; break;
			default: break;
			}
			temp_table.RecordLength += temp_attri.ScaleByte;
			temp_table.Attributes.add(temp_attri);
//			temp_table.AttriNum++;
			Counter++;
		}
		return temp_table;
	}
	
	public static Index readindex(String tablename, String indexname) {
		FileInputStream fos;

		try {
			fos = new FileInputStream(indexname+".index.cat");
			byte[] b = new byte[11];
			fos.read(b);
			int attr = Buffer.readInt(b);
			fos.read(b);
			int size = Buffer.readInt(b);
			fos.read(b);
			int blocknum = Buffer.readInt(b);
			fos.read(b);
			int rootnum = Buffer.readInt(b);
			System.out.println("attr = "+attr + "size = "+size +"blocknum = "+blocknum+"rootnum = "+rootnum);

			Index indexInfo = new Index(indexname, tablename, attr,size,blocknum,rootnum);
			return indexInfo;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void writeIndex(Index indexInfo) {
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(indexInfo.IndexName+".index.cat");
			byte[] b = new byte[11];
			b = Buffer.writeInt(indexInfo.Attri);
			fos.write(b);
			b = Buffer.writeInt(indexInfo.Size);
			fos.write(b);
			b = Buffer.writeInt(indexInfo.BlockNum);
			fos.write(b);
			b = Buffer.writeInt(indexInfo.RootNum);
			fos.write(b);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
