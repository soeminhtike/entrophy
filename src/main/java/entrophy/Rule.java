package entrophy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Rule {

	private static Logger logger = Logger.getLogger(Rule.class);
	 
	List<Matcher> constraints;

	Map<String, String> constrainMap;

	public String name;

	public Rule(String name) {
		this.constraints = new ArrayList<>();
		this.name = name;
	}

	public void buildMap() {
		constrainMap = new HashMap<>();
		for (Matcher matcher : constraints) {
			constrainMap.put(matcher.key, matcher.value);
		}
	}

	public void add(Branch branch) {
		Matcher matcher = new Matcher(branch.name, branch.criteria);
		constraints.add(matcher);
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
				//logger.info(String.format("%s,%s == %s,%s", header, rowValue, header, value));
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
