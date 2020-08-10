package entrophy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Row {

	public String className;

	public String[] attributes;

	public String[] header;

	public static Row create(String line, boolean first) {
		Row row = new Row();
		String[] data = line.split(",");
		int length = data.length;
		row.className = first ? data[0] : data[length - 1];
		if (!first)
			row.attributes = Arrays.copyOfRange(data, 0, length - 1);
		else
			row.attributes = Arrays.copyOfRange(data, 1, length);
		row.header = ID3.header;
		return row;
	}

	public static boolean isContinue(List<Row> list) {
		String className = list.get(0).className;
		for (Row row : list) {
			if (!row.className.equals(className))
				return true;
		}
		return false;
	}

	public String toString() {
		return String.format("class name: %s - %s \n", className, Arrays.toString(attributes));
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	public static double computeEntrophy(List<Row> attributes) {
		Map<String, Float> map = new HashMap<>();
		attributes.forEach(attr -> {
			Float count = map.getOrDefault(attr.className, 0f) + 1;
			map.put(attr.className, count);
		});
		float total = attributes.size();
		float entro = 0f;
		for (Float data : map.values()) {
			double num = data / total;
			entro += -(num * Utility.log2(num));
		}
		return entro;
	}

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
			entro += -(num * Utility.log2(num));
		}
		return entro;
	}
	
	public float[] toFloat() {
		float[] data = new float[attributes.length];
		for(int innerI = 0; innerI < attributes.length; innerI++) {
			data[innerI] = Float.parseFloat(attributes[innerI]);
		}
		return data;
	}

	
}