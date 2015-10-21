package de.unirostock.sems.masymos.grouping;

import java.util.LinkedList;

import de.unirostock.sems.masymos.configuration.Property;

public class GroupList {

	private LinkedList<Group> groups;

	public GroupList() {
		this.setGroups(new LinkedList<Group>());
	}
	
	public GroupList(LinkedList<Group> groups) {
		this.setGroups(groups);
	}

	public LinkedList<Group> getGroups() {
		return groups;
	}

	public void setGroups(LinkedList<Group> groups) {
		this.groups = groups;
	}

	public void printGroups() {
		System.out.println("---Groups:---");
		for (int i = 0; i < getGroups().size(); i++) {
			System.out.println("ID: " + getGroups().get(i).getTerm());
			System.out.println("Score: " + getGroups().get(i).getScore());
			System.out.println("...........");
		}
	}

	public Group getMaxByScore() {
		Group max = getGroups().getFirst();
		for (int i = 0; i < getGroups().size(); i++) {
			if (getGroups().get(i).getScore() > max.getScore()) {
				max = getGroups().get(i);
			}
		}
		return max;
	}

	public void getNMaxByScore(int n) {
		while (groups.size() > n) {
			groups.remove(getMinByScore());
		}
	}

	public void eliminateDuplicates() {
		LinkedList<Group> grList = new LinkedList<Group>();
		
		for (int i = 0; i < getGroups().size(); i++) {
			if (!grList.contains(getGroups().get(i))){
				grList.add(getGroups().get(i));
			}
		}
		setGroups(grList);
	}

	public void eliminateNonused() {
		LinkedList<Group> grList = new LinkedList<Group>();
		
		for (int i = 0; i < getGroups().size(); i++) {
			if ((getGroups().get(i)).getScore()>0){
				grList.add(getGroups().get(i));
			}
		}
		setGroups(grList);
	}
	public Group getMinByScore() {
		Group min = getGroups().getFirst();
		for (int i = 0; i < getGroups().size(); i++) {
			if (getGroups().get(i).getScore() < min.getScore()) {
				min = getGroups().get(i);
			}
		}
		return min;
	}
	
	public Group getMaxByDepth() {
		Group max = getGroups().getFirst();
		for (int i = 0; i < getGroups().size(); i++) {
			
			int depthOfCurrentObject = Integer.parseInt(UGroupingUtil.getNodebyTermID(getGroups().get(i).getTerm()).getProperty(Property.Ontology.depth).toString());
			int currentMaxDepth = Integer.parseInt(UGroupingUtil.getNodebyTermID(max.getTerm()).getProperty(Property.Ontology.depth).toString());

			if (depthOfCurrentObject > currentMaxDepth) {
				max = getGroups().get(i);
			}
		}
		return max;
	}
	
	public int getMaxDepth() {
		Group maxNode = getGroups().getFirst();
		int max = 0;
		for (int i = 0; i < getGroups().size(); i++) {
			
			int depthOfCurrentObject = Integer.parseInt(UGroupingUtil.getNodebyTermID(getGroups().get(i).getTerm()).getProperty(Property.Ontology.depth).toString());
			int currentMaxDepth = Integer.parseInt(UGroupingUtil.getNodebyTermID(maxNode.getTerm()).getProperty(Property.Ontology.depth).toString());

			if (depthOfCurrentObject > currentMaxDepth) {
				max = depthOfCurrentObject;
			}
		}
		return max;
	}

	public GroupList getNodesOfDepth(int depth) {
		GroupList grL = new GroupList();
		for (int i = 0; i < getGroups().size(); i++) {
			
			int depthOfCurrentObject = Integer.parseInt(UGroupingUtil.getNodebyTermID(getGroups().get(i).getTerm()).getProperty(Property.Ontology.depth).toString());

			if (depthOfCurrentObject == depth) {
				grL.getGroups().add(this.getGroups().get(i));
			}
		}
		return grL;
	}
	
	public void makeDisjunct(){
		@SuppressWarnings("unchecked")
		LinkedList<Group> grList = (LinkedList<Group>) getGroups().clone();
		for (int i1=0; i1<getGroups().size()-1; i1++){
			
			Group gr = getGroups().get(i1);
			
			for (int i2=i1+1;i2<getGroups().size();i2++){
				if ((UGroupingUtil.isAncestor(UGroupingUtil.getNodebyTermID(gr.getTerm()),UGroupingUtil.getNodebyTermID(getGroups().get(i2).getTerm())))||(UGroupingUtil.isAncestor(UGroupingUtil.getNodebyTermID(getGroups().get(i2).getTerm()),UGroupingUtil.getNodebyTermID(gr.getTerm())))){
					if(gr.getScore()>getGroups().get(i2).getScore()){
						grList.remove(getGroups().get(i2));
					

					}
							else {
								grList.remove(gr);


					}
				}
						
			}	
		}
		setGroups(grList);
	}

	

}
