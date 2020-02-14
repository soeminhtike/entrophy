package entrophy;

import java.util.List;
import java.util.Map;

public class Data {
	Map<String, List<Row>> map;
	// String attributeName;
	String name;
	double entrophy;
	double gain;

	public Data(String name, double entrophy, Map<String, List<Row>> map) {
		this.name = name;
		this.entrophy = entrophy;
		this.map = map;
	}
}
