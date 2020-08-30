package entrophy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import entrophy.Rule.Matcher;

public class Utility {

	private static final boolean applyNumeric = true;

	private static boolean manualMean = true;

	public static final int dataLength = 32;

	public static int[] means = {7, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};

	// location of class name
	private static final boolean first = false;

	// private static Logger logger = Logger.getLogger(Utility.class);

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
		System.out.println(String.format("[%s:%s]  ", tree.criteria, tree.name, tree.branch.size(), tree.level));
		if (!tree.branch.isEmpty()) {
			for (Branch branch : tree.branch) {
				print(branch);
			}
		}
	}

	public static void exportTreeJson(Branch tree) {
		String test = tree.toJSonString();
		System.out.println(test.substring(1, test.length() - 1));
	}

	public static List<Row> parseCSV(String fileName, boolean applyNumeric) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			// logger.error("Can't located file ", e);
			return new ArrayList<>();
		}

		String line;
		List<Row> rows = new ArrayList<>();
		int lineCount = 0;
		try {
			line = br.readLine();
			if (isHeader(line))
				updateHeader(line);
			else
				lineCount = createRow(line, rows, lineCount);

			while ((line = br.readLine()) != null) {
				lineCount = createRow(line, rows, lineCount);
			}
			br.close();
		} catch (IOException e) {
			// logger.error("Can't close reader", e);
			System.out.println("Can't close reader");
			e.printStackTrace();
		}
		System.out.println("Total line :" + lineCount);
		if (applyNumeric)
			applyNumeric(rows);
		return rows;
	}

	private static int createRow(String line, List<Row> rows, int lineCount) {
		Row row = parseLine(line);
		if (row == null)
			return lineCount;
		rows.add(Row.create(line, first));
		System.out.println(" line count :" + lineCount);
		return ++lineCount;
	}

	private static boolean isHeader(String line) {
		return line.startsWith("#");
	}

	private static boolean updateHeader(String headerLine) {
		if (!headerLine.startsWith("#"))
			return false;
		String[] rawHeader = headerLine.split(",");
		ID3.header = new String[rawHeader.length - 1];
		for (int i = 0; i < rawHeader.length - 1; i++) {
			ID3.header[i] = rawHeader[i];
		}
		ID3.className = rawHeader[rawHeader.length - 1];
		return true;
	}

	private static Row parseLine(String line) {
		if (line.startsWith("#") || line.trim().equals("")) {
			return null;
		}
		Row row = Row.create(line, first);
		row.header = ID3.header;
		return row;
	}

	public static void applyNumeric(List<Row> rows) {
		applyMinMaxMean(rows);
		// applyDefaultMean(rows);
	}

	public static Row cloneRow(Row original) {
		Row row = new Row();
		row.className = original.className;
		row.attributes = Arrays.copyOf(original.attributes, original.attributes.length);
		row.header = Arrays.copyOf(original.header, original.header.length);
		return row;
	}

	private static void applyMinMaxMean(List<Row> rows) {
		means = manualMean ? means : calculateMeans(rows);
		for (int i = 0; i < rows.size(); i++) {
			applyMean(rows.get(i), means);
		}
	}

	private static void applyMean(Row row, int[] means) {
		for (int i = 0; i < row.attributes.length; i++) {
			row.attributes[i] = means[i] >= Integer.parseInt(row.attributes[i]) ? "1" : "2";
		}
	}

	private static int[] calculateMeans(List<Row> rows) {
		int sum[][] = new int[rows.get(0).getAttributes().length][2];

		rows.parallelStream().forEach(row -> {
			for (int i = 0; i < row.attributes.length; i++) {
				int num = Integer.parseInt(row.attributes[i]);
				if (num > sum[i][1]) { // maximum
					sum[i][1] = num;
				}
				if (num < sum[i][0]) // minimum
					sum[i][0] = num;
			}
		});

		int[] means = new int[sum.length];

		for (int i = 0; i < means.length; i++) {
			means[i] = (sum[i][0] + sum[i][1]) / 2;
		}
		return means;
	}

	private static void applyDefaultMean(List<Row> rows) {
		int sum[] = new int[rows.get(0).getAttributes().length];

		for (Row row : rows) {
			for (int i = 0; i < row.attributes.length; i++) {
				sum[i] += Integer.parseInt(row.attributes[i]);
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

	public static List<String> groupRowDataList(Collection<Rule> rules, Row row) {
		List<String> name = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.isMatch(row)) {
				name.add(row.className);
			}
		}
		// System.exit(0);
		return name;
	}

	public static void groupRowDataMap(Map<String, List<Row>> name, Collection<Rule> rules, Row row) {
		for (Rule rule : rules) {
			if (rule.isMatch(row)) {
				List<Row> rowList = name.get(row.className);
				if (rowList == null) {
					rowList = new ArrayList<>();
					name.put(row.className, rowList);
				}
				rowList.add(row);
			}
		}
	}

	private static void addHeader(PrintWriter pw) {
		StringBuffer buffer = new StringBuffer();
		for (String header : ID3.header) {
			buffer.append(header + ",");
		}
		pw.write(buffer.toString() + ID3.className + "\n");
	}

	public static File partitionData(String fileName, Collection<Rule> rules, boolean applyNumeric) {
		rules.parallelStream().forEach(Rule::prepare);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			File file = new File(ID3.target + "c45.csv");
			PrintWriter pw = new PrintWriter(file);
			addHeader(pw);
			PrintWriter linearRegressionFile = new PrintWriter(new File(ID3.target + "linearregression.csv"));
			int i = 0;
			while ((line = reader.readLine()) != null) {
				Row originalDataRow = parseLine(line);
				if (originalDataRow == null)
					continue;
				Row clone = null;
				if (applyNumeric) {
					clone = cloneRow(originalDataRow);
					applyMean(clone, means);
				} else {
					clone = originalDataRow;
				}
				// List<Rule> matchRule = new ArrayList<>();
				boolean status = false;
				i++;
				for (Rule rule : rules) {
					if (rule.isMatch(clone)) {
						status = true;
						writeRow(pw, originalDataRow);
						break;
					}
				}
				if (!status) {
					// System.out.println("not match :" + originalDataRow);
					addToLinearRegressionDataFile(originalDataRow, linearRegressionFile);
				}
			}
			reader.close();
			pw.close();
			linearRegressionFile.close();
			return file;
			// writerMap.values().forEach(PrintWriter::close);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}

	private static void addToLinearRegressionDataFile(Row row, PrintWriter pw) {
		writeRow(pw, row);
	}

	public static SimpleMatrix toSimpleMatrix(List<Row> rows) {
		return new SimpleMatrix(toFloat(rows));
	}

	private static float[][] toFloat(List<Row> rows) {
		float[][] rawData = new float[rows.size()][];
		for (int i = 0; i < rows.size(); i++) {
			rawData[i] = rows.get(i).toFloat();
		}
		return rawData;
	}

	public static List<String> dividedData(String fileName, Collection<Rule> rules, boolean applyNumeric) {
		rules.parallelStream().forEach(Rule::prepare);
		// .collect(Collectors.toList());
		Map<String, PrintWriter> writerMap = new HashMap<>();
		List<String> fileNames = new ArrayList<>();
		try {
			for (Rule rule : rules) {
				if (!writerMap.containsKey(rule.id)) {
					rule.id = Long.toHexString(System.nanoTime());
					File file = new File("output/" + rule + ".csv");
					writerMap.put(rule.id, new PrintWriter(file));
					fileNames.add(file.getAbsolutePath());
				}
			}

			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String line;
			while ((line = reader.readLine()) != null) {
				Row originalDataRow = parseLine(line);
				if (originalDataRow == null)
					continue;
				Row clone = null;
				if (applyNumeric) {
					clone = cloneRow(originalDataRow);
					applyMean(clone, means);
				} else {
					clone = originalDataRow;
				}
				List<Rule> matchRule = new ArrayList<>();
				for (Rule rule : rules) {
					if (rule.isMatch(clone)) {
						matchRule.add(rule);
					}

				}
				// System.out.println("header :"
				// +Arrays.toString(clone.header));
				// System.out.println("Match rule size :" + matchRule.size()+"
				// >>" + clone);
				// System.exit(0);
				writeToFile(matchRule, originalDataRow, writerMap);

			}
			reader.close();
			writerMap.values().forEach(PrintWriter::close);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return fileNames;
	}

	private static void writeToFile(List<Rule> rules, Row row, Map<String, PrintWriter> writer) {
		for (Rule rule : rules) {
			PrintWriter pw = writer.get(rule.id);
			int length = row.attributes.length;
			for (int i = 0; i < length; i++)
				pw.write(row.attributes[i] + ",");

			pw.write(row.className + "\n");
		}
	}

	private static void writeRow(PrintWriter pw, Row row) {
		int length = row.attributes.length;
		for (int i = 0; i < length; i++)
			pw.write(row.attributes[i] + ",");

		pw.write(row.className + "\n");

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

	public static boolean isPure(Rule rule) {

		return false;
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
