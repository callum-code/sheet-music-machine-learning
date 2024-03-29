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

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author callumjohnston
 */
public class ExtractFiles {
    ArrayList<VectorizeImage> notes;
    ArrayList<VectorizeImage> accidentals;
    ArrayList<VectorizeImage> rests;
    ArrayList<VectorizeImage> clefs;
    ArrayList<VectorizeImage> timeSigs;
    ArrayList[] all = new ArrayList[5];
    final String ROOT_DIR = "Data";

    public ExtractFiles() {
        this.all[4] = this.timeSigs = new ArrayList<>();
        this.all[3] = this.clefs = new ArrayList<>();
        this.all[2] = this.rests = new ArrayList<>();
        this.all[1] = this.accidentals = new ArrayList<>();
        this.all[0] = this.notes = new ArrayList<>();
    }
    
    public void extract(){
        extractAllFromDir(notes, "Note");
        extractAllFromDir(clefs, "Clef");
        extractAllFromDir(rests, "Rest");
        extractAllFromDir(accidentals, "Accidental");
        extractAllFromDir(timeSigs, "TimeSig");
    }

    private void extractAllFromDir(ArrayList<VectorizeImage> imgs, String type) {
        File[] files = new File(ROOT_DIR + "/" + type).listFiles();
        for(File file: files){
            if (file.isFile() && !file.getAbsolutePath().contains("numFiles")){
                imgs.add(new VectorizeImage(file));
            }
        }
    }
    
    public static void main(String[] args) {
        ExtractFiles ex = new ExtractFiles();
        ex.extract();
        for (ArrayList<VectorizeImage> arr: ex.all){
            System.out.println(arr.size() + " items.");
            System.out.println("Item 0: " + arr.get(0).toString());
            System.out.println("");
        }
        int maxW = 0;
        int maxH = 0;
        for (VectorizeImage im: ex.notes){
            System.out.println(im.width + ", " + im.height);
            maxW = im.width > maxW ? im.width : maxW;
            maxH = im.height > maxH ? im.height : maxH;
        }
        System.out.println("");
        System.out.println(maxW + ", " + maxH);
    }

    
}
