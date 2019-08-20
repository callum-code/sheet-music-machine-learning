/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author callumijohnston
 */
public class MatrixConverter {

    myFileReader get;
    double matrix[][];
    double borderMatrix[][];
    int xLength;
    int yLength;
    int xBorderLength;
    int yBorderLength;

    public MatrixConverter(String name) {
        get = new myFileReader(name);
        int height = get.data.length;
        ArrayList<Double> temp[] = new ArrayList[height];
        for (int j = 0; j < height; j++) {
            temp[j] = new ArrayList<>();
            Scanner line = new Scanner(get.data[j]);
            if (line.next().charAt(0) == '[') {
                while (line.hasNextDouble()) {
                    temp[j].add(line.nextDouble());
                }
            }
        }
        matrix = new double[temp[0].size()][temp.length];
        for (int j = 0; j < temp.length; j++) {
            for (int i = 0; i < temp[0].size(); i++) {
                matrix[i][j] = temp[j].get(i);
            }
        }
        borderMatrix = new double[matrix.length + 500][matrix[0].length + 400];
        if (matrix.length > 250 && matrix[0].length > 200) {
            for (int j = 0; j < borderMatrix[0].length; j++) {
                for (int i = 0; i < borderMatrix.length; i++) {
                    borderMatrix[i][j] = 0;
                }
            }
            for (int j = 200; j < borderMatrix[0].length - 200; j++) {
                for (int i = 250; i < borderMatrix.length - 250; i++) {
                    borderMatrix[i][j] = temp[j-200].get(i-250);
//                    System.out.println("m");
                }
            }
        } else {
            for (int j = 0; j < borderMatrix[0].length; j++) {
                for (int i = 0; i < borderMatrix.length; i++) {
                    borderMatrix[i][j] = 0;
                }
            }
        }
        xLength = matrix.length;
        yLength = matrix[0].length;
        xBorderLength = borderMatrix.length;
        yBorderLength = borderMatrix[0].length;
    }

    public MatrixConverter() {
        matrix = new double[][]{{0,0,0},{0,1,0},{0,0,0}};
    }

    @Override
    public String toString() {
       String s= "";
        for (int i = 0; i < yLength; i++) {
            s+="[";
            for (int j = 0; j < xLength; j++) {
                s+=matrix[j][i]+" ";
            }
            s+="]\n";
        }
        return s;
    }
    
}
