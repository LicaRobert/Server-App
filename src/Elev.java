import org.json.simple.JSONObject;

public class Elev {
	private int id;
	private String nume;
	private String prenume;
	public int varsta;

	public Elev() {

	}

	public Elev(JSONObject json) {
		id = ((Long) json.get("id")).intValue();
		nume = (String) json.get("nume");
		prenume = (String) json.get("prenume");
		varsta = ((Long) json.get("varsta")).intValue();
	}

	public String getXML() {
		return "<elev id=\"" + id + "\"><nume>" + nume + "</nume><prenume>" + prenume + "</prenume><varsta>" + varsta
				+ "</varsta></elev>";
	}

	public void setNume(String nume) {
		this.nume = nume;
	}

	public String getNume() {
		return nume;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrenume() {
		return prenume;
	}

	public void setPrenume(String prenume) {
		this.prenume = prenume;
	}

	public int getVarsta() {
		return varsta;
	}

	public void setVarsta(int varsta) {
		this.varsta = varsta;
	}

	public JSONObject toJson() {
		JSONObject jsonElev = new JSONObject();
		jsonElev.put("id", this.getId());
		jsonElev.put("nume", this.getNume());
		jsonElev.put("prenume", this.getPrenume());
		jsonElev.put("varsta", this.getVarsta());
		return jsonElev;
	}

}
// public class Elev {
// private int id;
// private String nume;
// private String prenume;
// private int nrNote;
// private double media;
// private Vector<Integer> note;
// public int varsta;
//
// public Elev()
// {
// note = new Vector<Integer>();
// }
//
// public Elev(int _nrNote)
// {
// nrNote = _nrNote;
// note = new Vector<Integer>();
// }
//
// public String getXML()
// {
// return "<elev
// id="+id+"><nume>"+nume+"</nume><prenume>"+prenume+"</prenume><varsta>"+varsta+"</varsta></elev>";
// }
//
// public void setNume(String nume)
// {
// //proceseaza nume
// //nume = String.format("%s!", nume);
// this.nume = nume;
// }
//
// public String getNume()
// {
// return nume;
// }
//
// public Double getMedia()
// {
// return media;
// }
//
// public void PromoveazaExamen(int nota)
// {
// note.addElement(nota);
// }
//
// public void determinaMedia()
// {
// double suma = 0;
// for(Integer nota : note)
// {
// suma += nota;
// }
//
// if(note.size() > 0)
// {
// media = suma / note.size();
// }
//
// else
// {
// media = 0;
// }
// }
//
// }
