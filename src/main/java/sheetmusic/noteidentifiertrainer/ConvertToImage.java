/*
*   _____      _ _                                 _        ___   ___  __  ___  
*  / ____|    | | |                               | |      |__ \ / _ \/_ |/ _ \ 
* | |     __ _| | |_   _ _ __ ___     ___ ___   __| | ___     ) | | | || | (_) |
* | |    / _` | | | | | | '_ ` _ \   / __/ _ \ / _` |/ _ \   / /| | | || |> _ < 
* | |___| (_| | | | |_| | | | | | | | (_| (_) | (_| |  __/  / /_| |_| || | (_) |
*  \_____\__,_|_|_|\__,_|_| |_| |_|  \___\___/ \__,_|\___| |____|\___/ |_|\___/ 
* 
 */
package sheetmusic.noteidentifiertrainer;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author callumjohnston
 */
public class ConvertToImage {

    public static File convert(int[][] matrix, String filePath) {
        File output = new File(filePath);
        return convert(matrix, output);
    }

    public static File convert(int[][] matrix, File file) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            System.out.println("empty rows or columns");
            return null;
        }
        try {
            BufferedImage image = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    int a = matrix[i][j];
                    Color newColor = new Color(a, a, a);
                    image.setRGB(j, i, newColor.getRGB());
                }
            }
            File output = file;
            ImageIO.write(image, "jpg", output);
            return output;
        } catch (Exception e) {
            System.out.println("image conversion failure");
            return null;
        }
    }

    public static Image convert(int[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            System.out.println("empty rows or columns");
            return null;
        }
        try {
            BufferedImage image = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    int a = matrix[i][j];
                    Color newColor = new Color(a, a, a);
                    image.setRGB(j, i, newColor.getRGB());
                }
            }
            return image;
        } catch (Exception e) {
            System.out.println("image conversion failure");
            return null;
        }
    }

    public static int[][] scale(int[][] data, int newH, int newW) {
        int width = data.length;
        int height = data[0].length;
        int[][] newDataH = new int[width][newH];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < newH; j++) {
                newDataH[i][j] = data[i][(int) ((double)(j) / (double)(newH) * (double)(height))];
            }
        }
        int[][] newData = new int[newW][newH];
        for (int i = 0; i < newW; i++) {
            for (int j = 0; j < newH; j++) {
                newData[i][j] = newDataH[(int) ((double)(i) / (double)(newW) * (double)(width))][j];
            }
        }
        return newData;
    }

}
