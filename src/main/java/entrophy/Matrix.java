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
		 System.out.println("X");
		//X.print();
		System.out.println("----------------");
		System.out.println("Y");
		//Y.print();
		System.out.println("----------------");
		SimpleMatrix Xtransport = X.transpose();
		
		X = Xtransport.mult(X);
		System.out.println("XX'");
		X.printDimensions();
		System.out.println("de :" + X.determinant());
		//X.print();
		System.out.println("----------------");
		SimpleMatrix Xinverse = X.pseudoInverse();
		//System.out.println("X inverse");
		Xinverse.print();
		System.out.println("----------------");
		SimpleMatrix Yprime = Xinverse.mult(Xtransport).mult(Y);
		// mat.printDimensions();
		List<Row> dataList = Utility.parseCSV("output/lr.csv", false);
	
		double[] b = MatrixUtil.toArray(Yprime);
		Yprime.printDimensions();
		System.out.println(Yprime);
		PrintWriter pw = new PrintWriter("output/linearregression_estimate.csv");
		for(Row row : dataList) {
			double estimateY = MatrixUtil.estimateY(b, row);
			pw.write(estimateY + " >>" + row);
		}
		pw.close();
		dataList.stream().map( row -> MatrixUtil.estimateY(b, row)).forEach( x -> System.out.print(""));
	}
}
