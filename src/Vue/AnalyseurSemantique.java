package Vue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AnalyseurSemantique {
	
	public String ErreurMsg="";
	int indiceLab=0;
	int indicetp =0;
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
							System.out.println(tabSymbole.get(temp).getNom()+".PLACE = ALLOUER_ESPACE("+type+")");
							System.out.println(tabSymbole.get(temp).getNom()+".TYPE = "+type);
							tabSymbole.get(temp).setType(type);
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
		System.out.println("DEBUT");
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
					System.out.println(generateCode(lesInst[instI],indiceLab));
				
			}
				
		}
		return 1;
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
		//for(String xxx : set)
			//System.out.print(xxx +" **");
		//System.out.println();
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
	public String generateCode(String inst,int label)
	{
		String first = inst.split(" ")[1];
		if(first.equals("if"))
		{
			String[] lesmots = inst.split(" ");
			int nbreIf=0;
			boolean read=false;
			int nbreThen =0;
			String cond = "";
			String inst1 = "";
			String inst2 = "";
			int ii=0;
			for(int i=0;i<lesmots.length;i++)
			{
				if(lesmots[i].equals("else"))
				{
					nbreIf--;
					if(nbreIf==0){
						ii=i;
						break;
					}
				}
				if(nbreIf>0 && nbreThen>=1)
				{
					inst1+=lesmots[i]+" ";
				}
				if(lesmots[i].equals("then"))
				{	
					nbreThen++;
				}
				if(nbreIf==1 && nbreThen==0)
				{
					cond+=lesmots[i]+" ";
				}
				if(lesmots[i].equals("if"))
				{	
					nbreIf++;
				}
				
				
			}
			for(int i=ii+1;i<lesmots.length;i++)
			{
				inst2+=lesmots[i]+" ";
			}
			
			ArrayList<String> temp1 = generateCodeCond(cond);
			cond = temp1.get(1);
			String code=temp1.get(0)+"si "+cond+" aller à label"+(++label)+"\n";
			code+=(generateCode(" "+inst2.split("\\@")[0],label)+"\n"+"aller a finSi"+label+"\n");
			code+=("label"+label+":\n"+generateCode(" "+inst1,label)+"\n"+"finsi"+label+"");
			indiceLab = label;
			return code;
			
		}else
			if(first.equals("while"))
			{
				String cond = "";
				String inst1=" ";
				String[] lesmots;
				lesmots = inst.split("\\@")[0].split(" ");
				int nbrewhile=0;
				int ii=0;
				for(int i=0;i<lesmots.length;i++)
				{
					if(lesmots[i].equals("do"))
					{
						ii=i;
						break;
					}
					if(nbrewhile>0)
					{
						cond+=(lesmots[i]+" ");
					}
					if(lesmots[i].equals("while"))
					{
						nbrewhile++;
					}
				}
				for(int i=ii+1;i<lesmots.length;i++)
				{
					inst1+=(lesmots[i]+" ");
				}
				ArrayList<String> temp1 = generateCodeCond(cond);
				cond = temp1.get(1);
				String code = temp1.get(0)+"boucle"+(++label)+":\n"
						+"si "+cond+" aller à label"+(label)+"\n"
						+ "aller a finDo"+label+"\n"
								+ "label"+label+":\n"
										+ generateCode(inst1.split("\\@")[0], label)+"\n"
												+ "aller à boucle"+label+"\n"
												+ "finDo"+label+":";
				
				indiceLab = label;
				return code;
			}else
				if(inst.contains(":="))
				{
					ArrayList<String> temp = generateCodeEXP_SIMPLE(inst.split("\\@")[0].split(":=")[1]);
					return temp.get(0)+inst.replaceAll(" ", "").split("\\@")[0].split(":=")[0]+" := "+temp.get(1);
				}else
					return inst.replaceAll(" ", "").split("\\@")[0];
				
					
		
	}
	public String generateCOND(String cond,int label)
	{
		
		ArrayList<String> resultat = new ArrayList<>();
		while(cond.indexOf("(") != -1)
		{
			int indiceD=0;
			int indiceF=0;
			int nbreO=0;
			String tab[] = cond.split(" ");
			boolean passerparO= false;
			for(int i=0;i<tab.length;i++)
			{
				if(tab[i].equals("(")){
					if(nbreO==0)
						indiceD=i;
					nbreO++;
					passerparO=true;
				}
				if(tab[i].equals(")"))
					nbreO--;
				if(nbreO==0 && passerparO)
				{
					indiceF=i;
					break;
				}
			}
			String part1 = "";
			String part2= "";
			String part3 ="";
			for(int i=0;i<indiceD;i++)
			{
				part1+=tab[i]+" ";
			}
			for(int i=indiceD+1;i<indiceF;i++)
			{
				part3+=tab[i]+" ";
			}
			for(int i=indiceF+1;i<tab.length;i++)
			{
				part2+=tab[i]+" ";
			}
			resultat.add(part3);
			cond = part1+"VARTEMP"+label+" "+part2;
			label++;
		}
		String inter="";
		int labeli=0;
		for(String i : resultat)
		{
			inter+= generateCOND(i,labeli)+"\n";
		}
		return inter+cond;
		
	}
	public ArrayList<String> generateCodeCond(String cond)
	{
		String tempx = "";
		String result="";
		String[] tab = new String[2];
		String[] xcond = cond.split(">=|<=|==|<>|<|>");
		String[] tempTab = cond.split(" ");
		String serp="";
		AnalyseurLexical an = new AnalyseurLexical();
		for(int i=0;i<tempTab.length;i++)
		{
			if(an.isOprel(tempTab[i]))
			{
				serp=tempTab[i];
				break;
			}
		}
		for(int ij=0;ij<xcond.length;ij++)
		{	
			String[] lesConds = generateCOND(xcond[ij], ij).split("\n");
			int nbreDeVar=1;
			for(int indice=0;indice<lesConds.length;indice++)
			{
				String tabCond[] = lesConds[indice].split("VARTEMP");
				for(int in=1;in<tabCond.length&&lesConds[indice].contains("VARTEMP");in++)
				{
					int entier = Integer.parseInt(tabCond[in].charAt(0)+"");
					for(int x=indice-1;x>=0;x--)
					{
						if(!lesConds[x].contains(":="))
						{
							lesConds[x]="VARTEMP"+(ij)+(nbreDeVar)+" := "+lesConds[x];
							break;
						}
					}
					lesConds[indice] = lesConds[indice].split("VARTEMP"+tabCond[in].charAt(0)+"")[0]+"VARTEMP"+(ij)+(nbreDeVar++)+
							lesConds[indice].split("VARTEMP"+tabCond[in].charAt(0)+"")[1];
					
				}
			}
			lesConds[lesConds.length-1]="VARTEMP"+(ij)+(++nbreDeVar)+" := "+lesConds[lesConds.length-1];
			
			for(int indice=0;indice<lesConds.length;indice++)
			{
				tempx+=generacteToisAdresse(lesConds[indice]);
				//System.out.println(tempx);
			}
			tab[ij]=lesConds[lesConds.length-1];
		}
		result = tab[0].split(":=")[0]+" "+serp+" "+tab[1].split(":=")[0];
		ArrayList<String> xtempx=  new ArrayList<>();
		xtempx.add(tempx);
		xtempx.add(result);
		return xtempx;
		
	}
	public ArrayList<String> generateCodeEXP_SIMPLE(String cond)
	{
		String result="";
		String[] lesConds = generateCOND(cond, 0).split("\n");
		int nbreDeVar=1;
		for(int indice=0;indice<lesConds.length;indice++)
		{
			String tabCond[] = lesConds[indice].split("VARTEMP");
			for(int in=1;in<tabCond.length&&lesConds[indice].contains("VARTEMP");in++)
			{
				int entier = Integer.parseInt(tabCond[in].charAt(0)+"");
				for(int x=indice-1;x>=0;x--)
				{
					if(!lesConds[x].contains(":="))
					{
						lesConds[x]="VARTEMP"+(0)+(nbreDeVar)+" := "+lesConds[x];
						break;
					}
				}
				lesConds[indice] = lesConds[indice].split("VARTEMP"+tabCond[in].charAt(0)+"")[0]+"VARTEMP"+(0)+(nbreDeVar++)+
						lesConds[indice].split("VARTEMP"+tabCond[in].charAt(0)+"")[1];
				
			}

		}
		lesConds[lesConds.length-1]="VARTEMP"+(0)+(++nbreDeVar)+" := "+lesConds[lesConds.length-1];
		String xx="";
		for(int indice=0;indice<lesConds.length;indice++)
		{
			result=lesConds[indice].split(":=")[0];
			xx+=generacteToisAdresse(lesConds[indice]);
			//System.out.println(lesConds[indice]);
			
		}
		ArrayList<String> arr = new ArrayList<>();
		arr.add(xx);
		arr.add(result);
		return arr;
		
	}
	
	public String generacteToisAdresse(String in)
	{
		String r = in.split(":=")[0];
		String inst = in.split(":=")[1];
		AnalyseurLexical anl = new AnalyseurLexical();
		String[] tab1 = inst.split("\\+|\\-|\\|\\|");
		String[] tab2 = inst.split(" ");
		ArrayList<String> opadd = new ArrayList<>();
		ArrayList<String> sousInst = new ArrayList<>();
		int indiceTab = 0;
		for(int i=0;i<tab2.length;i++)
		{
			if(anl.isOpadd(tab2[i]))
				opadd.add(tab2[i]);
		}
		if(tab1.length>1)
		{
			String result ="";
			
			for(int i=1;i<tab1.length;i++)
			{
				//sousInst.add("temp"+indiceTab+" := "+tab1[i-1]+" "+opadd.get(indiceTab)+" "+tab1[i]);
				ArrayList<String> arr1 = generacteToisAdresseOPMULL(tab1[i-1],indicetp);
				ArrayList<String> arr2 = generacteToisAdresseOPMULL(tab1[i],indicetp);
				result+=arr1.get(0);
				result+=arr2.get(0);
				result +="temp"+indiceTab+" := "+arr1.get(1)+" "+opadd.get(indiceTab)+" "+arr2.get(1)+"\n";
				tab1[i] = "temp"+indiceTab;
				indiceTab++;
				
			}
			return result
					+r+ " := "+tab1[tab1.length-1]+"\n";
		}else
		{
			ArrayList<String> arr = generacteToisAdresseOPMULL(inst,indicetp);;
			return(arr.get(0)+r+" := "+arr.get(1)+"\n");
		}
		
	}
	public ArrayList<String> generacteToisAdresseOPMULL(String inst,int x)
	{
		AnalyseurLexical anl = new AnalyseurLexical();
		String[] tab1 = inst.split("\\*|\\/|\\&&");
		String[] tab2 = inst.split(" ");
		ArrayList<String> opmul = new ArrayList<>();
		ArrayList<String> sousInst = new ArrayList<>();
		int indiceTab = 0;
		for(int i=0;i<tab2.length;i++)
		{
			if(anl.isOpmul(tab2[i]))
				opmul.add(tab2[i]);
		}
		if(tab1.length>=1)
		{
			String result ="";
			for(int i=1;i<tab1.length;i++)
			{
				//sousInst.add("temp"+indiceTab+" := "+tab1[i-1]+" "+opadd.get(indiceTab)+" "+tab1[i]);
				result +="tp"+this.indicetp+" := "+tab1[i-1]+" "+opmul.get(indiceTab)+" "+tab1[i]+"\n";
				tab1[i] = "tp"+this.indicetp;
				this.indicetp++;
				indiceTab++;
				
			}
			ArrayList<String> arr = new ArrayList<>();
			arr.add(result);
			arr.add(tab1[tab1.length-1]);
			return arr;
		}else
		{
			ArrayList<String> arr = new ArrayList<>();
			arr.add("");
			arr.add(tab1[tab1.length-1]);
			return arr;
		}
		
	}

}
