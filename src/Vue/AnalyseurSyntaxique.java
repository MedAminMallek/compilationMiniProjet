package Vue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Stack;

public class AnalyseurSyntaxique {
	
	private ArrayList<Regle> grammaire = new ArrayList<Regle>();
	private int[][] tableAnalyse = new int[18][28];
	private ArrayList<String> indiceNT = new ArrayList<String>();
	private ArrayList<String> indiceT = new ArrayList<String>();
	private ArrayList<String> terminal = new ArrayList<String>();
	private int indiceL,indiceC;
	public String msgErreur = "";
	String log ="";
	
	public AnalyseurSyntaxique() {
		
		indiceL =indiceC=0;
		grammaire.add(new Regle(1, "P", "program id ; Dcl Inst_composée ."));
		grammaire.add(new Regle(2, "Dcl", "A'"));
		grammaire.add(new Regle(31, "A'", "var Liste_id : Type ; A'"));
		grammaire.add(new Regle(32, "A'", ""));
		grammaire.add(new Regle(4, "Liste_id", "id B'"));
		grammaire.add(new Regle(51, "B'", ", id B'"));
		grammaire.add(new Regle(52, "B'", ""));
		grammaire.add(new Regle(61, "Type", "integer"));
		grammaire.add(new Regle(62, "Type", "char"));
		grammaire.add(new Regle(7, "Inst_composée", "begin Inst end"));
		grammaire.add(new Regle(81, "Inst", "Liste_inst"));
		grammaire.add(new Regle(82, "Inst", ""));
		grammaire.add(new Regle(9, "Liste_inst", "I C'"));
		grammaire.add(new Regle(101, "C'", "; I C'"));
		grammaire.add(new Regle(102, "C'", ""));
		grammaire.add(new Regle(111, "I", "id := Exp_simple"));
		grammaire.add(new Regle(112, "I", "if Exp then I else I"));
		grammaire.add(new Regle(113, "I", "while Exp do I"));
		grammaire.add(new Regle(114, "I", "read ( id )"));
		grammaire.add(new Regle(115, "I", "readln ( id )"));
		grammaire.add(new Regle(116, "I", "write ( id )"));
		grammaire.add(new Regle(117, "I", "writeln ( id )"));
		grammaire.add(new Regle(12, "Exp", "Exp_simple F'"));
		grammaire.add(new Regle(13, "Exp_simple", "Terme D'"));
		grammaire.add(new Regle(141, "D'", "opadd Terme D'"));
		grammaire.add(new Regle(142, "D'", ""));
		grammaire.add(new Regle(15, "Terme'", "Facteur E'"));
		grammaire.add(new Regle(161, "E'", "opmul Facteur E'"));
		grammaire.add(new Regle(162, "E'", ""));
		grammaire.add(new Regle(171, "Facteur", "id"));
		grammaire.add(new Regle(172, "Facteur", "nb"));
		grammaire.add(new Regle(173, "Facteur", "( Exp_simple )"));
		grammaire.add(new Regle(181, "F'", "oprel Exp_simple"));
		grammaire.add(new Regle(182, "F'", ""));
		
		indiceNT.add("P");
		indiceNT.add("Dcl");
		indiceNT.add("Liste_id");
		indiceNT.add("Type");
		indiceNT.add("Inst_composée");
		indiceNT.add("Inst");
		indiceNT.add("Liste_inst");
		indiceNT.add("I");
		indiceNT.add("Exp");
		indiceNT.add("Exp_simple");
		indiceNT.add("Terme");
		indiceNT.add("Facteur");
		indiceNT.add("A'");
		indiceNT.add("B'");
		indiceNT.add("C'");
		indiceNT.add("D'");
		indiceNT.add("E'");
		indiceNT.add("F'");
		
		indiceT.add("program");
		indiceT.add("var");
		indiceT.add("begin");
		indiceT.add("id");
		indiceT.add("integer");
		indiceT.add("char");
		indiceT.add(".");
		indiceT.add(":");
		indiceT.add(";");
		indiceT.add("if");
		indiceT.add("while");
		indiceT.add("read");
		indiceT.add("readln");
		indiceT.add("write");
		indiceT.add("writeln");
		indiceT.add("nb");
		indiceT.add("(");
		indiceT.add(")");
		indiceT.add("opadd");
		indiceT.add("opmul");
		indiceT.add("oprel");
		indiceT.add("end");
		indiceT.add("then");
		indiceT.add("do");
		indiceT.add("$");
		indiceT.add(",");
		indiceT.add("else");
		indiceT.add(":=");
		
		terminal.addAll(indiceT);
		
		for(int i=0;i<18;i++)
			for(int j=0;j<28;j++)
				tableAnalyse[i][j]=-1;
		
		tableAnalyse[0][0] = 1;
		tableAnalyse[1][1] = 2;tableAnalyse[1][2] = 2;
		tableAnalyse[2][3] = 4;
		tableAnalyse[3][4] = 61;tableAnalyse[3][5] = 62;
		tableAnalyse[4][2] = 7;
		tableAnalyse[5][3] = 81;
		for(int i=9;i<15;i++)
			tableAnalyse[5][i] = 81;
		tableAnalyse[5][21] = 82;
		tableAnalyse[6][3] = 9;
		for(int i=9;i<15;i++)
			tableAnalyse[6][i] = 9;
		tableAnalyse[7][3] = 111;
		for(int i=9,t=112;i<15;i++,t++)
			tableAnalyse[7][i] = t;
		tableAnalyse[8][3] = tableAnalyse[8][15]=tableAnalyse[8][16]=12;
		tableAnalyse[9][3] = tableAnalyse[9][15]=tableAnalyse[9][16]=13;
		tableAnalyse[10][3] = tableAnalyse[10][15]=tableAnalyse[10][16]=15;
		tableAnalyse[11][3] = 171;tableAnalyse[11][15]=172;tableAnalyse[11][16]=173;
		tableAnalyse[12][1] = 31;tableAnalyse[12][2]=32;tableAnalyse[12][24]=32;
		tableAnalyse[13][7] = 52;tableAnalyse[13][25]=51;
		tableAnalyse[14][8]=101;tableAnalyse[14][21]=102;
		tableAnalyse[15][18]=141;tableAnalyse[15][17]=tableAnalyse[15][20]=tableAnalyse[15][21]=tableAnalyse[15][22]=tableAnalyse[15][23]=tableAnalyse[15][26]=tableAnalyse[15][8]=142;
		tableAnalyse[16][19]=161;tableAnalyse[16][17]=tableAnalyse[16][18]=tableAnalyse[16][20]=tableAnalyse[16][21]=tableAnalyse[16][22]=tableAnalyse[16][23]=tableAnalyse[16][26]=tableAnalyse[16][8]=162;
		tableAnalyse[17][20]=181;tableAnalyse[17][22]=tableAnalyse[17][23]=182;
		
	}
	public ArrayList<Regle> getGrammaire() {
		return grammaire;
	}
	public void setGrammaire(ArrayList<Regle> grammaire) {
		this.grammaire = grammaire;
	}
	
	public int[][] getTableAnalyse() {
		return tableAnalyse;
	}
	public void setTableAnalyse(int[][] tableAnalyse) {
		this.tableAnalyse = tableAnalyse;
	}
	public ArrayList<String> getIndiceNT() {
		return indiceNT;
	}
	public void setIndiceNT(ArrayList<String> indiceNT) {
		this.indiceNT = indiceNT;
	}
	public ArrayList<String> getIndiceT() {
		return indiceT;
	}
	public void setIndiceT(ArrayList<String> indiceT) {
		this.indiceT = indiceT;
	}
	
	public void afficherTable()
	{
		System.out.print(" ");
		for(int j=0;j<28;j++)
		{
			System.out.print(indiceT.get(j)+" ");
		}
		System.out.println();
		for(int i=0;i<18;i++)
		{
			System.out.print(indiceNT.get(i)+" ");
			for(int j=0;j<28;j++)
			{
				System.out.print(tableAnalyse[i][j]+" ");
			}
			System.out.println();
		}
				
	}
	public String nextItem(ArrayList<ArrayList<Symbole>> motlu)
	{
		if(indiceL>=motlu.size())
			return "$";
		else
		{
			if(indiceC>=motlu.get(indiceL).size())
			{
				indiceC=0;
				indiceL++;
				if(indiceL>=motlu.size())
					return "$";
				else{
					if(motlu.get(indiceL).size()>0)
						return motlu.get(indiceL).get(indiceC).getTypeL();
					else
					{
						while(motlu.get(indiceL).size()==0)
							indiceL++;
						if(indiceL>=motlu.size())
							return "$";
						else
							return motlu.get(indiceL).get(indiceC).getTypeL();
					}
				}
			}else
			{
				return motlu.get(indiceL).get(indiceC).getTypeL();
			}
		}
	}
	public boolean isTerminal(String x)
	{
		return terminal.contains(x);
	}
	public ArrayList<String> getRegle(int code)
	{
		Regle re = null;
		for(Regle r : grammaire)
		{
			if(r.getCode() == code)
			{
				re = r;
				break;
			}
		}
		String[] arr = re.getDroite().split(" ");
		ArrayList<String> res = new ArrayList<>();
		for(String s : arr){
			log+=s+" ";
			//System.out.print(s+" ");
		}
			
		for(int i=arr.length-1;i>=0;i--)
		{
			res.add(arr[i]);
		}
		return res;
	}
	public void executerANS(ArrayList<ArrayList<Symbole>> motlu)
	{
		Stack<String> pile = new Stack<String>();
		pile.push("$");pile.push("P");
		String mot = nextItem(motlu);
		String X = pile.peek();
		do
		{
			X = pile.peek();
			if(X.equals("$") ||isTerminal(X))
			{
				if(X.equals(mot))
				{
					pile.pop();
					indiceC++;
					mot = nextItem(motlu);
					
				}else
				{
					msgErreur = "Erreur Syntaxique: on attend "+X+" non "+mot+" ligne "+(indiceL+1);
					//System.out.println("on attend "+X+" non "+mot+" _ligne "+(indiceL+1));
					break;
				}
				
			}else
			{
				
				if(tableAnalyse[indiceNT.indexOf(X)][indiceT.indexOf(mot)] != -1)
				{
					pile.pop();
					int code = tableAnalyse[indiceNT.indexOf(X)][indiceT.indexOf(mot)];
					//System.out.print(X+"==>");
					log+=X+"==>";
					ArrayList<String> arr = getRegle(code);
					for(String s : arr){
						if(!s.equals(""))
						pile.push(s);
					}
					//System.out.println();
					log+="\n";
				}else
				{
					msgErreur = "Erreur Syntaxique ligne "+(indiceL+1);
					//System.out.println("erreur ligne"+(indiceL+1)+" "+X+" "+mot);
					break;
				}
			}
				
		}while(X!="$");
		try {
			PrintWriter writer = new PrintWriter("log-analyse-syntaxique.txt", "UTF-8");
			writer.println(log);
			writer.println(msgErreur);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
