package entrophy;

import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class MatrixUtil {

	public static SimpleMatrix[] loadMatrix(String path) {
		List<Row> rowList = Utility.parseCSV(path, false);
		float[][] data = toMatrix(rowList);
		SimpleMatrix result[] = new SimpleMatrix[2];
		result[0] = new SimpleMatrix(data);
		result[1] = new SimpleMatrix(extractY(rowList));
		return result;
	}

	public static void printMatrix(SimpleMatrix matrix) {
		int row = matrix.numRows();
		int column = matrix.numCols();
		System.out.println(row + " x " + column);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				// System.out.print(String.format("%5s, ", matrix.get(i, j)));
			}
			System.out.println(" ..");
		}
	}
	
	private static float[][] extractY(List<Row> rowList) {
		// int size = rowList.get(0).attributes.length + 1;
		float[][] number = new float[rowList.size()][1];

		for(int i = 0; i < number.length; i++) {
			number[i][0] = Integer.parseInt(rowList.get(i).getClassName());
		}
		return number;
	}
	
	public static double[] toArray(SimpleMatrix vector) {
		double[] b = new double[vector.numRows()];
		for(int i=0;i<vector.numRows();i++) {
			b[i]= vector.get(i,0);
		}
		return b;
	}
	
	public static double estimateY(double[] b, Row row) {
		double y = b[0];
		for(int i=1;i<b.length;i++) {
			//System.out.println(i+" >> " + row);
			y += Double.parseDouble(row.attributes[i-1]) * b[i];
		}
		return y;
	}

	private static float[][] toMatrix(List<Row> rowList) {
		int size = rowList.get(0).attributes.length + 1;
		float[][] number = new float[rowList.size()][size];

		for (int i = 0; i < number.length; i++) {
			number[i] = toMaxtrixColumn(rowList.get(i).attributes, size);
		}
		return number;
	}

	private static float[] toMaxtrixColumn(String[] numberStr, int size) {
		float[] number = new float[size];
		number[0] = 1;
		for (int i = 0; i < size - 1; i++) {
			number[i + 1] = Float.parseFloat(numberStr[i]);
		}
		return number;
	}
}
