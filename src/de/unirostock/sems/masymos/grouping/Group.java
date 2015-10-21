package de.unirostock.sems.masymos.grouping;

public class Group {
	
	//private Long databseID;
	private String term;
	//private LinkedList<String> concepts;
	private double Score;
	
	
	public Group(String term, double score) {
		super();
		this.term = term;
		Score = score;
	}
	
/*	public Long getDatabseID() {
		return databseID;
	}
	public void setDatabseID(Long databseID) {
		this.databseID = databseID;
	}*/
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	
/*	public LinkedList<String> getConcepts() {
		return concepts;
	}
	public void setConcepts(LinkedList<String> concepts) {
		this.concepts = concepts;
	}*/
	
	public double getScore() {
		return Score;
	}
	public void setScore(double score) {
		Score = score;
	}

/*	public boolean equals(Group gr){
		if (this.getTerm().equals(gr.getTerm())){
			return true;
		}
		else {
			return false;
		}
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(Score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (Double.doubleToLongBits(Score) != Double
				.doubleToLongBits(other.Score))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
	
	
}
