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

	public String toJSonString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{text:{");
		buffer.append(String.format("name:\"%s\",", name));
		buffer.append(String.format("criteria:\"%s\"", criteria));
		buffer.append("}, children:");
		buffer.append("[");
		StringBuffer temp = new StringBuffer();
		if (!branch.isEmpty()) {
			for (Branch c : branch) {
				temp.append("," + c.toJSonString());
				// buffer.append(c.toJSonString() +",");
			}
			buffer.append(temp.toString().substring(1));
		}
		buffer.append("]");
		buffer.append("}");
		return buffer.toString();
	}

	public static class JSon {
		public String name;
		public String criteria;
		public List<JSon> children;

		public JSon(String name, String criteria) {
			this.name = name;
			this.criteria = criteria;
			children = new ArrayList<>();
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("{");
			buffer.append(String.format("name:\":%s\",", name));
			buffer.append(String.format("criteria:\":%s\"", criteria));
			buffer.append("[");
			for (JSon child : children) {
				buffer.append(child);
			}
			buffer.append("]");
			return buffer.toString();
		}
	}

}
