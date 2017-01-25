package models.goserver.util;

public class MatrixUtil {

	public static int[][] copyMatrix(int[][] matrix) {
		int[][] newMatrix = new int[matrix.length][];
		for (int i = 0; i < matrix.length; i++) {
			newMatrix[i] = matrix[i].clone();
		}
		return newMatrix;
	}

	public static boolean compareMatrix(int[][] matrix1, int[][] matrix2) {
		if (matrix1.length != matrix2.length) {
			return false;
		}
		for (int i = 0; i < matrix1.length; i++) {
			if (matrix1[i].length != matrix2[i].length) {
				return false;
			}
			for (int j = 0; j < matrix1.length; j++) {
				if (matrix1[i][j] != matrix2[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void printMatrix(int[][] matrix){
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[j][i] + " ");
			}
			System.out.println();
		}
	}

}
