package miniSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class interpreter {
	private static boolean QUIT_FLAG = false;
	private static boolean FILE_FLAG = false;
	private static String filename = "";
	private static String query="";
//	private static Exception Excep=new Exception();
	private static DisPreSpace DisSpace=new DisPreSpace();
	private static Opt Option=new Opt();
	
	public static void main(String[] args) throws IOException{
//		List<File> ll_c = getFiles(new File(".."),"cat");
//		List<File> ll_r = getFiles(new File(".."),"rec");
//        for (File ff_c : ll_c) {
//        	Database.RebuildCat(ff_c);
//        }
//        for (File ff_r : ll_r) {
//        	Database.RebuildRec(ff_r);
//        }
		List<File> ll = getFiles(new File(".."),"cat");
		for (File ff : ll) {
//        	Database.Rebuild(ff);
        }
			
		System.out.println("Welcome to the MySQL monitor. Commands end with ;.");
		System.out.println("Server version: 1.0");
		System.out.println("Copyright kly. All rights reserved.");
//		try{
			inputQuery();
//		}
//		catch(IOException e){
//			;
//		}
	}
	
	public static void inputQuery() throws IOException{
		String str;
		//welcome();
		System.out.print("MiniSQL>>");
		Scanner s = new Scanner(System.in);
		while ((str=s.nextLine()) != null )
		{
			int pos_of_semicolon;
			pos_of_semicolon=str.indexOf(";");
			if (pos_of_semicolon==-1)
				query=query+str+" ";
				//query not finished
			else {
				String substr = str.substring(0,pos_of_semicolon);
				query=query+substr;
				//query finished.3
//				System.out.println(query);
				OptionDefine();
				
				query="";
				if( QUIT_FLAG )
				{
					System.out.println("Bye!");
					return;
				}
				else if( FILE_FLAG )
				{
					System.out.println("filename:"+filename);
					FileInputStream file = new FileInputStream(filename);
					InputStreamReader fin = new InputStreamReader(file);
					BufferedReader bin = new BufferedReader(fin);
					
					while( (str = bin.readLine()) != null )
					{
						pos_of_semicolon=str.indexOf(";");
						if (pos_of_semicolon==-1)
							query=query+str+" ";
							//query not finished
						else {
							substr = str.substring(0,pos_of_semicolon);
							query=query+substr;
							//query finished.3
//							System.out.println(query);
							OptionDefine();
							
							query="";
							if( QUIT_FLAG )
							{
								System.out.println("Bye!");
								continue;
							}
						}
					}
					System.out.println("Out of file!");
					filename = "";
					FILE_FLAG = false;
				}
				
				System.out.print("MiniSQL>>");
			}
			//System.out.println(query);
			
		}
		
	}
	
	private static void OptionDefine() throws IOException{
		String temp_query;
		temp_query=query;
//		System.out.println(temp_query);
		temp_query=DisSpace.dislodge_space(temp_query);
		if (temp_query.isEmpty()) {
			Exception.IsEmpty();
			return;
		}
		int split_pos=temp_query.indexOf(" ");
		String option,rest = "";
		if (split_pos==-1){
			option = temp_query;
		}
		else{
			option = temp_query.substring(0,split_pos);
			rest = temp_query.substring(split_pos+1);
		}
//		System.out.println(option+"/"+rest);

		allocate(option,rest);
	}
	
	private static void allocate(String option, String rest) throws IOException{
		option=DisSpace.dislodge_space(option);
		rest=DisSpace.dislodge_space(rest);
		rest=rest.toLowerCase();
		if (option.equalsIgnoreCase("create")) 
			Option.Create(rest);
		else if (option.equalsIgnoreCase("select")) 
			Option.Select(rest);
		else if (option.equalsIgnoreCase("update")) 
			Option.Update(rest);
		else if (option.equalsIgnoreCase("delete")) 
			Option.Delete(rest);
		else if (option.equalsIgnoreCase("drop"))
			Option.Drop(rest);
		else if (option.equalsIgnoreCase("insert")) 
			Option.Insert(rest);
        else if (option.equalsIgnoreCase("quit"))
        	QUIT_FLAG = true;
        else if (option.equalsIgnoreCase("execfile"))
        {
        	filename = rest;
        	filename = filename.replace(" ", "");
        	filename = filename.replace(";", "");
        	FILE_FLAG = true;
        }
        else
        	Exception.TypeError();
	}
	
	 public static List<File> getFiles(File fileDir, String fileType) {
	        List<File> lfile = new ArrayList<File>();
	        File[] fs = fileDir.listFiles();
	        for (File f : fs) {
	            if (f.isFile()) {
	                if (fileType
	                        .equals(f.getName().substring(
	                                f.getName().lastIndexOf(".") + 1,
	                                f.getName().length())))
	                    lfile.add(f);
	            } else {
	                List<File> ftemps = getFiles(f,fileType);
	                lfile.addAll(ftemps);
	            }
	        }
	        return lfile;
	    }

}
