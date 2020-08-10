package entrophy;

import java.io.IOException;

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
		
		System.out.println(" X");
		X.printDimensions();

		System.out.println("Y");
		Y.printDimensions();
		System.out.println("Start");
		SimpleMatrix Xtransport = X.transpose();
		
		X = Xtransport.mult(X);

		SimpleMatrix mat = X.pseudoInverse();
		System.out.println("second step");
		mat.printDimensions();
		mat = mat.mult(Xtransport).mult(Y);
		System.out.print("---------------");
		mat.printDimensions();
		mat.print();
	}
}
