package entrophy;

import java.util.ArrayList;
import java.util.List;

public class Branch {
	String name;
	String criteria;
	List<Branch> branch;
	int level = 0;
	
	String temp;

	public Branch(String name) {
		this.name = name;
		branch = new ArrayList<>();
	}

	public Branch create(Data data) {
		Branch leaf = new Branch(data.name);
		leaf.branch = new ArrayList<>();

		this.branch.add(leaf);
		return leaf;
	}

	public String toString() {
		return name;
	}

}
