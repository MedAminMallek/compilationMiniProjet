package Vue;

public class Regle {
	
	private int code;
	private String gauche;
	private String droite;
	
	Regle(int c,String g,String d)
	{
		code = c;
		gauche = g;
		droite = d;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getGauche() {
		return gauche;
	}
	public void setGauche(String gauche) {
		this.gauche = gauche;
	}
	public String getDroite() {
		return droite;
	}
	public void setDroite(String droite) {
		this.droite = droite;
	}
	
	

}
