package miniSQL;

import java.util.Scanner;
import java.io.IOException;

public class interpreter {
	private static boolean QUIT_FLAG = false;
	private static String query="";
	private static Exception Excep=new Exception();
	private static DisPreSpace DisSpace=new DisPreSpace();
	private static Opt Option=new Opt();
	
	public static void main(String[] args) throws IOException{
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
		while ((str=s.nextLine()).length()!=0)
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
				else
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
			Excep.IsEmpty();
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
        	Option.Exec(rest);
        else
        	Excep.TypeError();
	}
}
