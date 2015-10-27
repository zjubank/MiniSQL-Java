package miniSQL;

public class Exception {
	void IsEmpty(){
		System.out.println("The Option is Empty!");
	}
	void CreateType(){
		System.out.println("Undefined Type in CREATE!");
	}
	void CreateError(){
		System.out.println("Illegal Expression in CREATE!");
	}
	void NameProblem(String type,String Prob){
		System.out.println("Illegal Name Defined:"+type+":"+Prob);
	}
	void SelectError(){
		System.out.println("Illegal Expression in SELECT!");
	}
	void DeleteError(){
		System.out.println("Illegal Expression in DELETE");
	}
	void DropError(){
		System.out.println("Illegal Expression in DROP");
	}
	void InsertError(){
		System.out.println("Illegal Expression in INSERT");
	}
	void InsertErrorOutOfBound(){
		System.out.println("The scale of insert is out of bound. Only reseave the first 32 values.");
	}
    void TypeError(){
        System.out.println("Illegal type expression.");
    }
}
