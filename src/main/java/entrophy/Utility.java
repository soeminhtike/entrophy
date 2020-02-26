package entrophy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import entrophy.Rule.Matcher;

public class Utility {

	private static Logger logger = Logger.getLogger(Utility.class);

	private static final boolean applyNumeric = false;

	private static boolean manualMean = true;

	public static int[] means = {7, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3}; // {7,
																																	// 3,
																																	// 4,
																																	// 3};

	// location of class name
	private static final boolean first = false;

	public static double computeSystemEntrophy(List<Row> attributes) {
		Map<String, Float> map = new HashMap<>();
		attributes.forEach(attr -> {
			Float count = map.getOrDefault(attr.className, 0f) + 1;
			map.put(attr.className, count);
		});
		float total = attributes.size();
		float entro = 0f;
		for (Float data : map.values()) {
			double num = data / total;
			entro += -(num * log2(num));
		}
		return entro;
	}

	public static double log2(double num) {
		return Math.log(num) / Math.log(2d);
	}

	public static void print(Branch tree) {
		for (int i = 0; i < tree.level; i++) {
			System.out.print("--");
		}
		System.out.println(String.format("[%s:%s]  ", tree.criteria, tree.name, tree.branch.size(), tree.level));
		if (!tree.branch.isEmpty()) {
			for (Branch branch : tree.branch) {
				print(branch);
			}
		}
	}

	public static void exportTreeJson(Branch tree) {
		String test = tree.toJSonString();
		logger.info(test.substring(1, test.length() - 1));
	}

	public static List<Row> parseCSV(String fileName) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

		String line;
		List<Row> rows = new ArrayList<>();
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#"))
				continue;
			rows.add(Row.create(line, first));
		}
		br.close();

		if (applyNumeric)
			applyNumeric(rows);
		return rows;
	}

	public static void applyNumeric(List<Row> rows) {
		applyMinMaxMean(rows);
		// applyDefaultMean(rows);
	}

	private static void applyMinMaxMean(List<Row> rows) {
		if (!manualMean) {
			int sum[][] = new int[rows.get(0).getAttributes().length][2];

			for (Row row : rows) {
				for (int i = 0; i < row.attributes.length; i++) {
					// sum[i] += Integer.parseInt(row.attributes[i]);
					int num = Integer.parseInt(row.attributes[i]);
					if (num > sum[i][1]) { // maximum
						sum[i][1] = num;
					}
					if (num < sum[i][0]) // minimum
						sum[i][0] = num;

				}
			}

			means = new int[sum.length];

			for (int i = 0; i < means.length; i++) {
				means[i] = (sum[i][0] + sum[i][1]) / 2;
			}
		}
		for (Row row : rows) {
			for (int i = 0; i < row.attributes.length; i++) {
				row.attributes[i] = means[i] >= Integer.parseInt(row.attributes[i]) ? "1" : "2";
			}
		}
	}

	private static void applyDefaultMean(List<Row> rows) {
		int sum[] = new int[rows.get(0).getAttributes().length];

		for (Row row : rows) {
			for (int i = 0; i < row.attributes.length; i++) {
				sum[i] += Integer.parseInt(row.attributes[i]);
				// int num = Integer.parseInt(row.attributes[i]);
				// if (num > sum[i][1]) { // maximum
				// sum[i] = num;
				// }
				// if (num < sum[i][0]) // minimum
				// sum[i][0] = num;

			}
		}

		int mean[] = new int[sum.length];
		for (int i = 0; i < mean.length; i++) {
			mean[i] = sum[i] / rows.get(0).attributes.length;
		}
		for (Row row : rows) {
			for (int i = 0; i < row.attributes.length; i++) {
				row.attributes[i] = mean[i] >= Integer.parseInt(row.attributes[i]) ? "1" : "2";
			}
		}
	}

	public static List<String> exportRuleStr(Branch branch) {
		List<String> ruleList = new ArrayList<>();
		exportStr(branch, "", ruleList);
		return ruleList;
	}

	public static Collection<Rule> exportRules(Branch branch) {
		Map<String, Rule> ruleMap = new HashMap<>(); // new ArrayList<>();
		export(branch, new ArrayList<>(), ruleMap, "");
		return ruleMap.values();
	}

	public static void exportStr(Branch root, String prefix, List<String> ruleList) {
		if (root.branch.isEmpty()) {
			ruleList.add(String.format("(%s)= %s", prefix.substring(0, prefix.length() - 1), root.name));

		} else
			for (Branch child : root.branch) {
				String format = prefix.equals("") ? "%s" : "%s and ";
				exportStr(child, String.format(format + "(%s=%s) ", prefix, root.name, child.criteria), ruleList);
			}
	}

	public static void export(Branch root, List<Matcher> matcherList, Map<String, Rule> ruleMap, String criteria) {
		if (root.branch.isEmpty()) {
			Rule rule = new Rule(root.name);
			rule.constraints = matcherList;
			ruleMap.put(System.nanoTime() + "", rule);
		}

		for (Branch branch : root.branch) {
			Matcher matcher2 = new Matcher(root.name, branch.criteria);
			List<Matcher> newMatcher = new ArrayList<>();
			newMatcher.add(matcher2);
			newMatcher.addAll(matcherList);
			export(branch, newMatcher, ruleMap, branch.criteria);
		}
	}

	public static List<String> groupDataByRow(Collection<Rule> rules, Row row) {
		List<String> name = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.isMatch(row)) {
				name.add(row.className);
			}
		}
		// System.exit(0);
		return name;
	}

	public static void dividedData(String fileName, Collection<Rule> rules) {
		List<Row> rows = null;
		try {
			rows = Utility.parseCSV(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		rules.forEach(Rule::buildMap);
		for (Row row : rows) {
			logger.info(Arrays.deepToString(row.attributes) + " >> " + groupDataByRow(rules, row));
		}
	}

	private static Rule addRule(Map<String, Rule> ruleMap, Branch branch) {
		Rule rule = ruleMap.get(branch.name + branch.criteria);
		if (rule == null) {
			rule = new Rule(branch.name);
			ruleMap.put(branch.name + branch.criteria, rule);
		}
		rule.add(branch);
		return rule;
	}

	public static Row createCreateRow(Row row, int index) {
		String att1[] = new String[row.attributes.length - 1];
		int counter = 0;
		String h[] = new String[att1.length];
		for (int i = 0; i <= att1.length; i++) {
			if (i == index)
				continue;
			h[counter] = row.header[i];
			att1[counter++] = row.attributes[i];

		}

		Row newRow = new Row();
		newRow.setClassName(row.className);
		newRow.setAttributes(att1);
		newRow.header = h;
		return newRow;
	}

}
