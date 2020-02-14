package entrophy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Utility {

	private static Logger logger = Logger.getLogger(Utility.class);

	private static final boolean applyNumeric = true;

	private static boolean manualMean = true;

	public static int[] means = {7, 3, 4, 3};

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
		System.out.println(String.format("[%s:%s]  ", tree.criteria, tree.name,
				tree.branch.size(), tree.level));
		if (!tree.branch.isEmpty()) {
			for (Branch branch : tree.branch) {
				print(branch);
			}
		}
	}

	public static List<Row> parseCSV(String fileName)
			throws FileNotFoundException, IOException {
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
				row.attributes[i] = means[i] >= Integer
						.parseInt(row.attributes[i]) ? "1" : "2";
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
				row.attributes[i] = mean[i] >= Integer
						.parseInt(row.attributes[i]) ? "1" : "2";
			}
		}
	}

	public static List<String> exportRule(Branch branch) {
		List<String> ruleList = new ArrayList<>();
		export(branch, "", ruleList);
		return ruleList;
	}

	public static void export(Branch root, String prefix,
			List<String> ruleList) {
		if (root.branch.isEmpty()) {
			ruleList.add(String.format("(%s)= %s",
					prefix.substring(0, prefix.length() - 1), root.name));

		} else
			for (Branch child : root.branch) {
				String format = prefix.equals("") ? "%s" : "%s and ";
				export(child, String.format(format + "(%s=%s) ", prefix,
						root.name, child.criteria), ruleList);
			}
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
