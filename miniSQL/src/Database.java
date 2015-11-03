package miniSQL;

import java.util.ArrayList;

public class Database {
	ArrayList<Table> Tables = new ArrayList<Table>();
	public static int MaxBlock = 32;
	
	public Database(){
		System.out.println("Database Size:"+Tables.size());
	}
	
	
}
