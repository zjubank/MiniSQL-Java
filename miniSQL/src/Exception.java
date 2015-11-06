package miniSQL;

public class Exception {
	public static void IsEmpty(){
		System.out.println("The Option is Empty!");
	}
	public static void CreateType(){
		System.out.println("Undefined Type in CREATE!");
	}
	public static void CreateError(){
		System.out.println("Illegal Expression in CREATE!");
	}
	public static void NameProblem(String type,String Prob){
		System.out.println("Illegal Name Defined:"+type+":"+Prob);
	}
	public static void SelectError(){
		System.out.println("Illegal Expression in SELECT!");
	}
	public static void DeleteError(){
		System.out.println("Illegal Expression in DELETE");
	}
	public static void DropError(){
		System.out.println("Illegal Expression in DROP");
	}
	public static void InsertError(){
		System.out.println("Illegal Expression in INSERT");
	}
	public static void InsertErrorOutOfBound(){
		System.out.println("The scale of insert is out of bound. Only reseave the first 32 values.");
	}
	public static void TypeError(){
        System.out.println("Illegal type expression.");
    }
	public static  void TableIndexError()
    {
    	System.out.println("No such table!");
    }
	public static void AttriIndexError()
    {
		System.out.println("No such Attribute!");
    }
}
