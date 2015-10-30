package miniSQL;

import java.util.ArrayList;

public class Record {
	String TableName="";
	ArrayList<Integer>	Int=new ArrayList<Integer>();
	ArrayList<Double>	Dou=new ArrayList<Double>();
	ArrayList<String> 	Str=new ArrayList<String>();
	int type=0;//0:int 1:float;2:string
	int IntSize=0,DouSize=0,StrSize=0;
	
	boolean create(String s){
		if (s.equalsIgnoreCase("INT"))
			type=0;
		else if (s.equalsIgnoreCase("FLOAT"))
			type=1;
		else if (s.equalsIgnoreCase("STRING"))
			type=2;
		else return false;
		return true;
	}
	
	boolean typejudge(int t){
		if (t==type) return false;
		return true;
	}
	
	boolean add(int x){
		if (typejudge(0)) return false; 
		Int.add(x);
		return true;
	}
	
	boolean add(double f){
		if (typejudge(1)) return false; 
		Dou.add(f);
		return true;
	}
	boolean add(String s){
		if (typejudge(2)) return false; 
		Str.add(s);
		return true;
	}
	
	boolean update(int index, int x){
		if (typejudge(0)) return false; 
		if (index>=IntSize){
			return false;
		}
		Int.set(index, x);
		return true;
	}
	boolean update(int index, double f){
		if (typejudge(1)) return false; 
		if (index>=DouSize){
			return false;
		}
		Dou.set(index, f);
		return true;
	}
	boolean update(int index, String s){
		if (typejudge(2)) return false; 
		if (index>=StrSize){
			return false;
		}
		Str.set(index, s);
		return true;
	}
	
	boolean drop(int index, int x){
		if (typejudge(0)) return false; 
		if (index>=IntSize){
			return false;
		}
		Int.remove(index);
		return true;
	}
	
	boolean drop(int index, double f){
		if (typejudge(1)) return false; 
		if (index>=DouSize){
			return false;
		}
		Dou.remove(index);
		return true;
	}
	
	boolean drop(int index, String s){
		if (typejudge(2)) return false; 
		if (index>=StrSize){
			return false;
		}
		Str.remove(index);
		return true;
	}
	
}
