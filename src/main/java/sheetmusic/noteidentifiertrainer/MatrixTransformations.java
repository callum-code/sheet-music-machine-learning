/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

/**
 *
 * @author callumijohnston
 */
public class MatrixTransformations {

    public double[][] kernel = new double[3][3];
    public double[][] matrix;

    public MatrixTransformations(double[][] k, int[][] m) {

    }

    public double[][] getKernel() {
        return kernel;
    }

    public void setKernel(double[][] kernel) {
        this.kernel = kernel;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

}
