import java.util.*;

public class Matrix {
	public static void main(String[] args) {
		int[][] matrix = { { 1, 2, 3},{4,5,6},{7,8,9},{10,11,12},{13,14,15} };
		// int[][] matrix = { {1,2,3},{4,5,6},{7,8,9}};
		printMatrix(matrix);
	}

	public static void printMatrix(int[][] matrix) {
		if (matrix.length == 0) {
			return;
		}
		if (matrix.length == 1) {
			for (int i = 0; i < matrix[0].length; i++) {
				System.out.print(matrix[0][i]);
			}
			return;
		}
		if (matrix[0].length == 1) {
			for (int i = 0; i < matrix.length; i++) {
				System.out.print(matrix[i][0]);
			}
			return;
		}

		
		int row1 = 0;// 左上角的横坐标
		int col1 = 0;// 左上角的纵坐标
		int row2 = matrix.length;// 右下角的横坐标
		int col2 = matrix[0].length;// 右下角的纵坐标
		while (row1 < row2 && col1 < col2) {
			for (int i = col1; i < col2 - 1; i++) {// 打印上行 123
				System.out.print(matrix[row1][i]);
			}
			for (int i = row1; i < row2 - 1; i++) {// 打印右列 4 8 12
				System.out.print(matrix[i][col2 - 1]);
			}
			for (int i = col2 - 1; i > col1; i--) {// 打印下行 16 15 14
				System.out.print(matrix[row2 - 1][i]);
			}
			for (int i = row2 - 1; i > row1; i--) {// 打印左列 13 9 5
				System.out.print(matrix[i][col1]);
			}

			row1++;
			col1++;
			row2--;
			col2--;
			if (row1 == row2 - 1) { //内部在一行的时候
				for (int i = col1; i < col2; i++) {
					System.out.println(matrix[row1][i]);
				}
				return;
			}
			if (col1 == col2 - 1) { //内部在一列的时候
				for (int i = row1; i < row2; i++) {
					System.out.print(matrix[i][col1]);
				}
				return;
			}
		}
	}
}