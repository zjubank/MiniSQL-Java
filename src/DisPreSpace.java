package miniSQL;

public class DisPreSpace {
	public String dislodge_space(String s){
		if (s=="")return s;
		int i=0,j=s.length()-1;
		
		while (s.charAt(i)==' '||s.charAt(i)=='	') {i++;}
		while (s.charAt(j)==' '||s.charAt(i)=='	') {j--;}
		//System.out.println(i);
		s=s.substring(i,j+1);
		return s;
	}
}
