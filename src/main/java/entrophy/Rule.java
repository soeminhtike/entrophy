package entrophy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rule {

	List<Matcher> constraints;

	Map<String, String> constrainMap;

	public String name;
	
	public String id;

	public Rule(String name) {
		this.constraints = new ArrayList<>();
		this.name = name;
	}

	public void prepare() {
		constrainMap = new HashMap<>();
		for (Matcher matcher : constraints) {
			constrainMap.put(matcher.key, matcher.value);
		}
	}

	public void add(Branch branch) {
		Matcher matcher = new Matcher(branch.name, branch.criteria);
		constraints.add(matcher);
	}
	
	public boolean isPure() {
		return !name.trim().equals("NULL");
	}

	public String toString() {
		return name + " = " + this.constraints.toString();
	}

	public boolean isMatch(Row row) {
		for (int i = 0; i < row.header.length; i++) {
			String header = row.header[i];
			
			String rowValue = row.attributes[i];
			if (constrainMap.containsKey(header)) {
				String value = constrainMap.get(header);
				if (!value.equals(rowValue))
					return false;
			}
		}
		return true;
	}

	public static class Matcher {
		String key;
		String value;

		public Matcher(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String toString() {
			return String.format("(%s=%s)", key, value);
		}
	}
}
