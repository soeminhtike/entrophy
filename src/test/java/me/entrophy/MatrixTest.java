package me.entrophy;

import java.util.List;

import org.apache.log4j.Logger;
import org.ejml.simple.SimpleMatrix;

import entrophy.Row;
import entrophy.Utility;

public class MatrixTest {

	private static Logger logger = Logger.getLogger(MatrixTest.class);

	public static void main(String[] args) {
		List<Row> rowList = Utility.parseCSV("output/linearregression.csv", false);

		float[][] number = toMatrix(rowList);
		SimpleMatrix matrix = new SimpleMatrix(number);
		SimpleMatrix pseudoInversoMatrix = matrix.pseudoInverse();
		printMatrix(pseudoInversoMatrix);
	}
	
	private static void printMatrix(SimpleMatrix matrix) {
		int row = matrix.numRows();
		int column = matrix.numCols();
		System.out.println(row +" x "+ column);
		for(int i=0;i<row;i++) {
			for(int j=0;j<column;j++) {
				//System.out.print(String.format("%5s, ", matrix.get(i, j)));
			}
			System.out.println(" ..");
		}
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
		for(int i=0;i<size-1;i++) {
			number[i+1] = Float.parseFloat(numberStr[i]);
		}
		return number;
	}
}
