/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author callumijohnston
 */
public class ImageReader {

    BufferedImage image;
    int[][] BW;
    int[][][] colour;
    int progress = 0;

    public ImageReader(File imgFile) {

        image = getImage(imgFile);
        BW = new int[image.getWidth()][image.getHeight()];
        colour = new int[3][image.getWidth()][image.getHeight()];
        
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                colour[0][i][j]=red;
                colour[1][i][j]=blue;
                colour[2][i][j]=green;
                BW[i][j] = (red + green + blue) / 3;
            }
        }
//        int pix[]= new int[3];
//        for (int i = 0; i < image.getWidth(); i++) {
//            for (int j = 0; j < image.getHeight(); j++) {
//                image.getRaster().getPixel(i, j, pix);
//                int red = pix[0];
//                int green = pix[1];
//                int blue = pix[2];
//                colour[i][j][0]=red;
//                colour[i][j][1]=blue;
//                colour[i][j][2]=green;
//                BW[i][j] = (red + green + blue) / 3;
//            }
//        }
        
    }

//    public String getBWMatrix() {
//        String m = "";
//        for (int j = 0; j < BW[0].length; j++) {
//            m += "[ ";
//            for (int i = 0; i < BW.length; i++) {
//                if (BW[i][j] > 99) {
//                    m += BW[i][j] + " ";
//                }else if (BW[i][j] > 9){
//                    m += BW[i][j] + "  ";
//                }else {
//                    m += BW[i][j] + "   ";
//                }
//            }
//            m += "]\n";
//        }
//        return m;
//    }
//    
//    public String getRMatrix() {
//        String m = "";
//        for (int j = 0; j < BW[0].length; j++) {
//            m += "[ ";
//            for (int i = 0; i < BW.length; i++) {
//                if (colour[i][j][0] > 99) {
//                    m += colour[i][j][0] + " ";
//                }else if (colour[i][j][0] > 9){
//                    m += colour[i][j][0] + "  ";
//                }else {
//                    m += colour[i][j][0] + "   ";
//                }
//            }
//            m += "]\n";
//        }
//        return m;
//    }
//    
//    public String getBMatrix() {
//        String m = "";
//        for (int j = 0; j < BW[0].length; j++) {
//            m += "[ ";
//            for (int i = 0; i < BW.length; i++) {
//                if (colour[i][j][1] > 99) {
//                    m += colour[i][j][1] + " ";
//                }else if (colour[i][j][1] > 9){
//                    m += colour[i][j][1] + "  ";
//                }else {
//                    m += colour[i][j][1] + "   ";
//                }
//            }
//            m += "]\n";
//        }
//        return m;
//    }
//    
//    public String getGMatrix() {
//        String m = "";
//        for (int j = 0; j < BW[0].length; j++) {
//            m += "[ ";
//            for (int i = 0; i < BW.length; i++) {
//                if (colour[i][j][2] > 99) {
//                    m += colour[i][j][2] + " ";
//                }else if (colour[i][j][2] > 9){
//                    m += colour[i][j][2] + "  ";
//                }else {
//                    m += colour[i][j][2] + "   ";
//                }
//            }
//            m += "]\n";
//        }
//        return m;
//    }

    public BufferedImage getImage(String file) {
        BufferedImage preImg = null;
        try {
            preImg = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("fuck");
        }
        return preImg;
    }
    
    public BufferedImage getImage(File file) {
        BufferedImage preImg = null;
        try {
            preImg = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("fuck");
        }
        return preImg;
    }


}
