package sheetmusic.noteidentifiertrainer;

import java.awt.*;
import java.util.ArrayList;

public class Detected {
    public ArrayList<IndexItem> restDetected = new ArrayList<>();
    public ArrayList<IndexItem> noteDetected = new ArrayList<>();
    public ArrayList<IndexItem> clefDetected = new ArrayList<>();
    public ArrayList<IndexItem> accidentalDetected = new ArrayList<>();
    public ArrayList<IndexItem> noteFullDetected = new ArrayList<>();

    public ArrayList<IndexItem> get(int i){
        switch(i){
            case 0:
                System.out.println("got rest");
                return restDetected;
            case 1:
                System.out.println("got note");
                return noteDetected;
            case 2:
                System.out.println("got clef");
                return clefDetected;
            case 3:
                System.out.println("got accidental");
                return accidentalDetected;
            case 4:
                System.out.println("got note full");
                return noteFullDetected;
            default:
                return null;
        }
    }

    public void set(int i, ArrayList<IndexItem> det){
        switch(i){
            case 0:
                System.out.println("set to rest");
                restDetected = det;
                break;
            case 1:
                System.out.println("set to note");
                noteDetected = det;
                break;
            case 2:
                System.out.println("set to clef");
                clefDetected = det;
                break;
            case 3:
                System.out.println("set to accidental");
                accidentalDetected = det;
                break;
            case 4:
                System.out.println("set to note full");
                noteFullDetected = det;
        }
    }

    public static SubImage[][] getSubImgs(Staff staff, int i){
        switch(i){
            case 0:
                return staff.restImgs.subImgs;
            case 1:
                return staff.noteImgs.subImgs;
            case 2:
                return staff.clefImgs.subImgs;
            case 3:
                return staff.accidentalImgs.subImgs;
            case 4:
                return staff.nfSubImgs;
            default:
                return null;
        }
    }

    public static Rectangle[][] getMatrix(Staff staff, int i){
        switch(i){
            case 0:
                return staff.restImgs.matrix;
            case 1:
                return staff.noteImgs.matrix;
            case 2:
                return staff.clefImgs.matrix;
            case 3:
                return staff.accidentalImgs.matrix;
            case 4:
                return staff.nfMatrix;
            default:
                return null;
        }
    }

    public String getType(int i){
        return new String[]{"Rest","Note","Clef","Accidental","NoteFull"}[i];
    }

}
