package entrophy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class ID3 {

	public Branch tree = new Branch("##");

	private PrintWriter pw;

	public ID3() throws FileNotFoundException {
		pw = new PrintWriter(new File("operation.log"));
	}

	// public static final String[] header = { "Outlook", "Temperature", "Humidity",
	// "Wind" };

	public static final String[] header = { "Class", "Repeat", "Attendance", "Difficulty", "Q1", "Q2", "Q3", "Q4", "Q5",
			"Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15", "Q16", "Q17", "Q18", "Q19", "Q20", "Q21",
			"Q22", "Q23", "Q24", "Q25", "Q26", "Q27", "Q28" };

	public static void main(String[] args) throws Exception {
		ID3 id3 = new ID3();
		List<Row> dataRows = Utility.parseCSV("test2.csv");
		id3.createTree(dataRows, id3.tree, "entry");
		// System.out.println("----------------------------------------------------------------------------");
		Utility.print(id3.tree);
		id3.pw.flush();
		id3.pw.close();
		List<String> ruleList = Utility.exportRule(id3.tree.branch.get(0));
		for(String rule : ruleList) {
			System.out.println(rule);
		}
	}

	public void createTree(List<Row> attributes, Branch tree, String criteria) {
		Branch branch = new Branch("");
		branch.level = tree.level + 1;
		branch.criteria = criteria;
		tree.branch.add(branch);
		if (attributes == null || !Row.isContinue(attributes)) { // same class
			branch.name = attributes.get(0).className;
			return;
		}
		double systemEntrophy = Row.computeEntrophy(attributes);
		pw.write(String.format("System entrophy of %s :%s\n", criteria, systemEntrophy));
		List<Data> dataList = new ArrayList<>();
		pw.write("\n----------------------\n");
		for (int i = 0; i < attributes.get(0).getAttributes().length; i++) {
			Data data = classified(i, attributes, systemEntrophy);
			dataList.add(data);
			pw.write(String.format("%s: %s", data.name, data.entrophy));
			pw.write("\n");
		}
		pw.write(String.format("Branches :%s\n", dataList.size()));

		pw.write("\n");
		double result = Double.MIN_VALUE;
		Data selectedAttribute = null;
		for (Data data : dataList) {
			if (data.entrophy > result) {
				result = data.entrophy;
				selectedAttribute = data;
			}
		}

		pw.write("--------------------------------------------------------\n");
		if (selectedAttribute == null) {
			System.out.println("null dected.");
			createTree(rows, branch, "Zero");
			return;
		}
		pw.write("selected :" + selectedAttribute.name + " <<<<\n");
		pw.write("--------------------------------------------------------\n");
		branch.name = selectedAttribute.name;
		for (Entry<String, List<Row>> entry : selectedAttribute.map.entrySet()) {
			String name = entry.getKey();
			List<Row> rows = entry.getValue();
			createTree(rows, branch, name);
		}
	}

	public Data classified(int index, List<Row> list, double systemEntrophy) {
		Map<String, List<Row>> map = new HashMap<>();
		// group by key
		String header2 = list.get(0).header[index];
		for (Row row : list) {
			String key = row.getAttributes()[index];
			List<Row> rows = map.get(key);

			if (rows == null) {
				rows = new ArrayList<>();
				map.put(key, rows);
			}
			// System.out.println("input :" + row );
			Row newRow = Utility.createCreateRow(row, index);
			rows.add(newRow);
			// System.out.println("result :" + newRow+" >>" +
			// Arrays.toString(newRow.header));
			// System.out.println("");
		}
		// System.out.println(map.size());
		// String header = map.values().iterator().next().get(0).header[index];
		for (Entry<String, List<Row>> entry : map.entrySet()) {
			double entrophy = Row.computeEntrophy(entry.getValue());
			double size = entry.getValue().size();
			systemEntrophy = systemEntrophy - ((size / list.size()) * entrophy);
		}

		return new Data(header2, systemEntrophy, map);

	}

}