/*
 *   _____      _ _                                 _        ___   ___  __  ___
 *  / ____|    | | |                               | |      |__ \ / _ \/_ |/ _ \
 * | |     __ _| | |_   _ _ __ ___     ___ ___   __| | ___     ) | | | || | (_) |
 * | |    / _` | | | | | | '_ ` _ \   / __/ _ \ / _` |/ _ \   / /| | | || |> _ <
 * | |___| (_| | | | |_| | | | | | | | (_| (_) | (_| |  __/  / /_| |_| || | (_) |
 *  \_____\__,_|_|_|\__,_|_| |_| |_|  \___\___/ \__,_|\___| |____|\___/ |_|\___/
 *
 */

package sheetmusic;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author callumjohnston
 */
public class VectorImage {


    String info;
    boolean answer = false;
    int[] data;
    int width;
    int height;
    int fHeight;
    int fWidth;
    int[] fullData;

    public VectorImage(String filePath) {
        this(new File(filePath));
    }

    public VectorImage(File file) {
        System.out.println(file.getAbsolutePath());
        analyzeImage(file);
    }

    private void analyzeImage(File file) {
        try {
            ArrayList<Integer> preData = new ArrayList<>();
            ArrayList<Integer> preFullData = new ArrayList<>();
            boolean inImg = false;
            boolean inFullImg = false;
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine();

                if (inImg) {
                    if (!line.contains("0") && !line.contains("1")) {
                        inImg = false;
                    } else {
                        height++;
                        width = line.toCharArray().length > width ? line.toCharArray().length : width;
                        for (char c : line.toCharArray()) {
                            preData.add(c == '1'?0:1);
                        }
                    }
                }
                if (inFullImg) {
                    if (!line.contains("0") && !line.contains("1")) {
                        inImg = false;
                    } else {
                        fHeight++;
                        fWidth = line.toCharArray().length != fWidth ? line.toCharArray().length : fWidth;
                        for (char c : line.toCharArray()) {
                            preFullData.add(c == '1'?0:1);
                        }
                    }
                }

                if (line.contains("Symbol")) {
                    answer = line.contains("y");
                }
                if (line.contains("Info")) {
                    Scanner ls = new Scanner(line);
                    while (ls.hasNext()) {
                        if (ls.next().contains("Info")) {
                            info = ls.next();
                            break;
                        }
                    }
                }
                if (line.contains("Image") && !line.contains("Full")) {
                    inImg = true;
                    height = 0;
                }
                if (line.contains("Full")) {
                    inFullImg = true;
                    inImg = false;
                    fHeight = 0;
                }
            }

            preDataToData("f", preFullData);
            preDataToData(" ", preData);

        } catch (Exception e) {
            System.out.println("Vectorize Image fucked up");
        }
    }

    public void scale(int newW, int newH) {
        System.out.println("");
        int[] newData = this.data;
        if (this.width != newW) {
            int[] newDataW = new int[this.height * newW];
            for (int i = 0; i < this.height ; i++) {
                int[] row = Arrays.copyOfRange(this.data, i * this.width, (i + 1) * this.width);
                for (int j = 0; j < newW; j++) {
                    newDataW[i * newW + j] = row[(int) (1.0 * this.width / newW * j)];
                }
            }
            newData = newDataW;
        }
        this.data = newData;
        this.width = newW;
        if (this.height != newH) {
            int[] newDataW = new int[this.width * newH];
            for (int i = 0; i < this.width ; i++) {
                int[] column = new int[this.height];
                for (int j = 0; j < this.height; j++){
                    column[j] = this.data[j*this.width+i];
                }
                for (int j = 0; j < newH; j++) {
                    newDataW[j * this.width + i] = column[(int) (1.0 * this.height / newH * j)];
                }
            }
            newData = newDataW;
        }
        this.data = newData;
        this.height = newH;
    }

    public void fScale(int newW, int newH) {
        System.out.println("");
        int[] newData = this.fullData;
        if (this.fWidth != newW) {
            int[] newDataW = new int[this.fHeight * newW];
            for (int i = 0; i < this.fHeight ; i++) {
                int[] row = Arrays.copyOfRange(this.fullData, i * this.fWidth, (i + 1) * this.fWidth);
                for (int j = 0; j < newW; j++) {
                    newDataW[i * newW + j] = row[(int) (1.0 * this.fWidth / newW * j)];
                }
            }
            newData = newDataW;
        }
        this.fullData = newData;
        this.fWidth = newW;
        if (this.fHeight != newH) {
            int[] newDataW = new int[this.fWidth * newH];
            for (int i = 0; i < this.fWidth ; i++) {
                int[] column = new int[this.fHeight];
                for (int j = 0; j < this.fHeight; j++){
                    column[j] = this.fullData[j*this.fWidth+i];
                }
                for (int j = 0; j < newH; j++) {
                    newDataW[j * this.fWidth + i] = column[(int) (1.0 * this.fHeight / newH * j)];
                }
            }
            newData = newDataW;
        }
        this.fullData = newData;
        this.fHeight = newH;
    }

    private void preDataToData(String type, ArrayList<Integer> preData) {
        if (type.contains("f")) {
            fullData = new int[preData.size()];
            for (int i = 0; i < fullData.length; i++) {
                fullData[i] = preData.get(i);
            }
        } else {
            data = new int[preData.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = preData.get(i);
            }
        }
    }

    public INDArray getINDArray(){ return Nd4j.create(dataToDoubles(),new int[]{height,width},'c'); }

    public INDArray getFullINDArray(){
        return Nd4j.create(fullDataToDoubles(),new int[]{ fHeight,fWidth},'c');
    }

    public String toString() {
        String returnMe = "";
        returnMe += (info + "\n");
        returnMe += (width + ", " + height + "\n");
        returnMe += (answer + "\ndata:");
        for (int i = 0; i < data.length; i++) {
            if (i % this.width == 0) {
                returnMe += "\n";
            }
            returnMe += data[i];
        }
        if (fullData.length > 0) {
            returnMe += ("\nfull:");
            for (int i = 0; i < fullData.length; i++) {
                if (i % this.fWidth == 0) {
                    returnMe += "\n";
                }
                returnMe += fullData[i];
            }
        }
        return returnMe;
    }

    public static void main(String[] args) {
        VectorImage img = new VectorImage("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/Data/Note/Note_22.txt");
        System.out.println(img.toString());
        img.scale(24,18);
        System.out.println(img.toString());
    }

    public double[] dataToDoubles() {
        double[] returnMe = new double[data.length];
        for (int i =0;i<data.length;i++){
            returnMe[i] = (double)(data[i]);
        }
        return returnMe;
    }

    public double[] fullDataToDoubles() {
        double[] returnMe = new double[fullData.length];
        for (int i =0;i<fullData.length;i++){
            returnMe[i] = (double)(fullData[i]);
        }
        return returnMe;
    }
}

