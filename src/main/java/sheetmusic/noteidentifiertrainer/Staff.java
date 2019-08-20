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

import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author callumjohnston
 */
public class Staff {

    ArrayList<Integer[]> startCoors = new ArrayList<>();
    ArrayList<Integer[]> endCoors = new ArrayList<>();
    int startX;
    int endX;
    int startY;
    int endY;
    double avgInterval;
    double increment;
    String clef = "trebble";
    Rectangle[][] matrix;
    SubImage[][] subImgs;
    Rectangle[][] nfMatrix;
    SubImage[][] nfSubImgs;
    ArrayList<IndexItem> notes = new ArrayList<>();
    ArrayList<IndexItem> rests = new ArrayList<>();
    ArrayList<IndexItem> accidentals = new ArrayList<>();
    ArrayList<Symbol> symbols = new ArrayList<>();

    StaffGridLayout noteImgs;
    StaffGridLayout noteFullImgs;
    StaffGridLayout restImgs;
    StaffGridLayout clefImgs;
    StaffGridLayout timeSigImgs;
    StaffGridLayout accidentalImgs;

    int xGridLength;
    int yGridLength;

    public int id;
    String selected = "Note";
    public ArrayList<Measure> measures = new ArrayList<>();

    public Staff(ArrayList<Integer[]> startCoors, ArrayList<Integer[]> endCoors) {
        this.startCoors = startCoors;
        this.endCoors = endCoors;
        startX = startCoors.get(0)[0];
        endX = endCoors.get(0)[0];
        startY = startCoors.get(0)[1];
        endY = startCoors.get(startCoors.size() - 1)[1];
        avgInterval = (endY - startY) / 4.0;
        increment = avgInterval / 2.0;
    }

    public void setGridLayouts(boolean[][] image) {
        xGridLength = (int) ((endX - startX) / increment);
        yGridLength = (int) ((endY - startY) / increment);
        noteImgs = new StaffGridLayout(this, 3, 2, .5, .5, 0, -6, xGridLength, yGridLength + 6);
        noteImgs.setMatrix();
        noteImgs.setSubImages(image);
        accidentalImgs = new StaffGridLayout(this, 2, 2, .5, 4, 0, -5, xGridLength, yGridLength + 7);
        accidentalImgs.setMatrix();
        accidentalImgs.setSubImages(image);
        restImgs = new StaffGridLayout(this, 2, 4, 1, 3, 0, 3, xGridLength, yGridLength - 2);
        restImgs.setMatrix();
        restImgs.setSubImages(image);
        timeSigImgs = new StaffGridLayout(this, 3, 3, 1, 6, 7, 6, xGridLength * 15 / 16, 7);
        timeSigImgs.setMatrix();
        timeSigImgs.setSubImages(image);
        clefImgs = new StaffGridLayout(this, 5, 12, 1, 6, 0, 0, xGridLength / 16, 4);
        clefImgs.setMatrix();
        clefImgs.setSubImages(image);
        // additional dor notes
        noteFullImgs = new StaffGridLayout(this, 4, 12, 2, 11, 0, -6, xGridLength, yGridLength + 6);
        noteFullImgs.setMatrix();
        noteFullImgs.setSubImages(image);
        nfMatrix = noteFullImgs.matrix;
        nfSubImgs = noteFullImgs.subImgs;
    }

    public void addSymbol(Symbol symbol) {
        if (symbols.isEmpty()) {
            symbols.add(symbol);
        } else if (symbols.get(0).x > symbol.x) {
            symbols.add(0, symbol);
        } else if (symbols.get(symbols.size()-1).x < symbol.x) {
            symbols.add(symbol);
        } else {
            for (int i = 0; i < symbols.size()-1; i++) {
                if (symbols.get(i).x < symbol.x && symbols.get(i+1).x > symbol.x){
                    symbols.add(i+1, symbol);
                    break;
                }
                if (symbols.get(i).x == symbol.x){
                    symbols.add(i + 1, symbol);
                    break;
                }
            }
            if (symbols.get(symbols.size()-1).x == symbol.x){
                symbols.add(symbol);
            }
        }
    }

    public void addAllNotes(){
        for(IndexItem note:notes) {
            String accidental = "";
            for (int i = note.coors[2]; i < note.coors[2] + 4; i++) {
                for (int j = note.coors[1] - 4; j < note.coors[1]-1; j++) {
                    for (IndexItem acc : accidentals) {
                        if (acc.coors[0].equals(note.coors[0])
                            && acc.coors[2].intValue() == i
                            && acc.coors[1].intValue() == j) {
                            accidental = acc.label;
                        }
                    }
                }
            }
            Note newNote = new Note(note);
            newNote.pitch = getPitch(note.coors[2]);
            newNote.accidental = accidental.equals("sharp") ? '#' : (accidental.equals("flat") ? 'B' : 'N');
            newNote.octave = getOctave(note.coors[2]);
            addSymbol(newNote);
        }
        for(IndexItem rest:rests) {
            addSymbol(new Rest(rest));
        }
    }

    public char getPitch(int yVal){
        if (clef.equals("trebble")){
            return "EDCBAGF".charAt(yVal % 7);
        } else {
            return "GFEDCBA".charAt(yVal % 7);
        }
    }

    int getOctave(int yVal) {
        return 5 - (int) (Math.floor((yVal-3)/7));
    }

    void currentGrid(String selected) {
        switch (selected) {
            case "Note":
                matrix = noteImgs.matrix;
                subImgs = noteImgs.subImgs;
                System.out.println("Note " + id);
                break;
            case "Accidental":
                matrix = accidentalImgs.matrix;
                subImgs = accidentalImgs.subImgs;
                System.out.println("Accidental");
                break;
            case "Rest":
                matrix = restImgs.matrix;
                subImgs = restImgs.subImgs;
                System.out.println("Rest");
                break;
            case "Time Signature":
                matrix = timeSigImgs.matrix;
                subImgs = timeSigImgs.subImgs;
                System.out.println("Time Sig");
                break;
            case "Clef":
                matrix = clefImgs.matrix;
                subImgs = clefImgs.subImgs;
                System.out.println("Clef");
                break;
            default:
                matrix = noteImgs.matrix;
                subImgs = noteImgs.subImgs;
        }
        this.selected = selected;
        if (selected.equals("Time Signature")) {
            this.selected = "TimeSig";
        }
    }

    void paint(Graphics2D g) {
        for (int i = 0; i < startCoors.size(); i++) {
            g.drawLine(startCoors.get(i)[0], startCoors.get(i)[1],
                    endCoors.get(i)[0], endCoors.get(i)[1]);
        }
    }

    void paintMeasures(Graphics2D g) {
        for (Measure measure : measures) {
            measure.paint(g, startCoors.get(0)[1],
                    endCoors.get(endCoors.size() - 1)[1]);
        }
    }

    public void play(){
        MyMIDIPlayer player = new MyMIDIPlayer();
        player.setUpPlayer(symbols);
    }

    public String ouputString(){
        String returnMe = "";
        String pitch = "";
        double pause = 0;
        int octave = 0;

        for (int i = 0; i < symbols.size()-1; i++) {
            Symbol s = symbols.get(i);
            if (s.id.contains("n")) {
                pitch = s.pitch();
                pause = s.length * 2;
                octave = s.octave() + 1;
            } else {
                pause += s.length * 2;
            }
            if (symbols.get(i+1).id.contains("n")) {
                returnMe += "\n" + pitch + octave;
                returnMe +=  (s.x==symbols.get(i+1).x) ? 0 : pause;
            }
        }
        if (symbols.size()>0) {
            Symbol last = symbols.get(symbols.size() - 1);
            if (last.id.contains("n")) {
                pitch = last.pitch();
                pause = last.length * 2;
                octave = last.octave() + 1;
                returnMe += "\n" + pitch + octave;
                returnMe += pause;
            }
        }
        return returnMe;
    }

    public String toString() {
        String returnMe = "";
        returnMe += ("Staff: ");
        for (Symbol s: symbols) {
            returnMe += s.toString() + ", ";
        }
        return returnMe;
    }

}
