package entrophy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.ejml.simple.SimpleMatrix;

public class Matrix {

	public static void main(String[] args) throws IOException {
//		double[][] data = { { 1, 2, 3 }, { 3, 4, 5 }, { 7, 8, 9 }, { 10, 11, 12 } };
//		double[][] Ydata = { { 1 }, { 2 }, { 3 }, { 1 } };
//		SimpleMatrix X = new SimpleMatrix(data);
//		SimpleMatrix Y = new SimpleMatrix(Ydata);
		
		 SimpleMatrix[] data = MatrixUtil.loadMatrix("output/linearregression.csv");
		 SimpleMatrix X = data[0];
		 SimpleMatrix Y = data[1];
		
		X.printDimensions();
		Y.printDimensions();
		SimpleMatrix Xtransport = X.transpose();
		
		X = Xtransport.mult(X);
		SimpleMatrix mat = X.pseudoInverse();
		mat = mat.mult(Xtransport).mult(Y);
		// mat.printDimensions();
		List<Row> dataList = Utility.parseCSV("output/linearregression.csv", false);
		
		double[] b = MatrixUtil.toArray(mat);
		System.out.println(Arrays.toString(b));
		PrintWriter pw = new PrintWriter("output/linearregression_estimate.csv");
		for(Row row : dataList) {
			double estimateY = MatrixUtil.estimateY(b, row);
			pw.write(estimateY + " >>" + row);
		}
		pw.close();
		dataList.stream().map( row -> MatrixUtil.estimateY(b, row)).forEach( x -> System.out.print(""));
	}
}
