package Vue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AnalyseurLexical {
	
	public String ErreurMsg="";
	//public ArrayList<ArrayList<String>> tab = new ArrayList<>();
	public ArrayList<ArrayList<Symbole>> motLit = new ArrayList<>();
	public boolean symboleExistant(String nom,ArrayList<Symbole> table)
	{
		for(Symbole s : table)
			if(s.getNom().equals(nom))
				return true;
		return false;
	}
	public void executerAnalyse(String str,ArrayList<Symbole> tableSymboles)
	{
		String lesID ="";
		String lesNumber="";
		String lesMotCle="";
		String lesOprel="";
		String lesOpadd="";
		String lesOpmul="";
		
		int i=1;
		for (String line : str.split("\\n"))
		{
			
			motLit.add(new ArrayList<Symbole>());
			
			for(String mot : line.split(" "))
			{
				
				if(mot.compareTo("")!=0 && mot.compareTo(" ")!=0 && mot.compareTo("\\n")!=0)
				{
					if(isId(mot)){
						//tab.get(i-1).add("id");
						motLit.get(i-1).add(new Symbole("id",mot, "", ""));
						if(!symboleExistant(mot, tableSymboles))
							tableSymboles.add(new Symbole("id" ,mot, "", ""));
						lesID+=mot+" ";
					}
					else
						if(isChiffre(mot)){
							
							//tab.get(i-1).add("nb");
							motLit.get(i-1).add(new Symbole("nb",mot, "", ""));
							lesNumber+=mot+" ";
						}
						else
							if(isMotCle(mot)){
								
								motLit.get(i-1).add(new Symbole(mot,mot, "", ""));
								//tab.get(i-1).add(mot);
								lesMotCle+=mot+" ";
							}
							else
								if(isOprel(mot)){
									motLit.get(i-1).add(new Symbole("oprel",mot, "", ""));
									lesOprel+=mot+" ";
									//tab.get(i-1).add("oprel");
								}
								else
									if(isOpadd(mot)){
										lesOpadd+=mot+" ";
										motLit.get(i-1).add(new Symbole("opadd",mot, "", ""));
										//tab.get(i-1).add("opadd");
									}
									else
										if(isOpmul(mot)){
											lesOpmul+=mot+" ";
											motLit.get(i-1).add(new Symbole("opmul",mot, "", ""));
											//tab.get(i-1).add("opmul");
										}
										else
										{
											//System.out.print(mot+" "+isId(mot));
											ErreurMsg = "erreur lexical ligne "+i+" "+mot;
											break;
										}
					
				}
				
			}
			 
			if(ErreurMsg.compareTo("")!=0)
				break;
			i++;
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter("log-analyse-lexical.txt", "UTF-8");
			String s[] = lesID.split(" ");
			ArrayList<String> xx = new ArrayList<>();
			for(int ind=0;ind<s.length;ind++)
				if(!xx.contains(s[ind]))
					xx.add(s[ind]);
				
			writer.println("ID= "+xx.toString());
			s = lesNumber.split(" ");
			xx = new ArrayList<>();
			for(int ind=0;ind<s.length;ind++)
				if(!xx.contains(s[ind]))
					xx.add(s[ind]);
			
			writer.println("NB= "+xx.toString());
			s = lesMotCle.split(" ");
			xx = new ArrayList<>();
			for(int ind=0;ind<s.length;ind++)
				if(!xx.contains(s[ind]))
					xx.add(s[ind]);
			
			writer.println("MotCle= "+xx.toString());
			s = lesOprel.split(" ");
			xx = new ArrayList<>();
			for(int ind=0;ind<s.length;ind++)
				if(!xx.contains(s[ind]))
					xx.add(s[ind]);
			
			writer.println("OPREL= "+xx);
			s = lesOpadd.split(" ");
			xx = new ArrayList<>();
			for(int ind=0;ind<s.length;ind++)
				if(!xx.contains(s[ind]))
					xx.add(s[ind]);
			
			writer.println("OPADD= "+xx.toString());
			s = lesOpmul.split(" ");
			xx = new ArrayList<>();
			for(int ind=0;ind<s.length;ind++)
				if(!xx.contains(s[ind]))
					xx.add(s[ind]);
			writer.println("OPMUL= "+xx.toString());
			
			writer.println(ErreurMsg);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean isLettre(String str, int index)
	{
		return (str.toUpperCase().charAt(index)>= 'A')&&(str.toUpperCase().charAt(index)<= 'Z');
	}
	private boolean isChiffre(String str, int index)
	{
		return (str.charAt(index)>= '0')&&(str.charAt(index)<= '9');
	}
	public boolean isChiffre(String str)
	{
		for(int i=0;i<str.length();i++)
		{
			if(!isChiffre(str,i))
				return false;
		}
		return true;
	}
	public boolean isId(String id)
	{
		//if(id !=""){
			if(isMotCle(id))
				return false;
			if(isLettre(id, 0))
			{
				for(int i=1;i<id.length();i++)
				{
					if(!isChiffre(id, i) && !isLettre(id, i))
						return false;
				}
			}else
				return false;
		//}
		return true;
			
	}
	public boolean isMotCle(String str)
	{
		ArrayList<String> MC = new ArrayList<String>();
		MC.add("program");MC.add("var");
		MC.add("integer");MC.add("char");
		MC.add("begin");MC.add("end");
		MC.add("if");MC.add("then");
		MC.add("else");MC.add("while");
		MC.add("do");MC.add("read");
		MC.add("readln");MC.add("write");
		MC.add("writeln");MC.add(".");
		MC.add(";");MC.add(":");MC.add(")");
		MC.add("(");MC.add(",");MC.add(":=");
		if(!MC.contains(str))
			return false;
		return true;
	}
	public boolean isOprel(String str)
	{
		ArrayList<String> oprel = new ArrayList<String>();
		oprel.add("==");oprel.add("<>");
		oprel.add("<");oprel.add("<=");
		oprel.add(">");oprel.add(">=");
		if(!oprel.contains(str)) return false;
		return true;
	}
	public boolean isOpadd(String str)
	{
		ArrayList<String> opadd = new ArrayList<String>();
		opadd.add("+");opadd.add("-");
		opadd.add("||");
		if(!opadd.contains(str)) return false;
		return true;
	}
	public boolean isOpmul(String str)
	{
		ArrayList<String> opmul = new ArrayList<String>();
		opmul.add("*");opmul.add("/");
		opmul.add("%");opmul.add("&&");
		if(!opmul.contains(str)) return false;
		return true;
	}
}
