package entrophy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.PropertyConfigurator;
import org.ejml.simple.SimpleMatrix;

class ID3 {

	public Branch tree = new Branch("##");

	public ID3() throws FileNotFoundException {
		PropertyConfigurator.configure(new FileInputStream(new File("src/test/resources/log4j.properties")));
	}

	// @formatter:off
	public static String[] header ; //{ "Class", "Repeat", "Attendance", "Difficulty", "Q1", "Q2", "Q3", "Q4", "Q5",
		//	"Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15", "Q16", "Q17", "Q18", "Q19", "Q20", "Q21",
		//	"Q22", "Q23", "Q24", "Q25", "Q26", "Q27", "Q28" };
	// @formatter:on

	public static final String sourceFile = "test2.csv";

	public static final String target = "output/";
	

	// TODO
	public static void main(String[] args) throws Exception {
		// List<Row> datas = Utility.parseCSV(sourceFile, true);
		ID3 id3 = new ID3();
		List<Row> dataRows = Utility.parseCSV(sourceFile, true);
		id3.createTree(dataRows, id3.tree, "entry");
		System.out.println("Header name :" + Arrays.toString(ID3.header));
		Collection<Rule> ruleList = Utility.exportRules(id3.tree.branch.get(0));
		Utility.exportTreeJson(id3.tree.branch.get(0));
		ruleList = ruleList.parallelStream().filter(Rule::isPure).collect(Collectors.toList());

		File file = Utility.partitionData(sourceFile, ruleList, true);
		C4_5 c45 = new C4_5();
		c45.createTree(Utility.parseCSV(file.getAbsolutePath(), true), c45.tree, "entry");
		Utility.exportTreeJson(c45.tree.branch.get(0));
	}

	public void createTree(List<Row> attributes, Branch tree, String criteria) {
		Branch branch = new Branch("");
		branch.level = tree.level + 1;
		branch.criteria = criteria;
		tree.branch.add(branch);
		if (attributes == null || !Row.isContinue(attributes)) { // same class
			branch.name = attributes == null ? criteria + " " : attributes.get(0).className;
			branch.criteria = criteria;
			return;
		}
		double systemEntrophy = Row.computeEntrophy(attributes);
		List<Data> dataList = new ArrayList<>();

		for (int i = 0; i < attributes.get(0).getAttributes().length; i++) {
			Data data = classified(i, attributes, systemEntrophy);
			dataList.add(data);
		}
		Data selectedAttribute = selected(dataList);

		if (selectedAttribute == null) {
			createTree(null, branch, "NULL");
			return;
		}

		branch.name = selectedAttribute.name;
		for (Entry<String, List<Row>> entry : selectedAttribute.map.entrySet()) {
			String name = entry.getKey();
			List<Row> rows = entry.getValue();
			createTree(rows, branch, name);
		}
	}

	protected Data selected(List<Data> dataList) {
		double result = Double.MIN_VALUE;
		Data selectedAttribute = null;
		for (Data data : dataList) {
			if (data.entrophy > result) {
				result = data.entrophy;
				selectedAttribute = data;
			}
		}
		return selectedAttribute;
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
			Row newRow = Utility.createCreateRow(row, index);
			rows.add(newRow);
		}
		for (Entry<String, List<Row>> entry : map.entrySet()) {
			double entrophy = Row.computeEntrophy(entry.getValue());
			double size = entry.getValue().size();
			systemEntrophy = systemEntrophy - ((size / list.size()) * entrophy);
		}

		return new Data(header2, systemEntrophy, map);

	}

	protected void groupData(List<Row> list, int index, Map<String, List<Row>> map) {
		for (Row row : list) {
			String key = row.getAttributes()[index];
			List<Row> rows = map.get(key);

			if (rows == null) {
				rows = new ArrayList<>();
				map.put(key, rows);
			}
			Row newRow = Utility.createCreateRow(row, index);
			rows.add(newRow);
		}
	}

}
