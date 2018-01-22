package Vue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.jws.Oneway;

public class AnalyseurSemantique {
	
	public String ErreurMsg="";
	int labelP = 0;
	int labelT = 0;
	int labelTT = 0;
	int label = 0;
	String codeInter="";
	
	public int checkExpSimple(String expToCheck,ArrayList<Symbole> tabSymbole)
	{
		String r = expToCheck.split(":=")[0].replaceAll(" ","");
		int indexOfR =tabSymbole.indexOf(new Symbole("", r, "", ""));
		if(indexOfR == -1 || tabSymbole.get(indexOfR).getType().equals(""))
		{
			// ajouter ligne
			
			ErreurMsg = "Erreur Sémantique : var "+r+" non declaré ligne";
			return -1;
			
		}
		String ExpSimple = expToCheck.split(":=")[1].replaceAll(" ","");
		ExpSimple=ExpSimple.replaceAll("\\(", "");
		ExpSimple=ExpSimple.replaceAll("\\)", "");
		String[] Terme = ExpSimple.split("\\+|\\-|\\|\\|");
		for(int i=0;i<Terme.length;i++)
		{
			String[] term = Terme[i].split("\\*|\\/|\\&&");
			boolean nbExist = false;
			Set<String> set = new HashSet<>();
			for(int indTerm=0;indTerm<term.length;indTerm++)
			{
				if(isNumeric(term[indTerm]))
					nbExist=true;
				else
				{
					int indexOf =tabSymbole.indexOf(new Symbole("", term[indTerm], "", ""));
					if(indexOf == -1 || tabSymbole.get(indexOf).getType().equals(""))
					{
						ErreurMsg = "Erreur Sémantique : var "+term[indTerm]+" non declaré ligne";
						return -1;
						
					}else
					{
						Symbole symbole = tabSymbole.get(indexOf);
						set.add(symbole.getType());
					}
					
				}
			}
			if(tabSymbole.get(indexOfR).getType().equals("integer") && (set.size()>1||(set.size()==1 && set.contains("char"))))
			{
				ErreurMsg="Erreur Sémantique : on ne peut pas affecter char à integer ligne ";
				return -1;
			}
		}
		return 1;
	}
	
	public int executeANS(ArrayList<ArrayList<Symbole>> motlit,ArrayList<Symbole> tabSymbole)
	{
		boolean start = false;
		String P = motlit.get(0).get(1).getNom();
		for(Symbole s : tabSymbole)
		{
			if(s.getNom().equals(P))
			{
				s.setType("Nom du programme");
			}
		}
		String dcl="";
		String inst="";
		int ligne=1;
		boolean stop=false;
		for(ArrayList<Symbole> line : motlit)
		{
			for(Symbole s : line)
			{
				if(s.getTypeL().equals("begin")&& start){
					start = false;
					stop = true;
				}
					
				if(start && !stop){
					dcl+=s.getNom();
					if(s.getNom().equals(";"))
						dcl+=ligne;
				}
				
				if(s.getTypeL().equals(";") && !start)
					start = true;
				if(s.getNom().equals(";")){
					inst+="@"+ligne+"@";
					
				}
				inst+=s.getNom()+" ";
			}
			ligne++;
		}
		String[] tab1 = dcl.split("var");
		inst = inst.split("begin")[1];
		inst = inst.split("end")[0];
		String lesInst[] = inst.split(";");
		
		for(int i=0;i<tab1.length && !dcl.equals("");i++)
		{
			if(tab1[i].length()>0)
			{
				String[] tab2= tab1[i].split(":");
				String[] variables = tab2[0].split(",");
				String type = tab2[1].split(";")[0];
				String lineDcl = tab2[1].split(";")[1];
				ArrayList<String> vars = new ArrayList<>();
				for(int j=0;j<variables.length;j++)
				{
					vars.add(variables[j]);
				}
				for(String s : vars)
				{
					int temp=tabSymbole.indexOf(new Symbole("", s, "", ""));
					if(temp!=-1)
					{
						if(tabSymbole.get(temp).getType().equals("")){
							tabSymbole.get(temp).setType(type);
							codeInter+=temp+".Place := ALLOUER("+type+")\n";
							codeInter+=temp+".Type := "+type+"\n";
						}
						else{
							
							ErreurMsg = "Erreur Semantique : Deux variables ont le meme ID '"+s+"' ligne "+lineDcl;
							System.out.println("Erreur Semantique : Deux variables ont le meme ID '"+s+"' ligne "+lineDcl);
							return -1;
						}
					}
				}
				
			}
		}
		if(lesInst.length-1>=0 && !lesInst[lesInst.length-1].equals(" "))
			lesInst[lesInst.length-1]+="@"+(ligne-2)+"@";
		
		for(int instI=0;instI<lesInst.length;instI++)
		{
			if(!lesInst[instI].equals(" "))
			{	
				if(checkInst(lesInst[instI], tabSymbole)==-1)
				{
					return -1;
				}else
				{
					//generate code
					codeInter+=generateCodeInst(lesInst[instI].split("@")[0],label);
					
				}
			}
				
		}
		System.out.println(codeInter);
		return 1;
	}
	public ArrayList<String> generateFacteur3Adr(String inst)
	{
		String result="";
		String[] termes = inst.split("\\*|\\/|\\&&");
		if(termes.length>1)
		{	
			String[] sep = Editeur.addSpace(inst).split(" ");
			AnalyseurLexical anl = new AnalyseurLexical();
			ArrayList<String> lesAddOp = new ArrayList<>();
			int indinceOpAdd = 0;
			for(int i=0;i<sep.length;i++)
			{
				
				if(anl.isOpmul(sep[i]))
				{
					lesAddOp.add(sep[i]);
				}
			}
			for(int i=1;i<termes.length;i++)
			{
				result+="tempVar"+(labelTT)+" :="+termes[i-1]+" "+lesAddOp.get(indinceOpAdd)+" "+termes[i]+"\n";
				termes[i]="tempVar"+(labelTT);
				indinceOpAdd++;
				labelTT++;
			}
			labelTT--;
			ArrayList<String> arr = new ArrayList<>();
			arr.add(result);
			arr.add(termes[termes.length-1]);
			return arr;
		}else
		{
			ArrayList<String> arr = new ArrayList<>();
			arr.add("");
			arr.add(inst);
			return arr;
		}
	}
	public String generateTerme3Adr(String inst)
	{
		String r = inst.split(":=")[0];
		String rest = inst.split(":=")[1];
		String[] termes = rest.split("\\+|\\-|\\|\\|");
		if(termes.length>=2)
		{	
			String[] sep = Editeur.addSpace(rest).split(" ");
			AnalyseurLexical anl = new AnalyseurLexical();
			ArrayList<String> lesAddOp = new ArrayList<>();
			int indinceOpAdd = 0;
			for(int i=0;i<sep.length;i++)
			{
				
				if(anl.isOpadd(sep[i]))
				{
					lesAddOp.add(sep[i]);
				}
			}
			String result="";
			for(int i=1;i<termes.length;i++)
			{
				ArrayList<String> arr1 = generateFacteur3Adr(termes[i-1]);
				ArrayList<String> arr2 = generateFacteur3Adr(termes[i]);
				result+=arr1.get(0);
				result+=arr2.get(0);
				result+="tempVar"+(labelT)+" :="+arr1.get(1)+" "+lesAddOp.get(indinceOpAdd)+" "+arr2.get(1)+"\n";
				termes[i]="tempVar"+(labelT);
				indinceOpAdd++;
				labelT++;
			}
			labelT--;
			return result+r+" := tempVar"+(labelT++)+"\n";
		}else
		{
			ArrayList<String> arr1 = generateFacteur3Adr(inst.split(":=")[1]);
			return arr1.get(0)+"\n"+inst.split(":=")[0]+" := "+arr1.get(1);
		}
	}
	public ArrayList<String> deleteParenthese(String cond)
	{
		String resutl="";
		int indice = 0;
		int i =0;
		cond = cond.replace(" ", "");
		//if(cond.contains("(")){
			while(cond.contains("("))
			{
				if(cond.charAt(i) == '(')
					indice = i;
				if(cond.charAt(i)==')')
				{
					String part1 = cond.substring(0,indice);
					String part2 = cond.substring(i+1, cond.length());
					String part3 = cond.substring(indice+1,i);
					resutl+=generateTerme3Adr("tp"+(labelP)+" := "+part3)+"\n";
					cond = part1+"tp"+(labelP)+part2;
					labelP++;
					i=indice=0;
				}else
					i++;
			
			}
		//}else
		//{
			String[] tabtemp = Editeur.addSpace(cond).split(" ");
			AnalyseurLexical anl = new AnalyseurLexical();
			String oprel ="";
			boolean separation = false;
			
			String part1 = "";
			String part2 = "";
			
			for(int j=0;j<tabtemp.length;j++)
			{
				if(separation)
				{
					part2+= tabtemp[j]+" ";
				}
				if(anl.isOprel(tabtemp[j]))
				{
					separation = true;
					oprel = tabtemp[j];
				}
				if(!separation)
				{
					part1+= tabtemp[j]+" ";
				}
			}
			
			resutl+=generateTerme3Adr("tp"+(labelP)+" := "+part1);
			resutl+=generateTerme3Adr("tp"+(labelP+1)+" := "+part2)+"\n";
			cond = "tp"+(labelP)+oprel+"tp"+(labelP+1);
			labelP+=2;
		//}
		ArrayList<String> arr = new ArrayList<>();
		arr.add(Editeur.addSpace(resutl));
		arr.add(Editeur.addSpace(cond));
		return arr;
	}
	public ArrayList<String> deleteParenthese2(String cond)
	{
		String resutl="";
		int indice = 0;
		int i =0;
		cond = cond.replace(" ", "");
			while(cond.contains("("))
			{
				if(cond.charAt(i) == '(')
					indice = i;
				if(cond.charAt(i)==')')
				{
					String part1 = cond.substring(0,indice);
					String part2 = cond.substring(i+1, cond.length());
					String part3 = cond.substring(indice+1,i);
					resutl+=generateTerme3Adr("tp"+(labelP)+" := "+part3)+"\n";
					cond = part1+"tp"+(labelP)+part2;
					labelP++;
					i=indice=0;
				}else
					i++;
			
			}
		resutl+=generateTerme3Adr("tp"+(labelP)+" := "+cond)+"\n";
		cond = "tp"+(labelP);
		labelP++;
		ArrayList<String> arr = new ArrayList<>();
		arr.add(Editeur.addSpace(resutl));
		arr.add(Editeur.addSpace(cond));
		return arr;
	}
	public String generateCodeInst(String inst,int labelX)
	{
		String result = "";
		String first =inst.split(" ")[1];
		if(first.equals("if"))
		{
			String cond = inst.split("if")[1].split("then")[0];
			int aux1 = inst.indexOf("then")+4;
			int aux2 = inst.lastIndexOf("else")+4;
			String inst1 = inst.substring(aux1,aux2-4);
			String inst2 = inst.substring(aux2,inst.length());
			ArrayList<String> arr = deleteParenthese(cond);
			result+=arr.get(0);
			result+="Si "+arr.get(1)+" aller à label"+labelX+"\n";
			result+=generateCodeInst(inst2,labelX+1);
			result+="aller à FinSi"+labelX+"\n";
			result+="label"+labelX+":\n";
			result+=generateCodeInst(inst1,labelX+1)+"\n";
			result+="FinSi"+labelX+":\n";
			labelX++;
			return result;
			
		}else
			if(first.equals("while"))
			{
				String cond = inst.split("while")[1].split("do")[0];
				ArrayList<String> arr = deleteParenthese(cond);
				int aux1 = inst.indexOf("do")+2;
				String inst1 = inst.substring(aux1,inst.length());
				result+=arr.get(0);
				result+="boucle"+labelX+":\n";
				result+="Si "+arr.get(1)+" aller à label"+labelX+"\n";
				result+="aller à FinBoucle"+labelX+":\n";
				result+="label"+labelX+":\n";
				result+=generateCodeInst(inst1,labelX+1)+"\n";
				result+="aller à boucle"+labelX+"\n";
				result+="FinBoucle"+labelX+":\n";
				labelX++;
				return result;
			}else
				if(inst.contains(":="))
				{
					ArrayList<String> arr = deleteParenthese2(inst.split(":=")[1]);
					return arr.get(0)+"\n"+
					inst.split(":=")[0]+" := "+arr.get(1);
				}else
				{
					return inst+"\n";
				}
		
	}
	public boolean isNumeric(String str)
	{
		try{

			int x = Integer.parseInt(str);
			return true;
			
		}catch(Exception e)
		{
			return false;
		}
	}
	public int checkInst(String instruction,ArrayList<Symbole> tabSymbole)
	{
		String aux1 = instruction.split("\\@")[0];
		int ligneCourante = Integer.parseInt(instruction.split("\\@")[1]);
		instruction = aux1;
		String first =instruction.split(" ")[1];
		if(first.equals("if") || first.equals("while"))
		{
			String[] sousInst = instruction.split("do|then|else");
			for(int i=0;i<sousInst.length;i++){
				if(sousInst[i].contains("if") || sousInst[i].contains("while"))
				{
					String exp = sousInst[i].replaceAll(" ", "").split("if|while")[1];
					String[] exp_simple = exp.split(">=|<=|==|<>|<|>");
					Set<String> lesTypesDesExp_simples = new HashSet();
					for(int j=0;j<exp_simple.length;j++)
					{
						int temp = checkExp_simple(exp_simple[j], tabSymbole);
						//System.out.println(exp_simple[j]+ " temp = "+temp);
						if(temp == -1)
						{
							ErreurMsg+=" "+ligneCourante;
							return -1;
						}else
						{
							lesTypesDesExp_simples.add(temp+"");
						}
					}
					if(lesTypesDesExp_simples.size()>1)
					{
						ErreurMsg="Erreur Sémantique : on ne peut pas comparer deux expressions de types icompatibles lignes "+ligneCourante;
						return -1;
					}
				}else
				{
					checkInst(sousInst[i]+"@"+ligneCourante+"@", tabSymbole);
				}
				
			}
			
			
		}else
		{ 
			if(!first.equals("read") && !first.equals("readln") && !first.equals("write") && !first.equals("writeln"))
			{
				if(checkExpSimple(instruction, tabSymbole)==-1){
					ErreurMsg+=" "+ligneCourante;
					return -1;
				}
			}else
			{
				//System.out.println(instruction);
				String idTraitement= instruction.split("\\(")[1];
				idTraitement = idTraitement.split("\\)")[0];
				int indexOfR =tabSymbole.indexOf(new Symbole("", idTraitement.replaceAll(" ",""), "", ""));
				if(indexOfR == -1 || tabSymbole.get(indexOfR).getType().equals(""))
				{
					ErreurMsg = "Erreur Sémantique : var "+idTraitement+" non declaré ligne "+ligneCourante;
					return -1;
				}
			}
		}
		return 1;
	}
	
	public int checkExp_simple(String expToCheck,ArrayList<Symbole> tabSymbole)
	{
		String ExpSimple = expToCheck.replaceAll(" ","");
		ExpSimple=ExpSimple.replaceAll("\\(", "");
		ExpSimple=ExpSimple.replaceAll("\\)", "");
		String[] Terme = ExpSimple.split("\\+|\\-|\\|\\|");
		Set<String> set = new HashSet<>();
		for(int i=0;i<Terme.length;i++)
		{
			String[] term = Terme[i].split("\\*|\\/|\\&&");
			boolean nbExist = false;
			for(int indTerm=0;indTerm<term.length;indTerm++)
			{
				if(isNumeric(term[indTerm])){ 
					set.add("integer");
					nbExist=true;
				}else
				{
					int indexOf =tabSymbole.indexOf(new Symbole("", term[indTerm], "", ""));
					if(indexOf == -1 || tabSymbole.get(indexOf).getType().equals(""))
					{
						ErreurMsg = "Erreur Sémantique : var "+term[indTerm]+" non declaré ligne";
						return -1;
						
					}else
					{
						Symbole symbole = tabSymbole.get(indexOf);
						set.add(symbole.getType());
					}
					
				}
			}
			
		}
		if(set.size()==2)				
			return 2;
		else
		{
			if(set.contains("char"))
				return 2;
			else
				return 1;
		}	
	}
	
	
	

}
