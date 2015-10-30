package miniSQL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class RecordManager {
	String FileName;
	
	void GenerateRecordFile(Table t) throws IOException{
		FileName=t.TableName+".rec";
		File f=new File (FileName);
		GeneFile(f);
		for (int i=0;i<t.AttriNum;i++){
			WriteFile(t.Records.get(i),t.Attributes.get(i),f);
		}
		
	}
	
	void GeneFile(File f) throws IOException{
		if (!f.exists()){
			f.createNewFile();
		}
	}
	
	boolean WriteFile(Record Rec,Attribute Att,File f) throws IOException{
		FileWriter fout=new FileWriter(f);
		if (Rec.type==1){
			for (int i=0;i<Rec.IntSize;i++){
				int x=Rec.Int.get(i);
				int scale=Att.Scale;
				String out=Integer.toString(x);
				while (out.length()<scale){
					out="*"+out;
				}
				fout.write(out);
			}
		}
		else if (Rec.type==2){
			for (int i=0;i<Rec.DouSize;i++){
				double x=Rec.Dou.get(i);
				int scale=Att.Scale;
				for (int j=0;j<Att.Addit;j++){
					x=x*10;
				}
				String out=Double.toString(x);
				int point=out.indexOf(".");
				out=out.substring(0,point);
				while (out.length()<scale){
					out="*"+out;
				}
				fout.write(out);
			}
		}
		else if (Rec.type==3){
			for (int i=0;i<Rec.StrSize;i++){
				String out=Rec.Str.get(i)+"|";
				fout.write(out);
			}
		}
		fout.write("\n");
		return true;
	}
}
