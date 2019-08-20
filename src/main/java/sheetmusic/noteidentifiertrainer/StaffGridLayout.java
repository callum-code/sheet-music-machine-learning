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

import java.awt.Rectangle;

/**
 *
 * @author callumjohnston
 */
public class StaffGridLayout {

    SubImage[][] subImgs;
    Rectangle[][] matrix;
    int staffLines;
    int gridWidth;
    int gridHeight;
    double vPadding;
    double hPadding;
    double increment;

    int xMin;
    int xMax;
    int yMin;
    int yMax;

    int startX;
    int startY;

    public StaffGridLayout(Staff staff, int gridWidth, int gridHeight, double hPadding, double vPadding, int xMin, int yMin, int xMax, int yMax) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.vPadding = vPadding;
        this.hPadding = hPadding;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;

        increment = staff.increment;
        startX = staff.startX;
        startY = (int) (staff.startY - increment / 2);

    }

    public void setMatrix() {
        matrix = new Rectangle[xMax - xMin][yMax - yMin];
        for (int i = 0; i < xMax - xMin; i++) {
            for (int j = 0; j < yMax - yMin; j++) {
                Rectangle box = new Rectangle(
                        (int) ((startX + xMin * increment) + (i * increment) - (increment * hPadding)),
                        (int) ((startY + yMin * increment) + (j * increment) - (increment * vPadding)),
                        (int) ((increment * gridWidth) + (increment * hPadding)),
                        (int) ((increment * gridHeight) + (increment * vPadding)));
                matrix[i][j] = box;
            }
        }
    }

    public void setSubImages(boolean[][] image) {
        subImgs = new SubImage[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                Rectangle rectangle = matrix[i][j];
                subImgs[i][j] = new SubImage();
                subImgs[i][j].image = new boolean[rectangle.width][rectangle.height];
                subImgs[i][j].startX = startX + rectangle.x;
                subImgs[i][j].startY = startY + rectangle.y;
                for (int k = 0; k < subImgs[i][j].image.length; k++) {
                    for (int l = 0; l < subImgs[i][j].image[0].length; l++) {
                        if (rectangle.x + k >= 0 && rectangle.y + l >= 0
                                && rectangle.x + k < image.length && rectangle.y + l < image[0].length) {
                            subImgs[i][j].image[k][l] = image[rectangle.x + k][rectangle.y + l];
                        } else {
                            subImgs[i][j].image[k][l] = true;
                        }
                    }
                }
            }
        }
    }

}
