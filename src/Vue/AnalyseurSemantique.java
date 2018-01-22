package Vue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AnalyseurSemantique {
	
	public String ErreurMsg="";
	int labelP = 0;
	
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
						if(tabSymbole.get(temp).getType().equals(""))
							tabSymbole.get(temp).setType(type);
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
					generateCodeInst(lesInst[instI].split("@")[0]);
				}
			}
				
		}
		return 1;
	}
	public String generateTerme3Adr(String inst)
	{
		String r = inst.split(":=")[0];
		String rest = inst.split(":=")[1];
		String[] termes = rest.split("\\+|\\-|\\|\\|");
		
		return "";
	}
	public String deleteParenthese(String cond)
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
				resutl+="tp"+(labelP)+":="+part3+"\n";
				cond = part1+"tp"+(labelP)+part2;
				labelP++;
				i=indice=0;
			}else
				i++;
			
		}
		
		return Editeur.addSpace(resutl+cond);
	}
	public String GenerateCodePartieDroite(String cond)
	{
		
		return "";
	}
	public String generateCodeInst(String inst)
	{
		String first =inst.split(" ")[1];
		if(first.equals("if"))
		{
			String cond = inst.split("if")[1].split("then")[0];
			int aux1 = inst.indexOf("then")+4;
			int aux2 = inst.lastIndexOf("else")+4;
			String inst1 = inst.substring(aux1,aux2-4);
			String inst2 = inst.substring(aux2,inst.length());
			System.out.println(deleteParenthese(cond));
			return generateCodeInst(inst1)+"\n"+generateCodeInst(inst2);
			
		}else
			if(first.equals("while"))
			{
				String cond = inst.split("while")[1].split("do")[0];
				System.out.println(deleteParenthese(cond));
				int aux1 = inst.indexOf("do")+2;
				String inst1 = inst.substring(aux1,inst.length());//inst.split("while")[1].split("do")[1];
				return generateCodeInst(inst1);
			}else
				if(inst.contains(":="))
				{
					return inst;
				}else
				{
					return inst;
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
