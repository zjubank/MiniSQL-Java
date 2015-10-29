package miniSQL;

import java.io.IOException;

public class Opt {
	public static API api = new API();
	private DisPreSpace DisSpace=new DisPreSpace();
	private Exception Excep= new Exception();
	
	
	
	
	//===============================create===========================//
	void Create(String s) throws IOException{
		String str=s;
		String type="";
		
		int split=str.indexOf(" ");
		type= str.substring(0, split);		
		if (type.equalsIgnoreCase("database")){
			String name="";
			name=str.substring(split);
			name=DisSpace.dislodge_space(name);
			if (Check_nameLegal(name))
				
				//=======================Create Database==========================
				System.out.println("Database Create Success");
			else Excep.NameProblem("DATABASE","NULL");
			}
		else if (type.equalsIgnoreCase("table")){
			String rest="";
			String name="";
			String infor="";
			rest=str.substring(split);
			rest=DisSpace.dislodge_space(rest);
			int split_brac1,split_brac2;
			split_brac1=rest.indexOf("(");
			split_brac2=rest.lastIndexOf(")");
			name=rest.substring(0,split_brac1);
			name=DisSpace.dislodge_space(name);
			infor=rest.substring(split_brac1+1,split_brac2);
			System.out.println("****name**** : " + name);
			if (!name.isEmpty())
			{
				if (Check_nameLegal(name)) Excep.NameProblem("TABLE","ILLEGAL");
			}
			else
			{
				Excep.NameProblem("TABLE","NULL");
			}
			if (!infor.isEmpty())
			{
				Create_Table_Infor(infor);
			}
			System.out.println("Table Created");
		}
		
		else if (type.equalsIgnoreCase("index")){
			String rest="";
			String index_name="";
			String after_on="";
			rest=str.substring(split);
			rest=DisSpace.dislodge_space(rest);
			int on_pos=rest.indexOf("on");
			if (on_pos==-1){
				Excep.CreateError();
				return;
			}
			index_name=rest.substring(0,on_pos);
			after_on=rest.substring(on_pos+2);
			index_name=DisSpace.dislodge_space(index_name);
			after_on=DisSpace.dislodge_space(after_on);
			
			String table_name="",sname="",over_rest="";
			
			int brac1,brac2;
			brac1=after_on.indexOf("(");
			brac2=after_on.lastIndexOf(")");
			if (brac1==-1||brac2==-1){
				Excep.CreateError();
				return;
			}
			System.out.println(brac1+"/"+brac2);
			table_name=after_on.substring(0,brac1);
			sname=after_on.substring(brac1+1,brac2);
			over_rest=after_on.substring(brac2+1);
			System.out.println();
			table_name=DisSpace.dislodge_space(table_name);
			sname=DisSpace.dislodge_space(sname);
			if (!over_rest.isEmpty())
				over_rest=DisSpace.dislodge_space(over_rest);
			if (!over_rest.isEmpty()){
				Excep.CreateError();
				return;
			}
			//========================Create Index======================
			System.out.println(index_name);
			System.out.println(table_name+"    "+sname);
		}
		else Excep.CreateType();
	}
	

	
	void Create_Table_Infor(String s) throws IOException{
		//Create_Table();
		String str=s, substr="";
		str=DisSpace.dislodge_space(s);
		
		
		System.out.println("mark");	
		Insert_Table_Element(str);
		//System.out.println(s);
	}

	//==================================select================================//
	
	
	void Select(String s){
		int split,dist,where_pos;
		String str=s, before_from="", after_from="", distinct="";
		String after_where="";
		split=str.indexOf("from");
		if (split==-1) {
			Excep.SelectError();
			return;
		}
		before_from=str.substring(0,split);
		after_from=str.substring(split+4);
		before_from=DisSpace.dislodge_space(before_from);
		after_from=DisSpace.dislodge_space(after_from);
		dist=before_from.indexOf(" ");
		if (dist!=-1){
			distinct=before_from.substring(0,dist);
			if (distinct.equalsIgnoreCase("distinct")) {
				before_from=before_from.substring(dist+1);
			}
			else{
				distinct="";
			}
		}
		String[] names=new String[20];
		names=Split_Name(before_from);
		
		where_pos=after_from.indexOf("where");
		if (where_pos!=-1) {
			
			after_where=after_from.substring(where_pos+5);
			System.out.println("A_W     "+after_where);
			Split_Where_Orders(after_where);
			//split.cond();
		}
		else {
			//select(names,) 
		}
		
		//System.out.println("distinct:"+distinct);
		//for (int i=0;i<names.length;i++)
		//System.out.println("before from:"+names[i]);
		//System.out.println("after from:"+after_from);
		//System.out.println("after where:"+after_where);
	}
	
	

	
	//==============================update====================================//
	
	
	
	
	void Update(String s){
		String str=DisSpace.dislodge_space(s);
		String all_name,rest,all_cond;
		String[] cond=new String[20];
		String[] names=new String[20];
		int set_pos=str.indexOf("set"),where_pos;
		all_name=str.substring(0,set_pos);
		rest=str.substring(set_pos+3);
		where_pos=rest.indexOf("where");
		all_cond=rest.substring(0,where_pos);
		rest=rest.substring(where_pos+5);
		all_name=DisSpace.dislodge_space(all_name);
		all_cond=DisSpace.dislodge_space(all_cond);
		rest=DisSpace.dislodge_space(rest);
		
		
		names=Split_Name(all_name);
		Split_Cond(all_cond);
		Split_Where_Orders(rest);
		
		System.out.println(all_name);
		System.out.println(all_cond);
		System.out.println(rest);
		
	}
	
	
	//=============================delete=================================//
	
	void Delete(String s){
		String str=DisSpace.dislodge_space(s);
		
		int from_space_place=str.indexOf(" ");
		int where_place;
		String from, after_from, table_name,after_where;
		if (from_space_place==-1){
			Excep.DeleteError();
			return;
		}
		from=str.substring(0,from_space_place);
		after_from=str.substring(from_space_place);
		from=DisSpace.dislodge_space(from);
		after_from=DisSpace.dislodge_space(after_from);
		if (!from.equalsIgnoreCase("from")){
			Excep.DeleteError();
			return;
		}
		where_place=after_from.indexOf("where");
		if (where_place!=-1){
			table_name=after_from.substring(0,where_place);
			after_where=after_from.substring(where_place);
			table_name=DisSpace.dislodge_space(table_name);
			after_where=DisSpace.dislodge_space(after_where);
			//operate(table_name);
			System.out.println(table_name);
			Split_Where_Orders(after_where);
		}
		else {
			after_where="";
			table_name=after_from;
			table_name=DisSpace.dislodge_space(table_name);
			System.out.println(table_name);
			//operate(table_name);	
		}
		
		
	}
	
	//==============================Drop==================================//
	
	void Drop(String s){
		String str=DisSpace.dislodge_space(s);
		int type_space=str.indexOf(" ");
		if (type_space==-1){
			Excep.DropError();
			return;
		}
		String type;
		type=str.substring(0,type_space);
		if (type.equals("table")){
			String name;
			name=str.substring(type_space+1);
		}
		else if (type.equals("index")){
			String name;
			name=str.substring(type_space+1);
		}
		else Excep.DropError();
	}
	
	//==============================insert================================//
	
	void Insert(String s){
		String str=DisSpace.dislodge_space(s);
		String if_into;
		int into_space;
		String rest;
		into_space=str.indexOf(" ");
		if_into=str.substring(0,into_space);
		if_into=DisSpace.dislodge_space(if_into);
		rest=str.substring(into_space+1);
		rest=DisSpace.dislodge_space(rest);
		if (!if_into.equals("into")){
			Excep.InsertError();
			return;
		}
		String if_values;
		int values_pos;
		values_pos=rest.indexOf("values");
		if (values_pos==-1){
			Excep.InsertError();
			return;
		}
		String names=rest.substring(0,values_pos);
		String values=rest.substring(values_pos+6);
		names=DisSpace.dislodge_space(names);
		values=DisSpace.dislodge_space(values);
		String[] all_value=new String[32];
		if (values.charAt(0)!='('||values.charAt(values.length()-1)!=')'){
			Excep.InsertError();
			return;
		}
		values=values.substring(1,values.length()-1);
		all_value=split_values(values);
		if (all_value==null){
			Excep.InsertError();
			return;
		}
		//operate(names,all_value)
		System.out.println(names+values);
	}
	
	String[] split_values(String s){
		String[] values=new String[32];
		int brac1,brac2;
		String str=DisSpace.dislodge_space(s);
		String sub_split;
		System.out.println(str);
		int comma;
		int last_comma=0,i=0;
		do{
			comma=str.indexOf(",",last_comma);
			if (comma==-1){
				sub_split=str.substring(last_comma);
			}
			else{
				System.out.println(last_comma+" "+comma);
				sub_split=str.substring(last_comma,comma);
			}
			if (i==31){
				Excep.InsertErrorOutOfBound();
				return values;
			}
			sub_split=DisSpace.dislodge_space(sub_split);
			if (sub_split.charAt(0)!='\''||sub_split.charAt(sub_split.length()-1)!='\''){
				Excep.InsertError();
				return null;
			}
			sub_split=sub_split.substring(1,sub_split.length()-1);
			values[i]=sub_split;
			i++;
			System.out.println(sub_split);
			last_comma=comma+1;
			
		}while (comma!=-1);
		
		return values;
	}
	
	//==============================function==============================//
	void Insert_Table_Element(String s) throws IOException {
		String str=DisSpace.dislodge_space(s);
		int comma,lastcomma=0;
		str=str+",";
		comma=str.indexOf(",");
		System.out.println(comma);
		System.out.println(str);
		do{
			String substr,name,type,para1="",para2="",para="";
			int split_space,split_brac1,split_brac2;
			substr=str.substring(lastcomma,comma);
			//depart one of the input
			substr=DisSpace.dislodge_space(substr);
			
			if (judgement_for_attri(substr)) {
				lastcomma=comma+1;
				comma=str.indexOf(",", lastcomma);
				continue;
			}
			
			split_space=substr.indexOf(" ");
			split_brac1=substr.indexOf("(");
			split_brac2=substr.indexOf(")");
			
			

			name=substr.substring(0,split_space);
			if (split_brac1==-1){
				type=substr.substring(split_space+1);
				para1="";
				para2="";
			}
			else{
				type=substr.substring(split_space+1,split_brac1);
				para=substr.substring(split_brac1+1,split_brac2);
				int subcomma=para.indexOf(",");
				if (subcomma!=-1){
					para1=para.substring(0,subcomma);
					para2=para.substring(subcomma+1);
				}
				else {
					para1=para;
					para2="";
				}
			}
			
			
			if (!name.isEmpty()){
				if (Check_nameLegal(name)) Excep.NameProblem("VAR","ILLEGAL");
			}
			else Excep.NameProblem("VAR","NULL");
			
			System.out.println("name:"+name+"  type:"+type+"  para1:"+para1+"  para2:"+para2);
			lastcomma=comma+1;
			comma=str.indexOf(",", lastcomma);
			
//			API: Add(Uni, Pri, ty, name, scale, add);
			boolean Uni = judgement_for_attri(substr);
			boolean Pri = judgement_for_attri(substr);
			int scale, add;
			if( para1.length() > 0 )
			{
				scale = Integer.parseInt(para1);
			}
			else
			{
				scale = 0;
			}
			if( para2.length() > 0 )
			{
				add = Integer.parseInt(para2);
			}
			else
			{
				add = 0;
			}
			API.create_table(Uni, Pri, type, name, scale, add);
		}while (comma!=-1);
	}
	
	boolean Check_nameLegal(String s){
		if (!(s.charAt(0)>='A'&&s.charAt(0)<='Z')||(s.charAt(0)>='a'&&s.charAt(0)<='z')) 
			return false;
		for(int i=1;i<s.length();i++)
		{
			if (!(s.charAt(i)>='A'&&s.charAt(i)<='Z')||(s.charAt(i)>='a'&&s.charAt(i)<='z'))
				if (!(s.charAt(i)>='0'&&(s.charAt(i)<='9')))
					if (!(s.charAt(i)=='_'))
						return false;
		}
		return true;
	}
	
	
	
	boolean judgement_for_attri(String s){
		String if_pri,if_uni;
		
		if (s.length()<6) return false;
		if_uni=s.substring(0,6);
		System.out.println(if_uni);
		if (if_uni.equals("unique"))
		{
			String rest;
			rest=s.substring(6);
			rest=DisSpace.dislodge_space(rest);
			String sname;
			int brac1,brac2;
			brac1=rest.indexOf("(");
			brac2=rest.lastIndexOf(")");
			if (brac1==-1||brac2==-1){
				Excep.CreateError();
				return false;
			}
			sname=rest.substring(brac1+1,brac2);
			sname=DisSpace.dislodge_space(sname);
			System.out.println(sname);
			return true;
		}
		
		
		//pri_judgement
		
		if (s.length()<7) return false;
		if_pri=s.substring(0,7);
		System.out.println(if_pri);
		if (if_pri.equals("primary"))
		{
			String if_key,rest;
			rest=s.substring(7);
			System.out.println(rest);
			rest=DisSpace.dislodge_space(rest);
			if_key=rest.substring(0,3);
			System.out.println(if_key);
			if (if_key.equals("key")){
				rest=rest.substring(3);
				String sname;
				int brac1,brac2;
				brac1=rest.indexOf("(");
				brac2=rest.lastIndexOf(")");
				if (brac1==-1||brac2==-1){
					Excep.CreateError();
					return false;
				}
				sname=rest.substring(brac1+1,brac2);
				sname=DisSpace.dislodge_space(sname);
				System.out.println(sname);
				//primary_key(sname);
				return true;
			}
		}
		return false;
	}
	
	//=================name of var=====================//
	String[] Split_Name(String s){
		String[] names=new String[20];
		String str=DisSpace.dislodge_space(s);
		System.out.println(str);
		int last_comma=0,comma,i=0;
		do{
			comma=str.indexOf(",",last_comma);
			if (comma!=-1)
				names[i]=str.substring(last_comma,comma);
			else names[i]=str.substring(last_comma);
			names[i]=DisSpace.dislodge_space(names[i]);
			i++;
			last_comma=comma+1;
			
		}while(comma!=-1&&i<20); 
		System.out.println(i);
		return names;
	}
	
//where	
	
	void Split_Where_Orders(String s){
		String str=DisSpace.dislodge_space(s);
		String suborder;
		int comma,last_comma=0;
		do{
			comma=str.indexOf("and",last_comma);
			if (comma!=-1)
				suborder=str.substring(last_comma,comma);
			else suborder=str.substring(last_comma);
			suborder=DisSpace.dislodge_space(suborder);
			Gene_Order(suborder);
			last_comma=comma+3;
		}while(comma!=-1); 
	}
	
	void Gene_Order(String s){
		String str=DisSpace.dislodge_space(s);
		String[] sign_list={">=","<=","<>",">","<","="};
		String lvar,rvar;
		String sign="";
		int sign_pos;
		for (int i=0;i<sign_list.length;i++){
			sign_pos=str.indexOf(sign_list[i]);
			if (sign_pos!=-1){
				sign=sign_list[i];
				lvar=str.substring(0,sign_pos);
				rvar=str.substring(sign_pos+sign_list[i].length());
				lvar=DisSpace.dislodge_space(lvar);
				rvar=DisSpace.dislodge_space(rvar);
				System.out.println("lvar  "+lvar+"  rvar  "+rvar+"  sign  "+sign_list[i]);
				//operate (lvar,rvar,sign);
			}
		}
		
	}
	
	//==========================conditions===========================//
	void Split_Cond(String s){
		String str=DisSpace.dislodge_space(s);
		System.out.println(str);
		String suborder;
		int comma,last_comma=0;
		do{
			comma=str.indexOf(",",last_comma);
			if (comma!=-1)
				suborder=s.substring(last_comma,comma);
			else suborder=s.substring(last_comma);
			suborder=DisSpace.dislodge_space(suborder);
			Update_Cond(suborder);
			last_comma=comma+1;
		}while(comma!=-1); 
	}
	
	void Update_Cond(String s){
		String str=DisSpace.dislodge_space(s);
		String lvar,rvar;
		int sign_pos;
		System.out.println("  m   "+s);
		sign_pos=str.indexOf("=");
		if (sign_pos!=-1){
			lvar=str.substring(0,sign_pos);
			rvar=str.substring(sign_pos+1);
			lvar=DisSpace.dislodge_space(lvar);
			rvar=DisSpace.dislodge_space(rvar);
			System.out.println("Update: lvar  "+lvar+"  rvar  "+rvar);
			//operate (lvar,rvar);
		}
		
	}
}
