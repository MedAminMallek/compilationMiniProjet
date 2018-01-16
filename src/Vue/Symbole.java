package Vue;

public class Symbole {
	
	private String typeL;
	private String nom;
	private String type;
	private String valeur;
	
	
	public Symbole(String typeL,String nom, String type, String valeur) {
		this.typeL = typeL;
		this.nom = nom;
		this.type = type;
		this.valeur = valeur;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symbole other = (Symbole) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	public String getTypeL() {
		return typeL;
	}

	public void setTypeL(String typeL) {
		this.typeL = typeL;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValeur() {
		return valeur;
	}
	public void setValeur(String valeur) {
		this.valeur = valeur;
	}
	

}
