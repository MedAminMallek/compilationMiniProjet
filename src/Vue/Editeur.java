package Vue;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Editeur implements ActionListener,KeyListener{
	private JTextArea Jtext ;
	private JLabel Jlabel;
	
	public Editeur()
	{
		JFrame fenetre = new JFrame();
        fenetre.setTitle("Projet Compilation");
	    fenetre.setSize(600, 250);
	    fenetre.setLocationRelativeTo(null);
	    fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    Panel p = new Panel(new BorderLayout());
	    Jtext = new JTextArea();
	    
	    JScrollPane sp = new JScrollPane(Jtext);
	    Button b = new Button("Compiler");
	    Jlabel = new JLabel("");
	    Jlabel.setForeground(Color.RED);
	    b.addActionListener(this);
	    p.add(sp, BorderLayout.CENTER);
	    p.add(b,BorderLayout.EAST);
	    p.add(Jlabel,BorderLayout.AFTER_LAST_LINE);
	    //Jtext.addKeyListener(this);
	    fenetre.add(p);
	    fenetre.setVisible(true);
	}
	
	public static void main(String[] args) {
		
		
		//ANS.afficherTable();
		
		new Editeur();
	}

	public String deleteComment(String txt)
	{
		while(txt.indexOf("(*")!=-1)
		{
			txt = txt.substring(0, txt.indexOf("(*")) + txt.substring(txt.indexOf("*)")+2, txt.length());
		}
		return txt;
	}
	
	public String addSpace(String mot)
	{
		ArrayList<String> cle = new ArrayList<>();
		cle.add(";");
		cle.add(")");
		cle.add("(");
		cle.add("+");
		cle.add("*");
		cle.add("-");
		cle.add("/");
		cle.add("%");
		cle.add("<");
		cle.add(">");
		cle.add(",");
		
		cle.add(":=");
		cle.add("&&");
		cle.add("||");
		cle.add("<>");
		cle.add("<=");
		cle.add(">=");
		cle.add("==");
		
		int i = 1;
		while(i<mot.length()-2)
		{
			String xx =""+mot.charAt(i);
			if(cle.contains(xx) && mot.charAt(i-1)!=' ')
			{
				String str1 = mot.substring(0, i);
				String str2 = mot.substring(i, mot.length());
				mot = str1 +" "+str2;
				//System.out.println(mot+"\n **1");
			}else
			{
				String x =""+mot.charAt(i)+mot.charAt(i+1);
				if(cle.contains(x) && mot.charAt(i-1)!=' ' && x.length()==2)
				{
					String str1 = mot.substring(0, i);
					String str2 = mot.substring(i, mot.length());
					mot = str1 +" "+str2;
					//System.out.println(mot+"\n **2");
				}
			}
			if(cle.contains(xx) && mot.charAt(i+1)!=' ')
			{
				String str1 = mot.substring(0, i+1);
				String str2 = mot.substring(i+1, mot.length());
				mot = str1 +" "+str2;
				//System.out.println(mot+"\n **3");
			}else
			{
				String x =""+mot.charAt(i)+mot.charAt(i+1);
				if(cle.contains(x) && mot.charAt(i+2)!=' ' && x.length()==2)
				{
					String str1 = mot.substring(0, i+2);
					String str2 = mot.substring(i+2, mot.length());
					mot = str1 +" "+str2;
					//System.out.println(mot+"\n **4");
				}
			}
			if(mot.charAt(i) == ':' && mot.charAt(i+1) != '=' && mot.charAt(i+1) != ' ')
			{
				String str1 = mot.substring(0, i+1);
				String str2 = mot.substring(i+1, mot.length());
				mot = str1 +" "+str2;
			}
			if(mot.charAt(i) == ':' && mot.charAt(i-1) != '=' && mot.charAt(i-1) != ' ')
			{
				String str1 = mot.substring(0, i);
				String str2 = mot.substring(i, mot.length());
				mot = str1 +" "+str2;
			}
			
			i++;
		}
		return mot;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		ArrayList<Symbole> tableSymboles = new ArrayList<Symbole>();
		AnalyseurLexical ANL = new AnalyseurLexical();
		
		//System.out.println(addSpace(Jtext.getText()));
		String code = deleteComment(Jtext.getText());
		ANL.executerAnalyse(addSpace(code),tableSymboles);
		
		Jlabel.setText(ANL.ErreurMsg);
		
		if(ANL.ErreurMsg.equals("")){
			AnalyseurSyntaxique ANS = new AnalyseurSyntaxique();
			ANS.executerANS(ANL.motLit);
			Jlabel.setText(ANS.msgErreur);
			if(ANS.msgErreur.equals("")){
				AnalyseurSemantique ANSe = new AnalyseurSemantique();
				ANSe.executeANS(ANL.motLit, tableSymboles);
				Jlabel.setText(ANSe.ErreurMsg);
			}
		}
			
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		/*
		if(e.getKeyChar() == ' '|| e.getKeyChar() == '\n')
		{
			String str  = Jtext.getText();
			String tab[] = str.split(" |\\n");
			if(ANL.isMotCle(tab[tab.length-1])){
				
				String subStr = Jtext.getText().substring(0,Jtext.getText().lastIndexOf(tab[tab.length-1]));
				String subStrEnd = Jtext.getText().substring(Jtext.getText().lastIndexOf(tab[tab.length-1]),Jtext.getText().length());
				String result = subStr + "<font color=red>" + subStrEnd+"</font>";
				
			}
		}*/
		
	}

}
