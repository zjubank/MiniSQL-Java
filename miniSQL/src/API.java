package miniSQL;

import java.io.IOException;
import java.util.*;

public class API {
	public static Table table = new Table();
	
	public static void create_database()
	{
		System.out.println("|**** Create Database ****|");
	}
	
	public static void create_table(boolean Uni, boolean Pri, String ty, String name, int scale, int add) throws IOException
	{
		System.out.println("|**** Create Table ****|");
		System.out.println("|**** API: Uni:"+Uni+", Pri:"+Pri+", Type:"+ty+", Name:"+name+", Sacle:"+scale+", Add:"+add );
		boolean CheckAdd = table.Add(Uni, Pri, ty, name, scale, add);
		System.out.println("|**** Check Add:"+CheckAdd);
		boolean CheckPrint = table.Print();
		System.out.println("|**** Print:" + CheckPrint);
	}
	
	public static void create_index()
	{
		System.out.println("|**** Create Index ****|");
	}
	
	
}
