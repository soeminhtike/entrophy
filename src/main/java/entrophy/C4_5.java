package entrophy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class C4_5 extends ID3 {
	
	// private static Logger logger = Logger.getLogger(C4_5.class);

	public C4_5() throws FileNotFoundException {
		PropertyConfigurator.configure(new FileInputStream(new File("src/test/resources/log4j.properties")));
	}

	public static void main(String[] args) throws Exception {
		C4_5 c45 = new C4_5();
		List<Row> dataRows = Utility.parseCSV("test3.csv");
		c45.createTree(dataRows, c45.tree, "entry");
		//Utility.print(c45.tree);
		Utility.exportTreeJson(c45.tree);
		Utility.dividedData("test2.csv", Utility.exportRules(c45.tree.branch.get(0)));
	}

	public Data classified(int index, List<Row> list, double systemEntrophy) {
		String header2 = list.get(0).header[index];
		double totalRow = list.size();
		Map<String, List<Row>> map = new HashMap<>();

		groupData(list, index, map);
		double gain = 0;
		double splitInfo = 0d;
		for (Entry<String, List<Row>> entry : map.entrySet()) {
			List<Row> attributes = entry.getValue();
			double subEntrophy = calculateEntrophy(attributes, totalRow);
			gain = gain + subEntrophy;
			double temp = attributes.size() / totalRow;
			splitInfo += temp * Utility.log2(temp);
		}
		splitInfo = -splitInfo;
		Data data = new Data(header2, (systemEntrophy - gain) / splitInfo, map);

		return data;
	}

	protected Data selected(List<Data> dataList) {
		Double result = Double.MIN_VALUE;
		Data selectedAttribute = null;
		//not require only for log purpose
		dataList.sort((ob1, ob2) -> {
			return Double.compare(ob2.entrophy, ob1.entrophy);
		});
		
		
		for (Data data : dataList) {
			if(Double.isInfinite(data.entrophy))
				continue;
			
			if (data.entrophy > result) {
				result = data.entrophy;
				selectedAttribute = data;
			}
		}
		//System.out.println("selected entrophy :" + selectedAttribute.entrophy);
		// System.exit(0);
		return selectedAttribute;
	}

	private double calculateEntrophy(List<Row> attributes, double max) {
		Map<String, Float> map = new HashMap<>();

		attributes.forEach(attr -> {
			Float count = map.getOrDefault(attr.className, 0f) + 1;
			map.put(attr.className, count);
		});

		double result = 0;
		for (float val : map.values()) {
			float p = val / attributes.size();
			result += -p * Utility.log2(p);
		}
		return attributes.size() / max * result;
	}
}
