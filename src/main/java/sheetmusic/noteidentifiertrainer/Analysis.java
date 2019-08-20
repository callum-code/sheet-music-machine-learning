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

import play.mvc.WebSocket;
import sheetmusic.MusicNet;

import java.awt.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author callumjohnston
 */
public class Analysis {

    MatrixConverter kernel = new MatrixConverter();
    //MatrixTransformations BWMatrix = new MatrixTransformations(kernel.matrix, BW.borderMatrix);
    int width;
    int height;
    Square graph[][];
    int ratio = 1;
    int[][][] imgMatrix;
    int[][] imgMatrixBW;
    int[][][] finalValues;
    public boolean drawStaffs = false;
    public boolean drawMeasures = false;
    public boolean drawMatrices = false;
    public boolean[][] blackWhite;
    public ArrayList<Integer[]> startCoors = new ArrayList<>();
    public ArrayList<Integer[]> endCoors = new ArrayList<>();
    public ArrayList<Staff> staffs = new ArrayList<>();
    public int threshold = 200;
    public MatrixPanel mp;
    public NotePanel noteAnalyser;
    public NoteFrame noteAnalyserFrame;
    public OutputFrame outputFrame;
    public int staffSelected = 0;
    public int xNoteSelected = 0;
    public int yNoteSelected = 0;
    public int detectedNoteSelected = 0;
    public String detectionSymbol;
    public int[] areaSelection = new int[2];
    public boolean selecting = false;
    File dir = new File("Data/Note");

    public Detected detections = new Detected();

    public Analysis(int[][] inputMatrix) {
        imgMatrixBW = inputMatrix;
        //fixTooBig();
        width = imgMatrixBW.length;
        height = imgMatrixBW[0].length;
        blackWhite = new boolean[width][height];
        graph = new Square[width][height];
        blackWhite = toBoolean(imgMatrixBW);
        mp = new MatrixPanel(blackWhite, this);
        setPanel();
        mp.repaint();
    }

    private void fixTooBig(){
        int[][] newBW = new int[imgMatrixBW.length/2][imgMatrixBW[0].length/2];
        if (imgMatrixBW.length>1000||imgMatrixBW[0].length>1000){
            for(int i = 0;i<imgMatrixBW.length;i=+2) {
                for (int j = 0; j< imgMatrixBW[0].length; j=+2) {
                    newBW[i/2][j/2] = imgMatrixBW[i][j];
                }
            }
        }
        imgMatrixBW = newBW;
    }

    public void analyzeHorizontalLines(boolean blackLines) {
        System.out.println("Analysing....");
        boolean[][] newBW = new boolean[blackWhite.length][blackWhite[0].length];
        for (int i = 0; i < blackWhite[0].length; i++) {
            if (i % ((int) (blackWhite[0].length / 20)) == 0) {
                System.out.print("*");
            }
            for (int j = 0; j < blackWhite.length; j++) {
                boolean isLine = true;
                for (int k = 0; k < blackWhite.length / 10; k++) {
                    if (j + k >= blackWhite.length - 1) {
                        break;
                    }
                    if (blackWhite[j + k][i] && blackLines
                            || !blackWhite[j + k][i] && !blackLines) {
                        isLine = false;
                        break;
                    }
                }
                newBW[j][i] = isLine;
                if (!isLine) {
                    isLine = true;
                    for (int k = 0; k < blackWhite.length / 10; k++) {
                        if (j - k <= 0) {
                            break;
                        }
                        if (blackWhite[j - k][i] && blackLines
                                || !blackWhite[j - k][i] && !blackLines) {
                            isLine = false;
                            break;
                        }
                    }
                    newBW[j][i] = isLine;
                }
            }
        }
        System.out.println("\nAnalysed");
        blackWhite = newBW;
        setPanel();
        mp.repaint();
    }

    private boolean[][] removeStaffLines(boolean[][] blackWhite) {
        boolean[][] newBW = new boolean[blackWhite.length][blackWhite[0].length];
        for (int i = 0; i < blackWhite[0].length; i++) {
            if (i % ((int) (blackWhite[0].length / 20)) == 0) {
                System.out.print("*");
            }
            for (int j = 0; j < blackWhite.length; j++) {
                boolean isLine = true;
                for (int k = 0; k < blackWhite.length / 10; k++) {
                    if (j + k >= blackWhite.length - 1) {
                        break;
                    }
                    if (blackWhite[j + k][i]) {
                        isLine = blackWhite[j][i];
                        break;
                    }
                }
                newBW[j][i] = isLine;
                if (isLine == blackWhite[j][i]) {
                    isLine = true;
                    for (int k = 0; k < blackWhite.length / 10; k++) {
                        if (j - k <= 0) {
                            break;
                        }
                        if (blackWhite[j - k][i]) {
                            isLine = blackWhite[j][i];
                            break;
                        }
                    }
                    newBW[j][i] = isLine;
                } else {
                    if (i > 0 && i < blackWhite[0].length - 1) {
                        if (!blackWhite[j][i + 1] && !blackWhite[j][i - 1]) {
                            isLine = blackWhite[j][i];
                            newBW[j][i] = isLine;
                        }
                    }
                }
            }
        }
        return newBW;
    }

    public void extractIndividualLines() {
        boolean[][] newBW = new boolean[blackWhite.length][blackWhite[0].length];
        for (int i = 0; i < blackWhite.length; i++) {
            for (int j = 1; j < blackWhite[i].length; j++) {
                if (blackWhite[i][j] && !blackWhite[i][j - 1]) {
                    newBW[i][j] = true;
                }
            }
        }
        blackWhite = newBW;
        setPanel();
        mp.repaint();
        analyzeHorizontalLines(false);
        //TODO:
        //change all this to a system that scans down from the middle of the page,
        //and goes outwards both ways until it ends, then checks to see if there is
        //anything above or below it. It continues on until it ends, then it has the 
        //horizontical bounds. If the vertical bounds are within 3 pixels of eachother
        //then is a line.
    }

    public void extractCoordinates() {
        for (int j = 1; j < blackWhite[0].length - 1; j++) {
            boolean white = false;
            for (int i = 1; i < blackWhite.length - 1; i++) {
                if (!white && blackWhite[i][j]) {
                    white = true;
                    Integer[] coors = {i, j};
                    startCoors.add(coors);
                } else if (white && !blackWhite[i][j]) {
                    Integer[] coors = {i - 1, j};
                    endCoors.add(coors);
                    break;
                }
            }
        }
        System.out.println("Coordinates");
        for (int i = 0; i < startCoors.size(); i++) {
            System.out.println("\t" + startCoors.get(i)[0] + ", " + startCoors.get(i)[1]
                    + " -> " + endCoors.get(i)[0] + ", " + endCoors.get(i)[1]);
        }
    }

    public void staffIdentification() {
        int total = 0;
        for (int i = 0; i < startCoors.size() - 1; i++) {
            total += startCoors.get(i + 1)[1] - startCoors.get(i)[1];
        }
        int avg = total / (startCoors.size() - 1);

        ArrayList<Integer[]> staffStartCoors = new ArrayList<>();
        ArrayList<Integer[]> staffEndCoors = new ArrayList<>();
        staffStartCoors.add(startCoors.get(0));
        staffEndCoors.add(endCoors.get(0));

        for (int i = 0; i < startCoors.size() - 1; i++) {
            if (startCoors.get(i + 1)[1] - startCoors.get(i)[1] > avg) {
                staffs.add(new Staff((ArrayList<Integer[]>) (staffStartCoors.clone()),
                        (ArrayList<Integer[]>) (staffEndCoors.clone())));
                staffStartCoors = new ArrayList<>();
                staffEndCoors = new ArrayList<>();
            }
            staffStartCoors.add(startCoors.get(i + 1));
            staffEndCoors.add(endCoors.get(i + 1));
        }
        staffs.add(new Staff((ArrayList<Integer[]>) (staffStartCoors.clone()),
                (ArrayList<Integer[]>) (staffEndCoors.clone())));
        ArrayList<Staff> realStaffs = new ArrayList<>();
        for (Staff staff : staffs) {
            if (staff.startCoors.size() >= 3) {
                realStaffs.add(staff);
            }
        }
        staffs = realStaffs;
        for (int i = 0; i < staffs.size(); i++) {
            staffs.get(i).id = i;
        }
        for (Staff staff : staffs) {
            int minStart = 100000;
            for (Integer[] coors : staff.startCoors) {
                if (coors[0] < minStart) {
                    minStart = coors[0];
                }
            }
            int maxEnd = 0;
            for (Integer[] coors : staff.endCoors) {
                if (coors[0] > maxEnd) {
                    maxEnd = coors[0];
                }
            }
            for (Integer[] coors : staff.startCoors) {
                coors[0] = minStart;
            }
            for (Integer[] coors : staff.endCoors) {
                coors[0] = maxEnd;
            }
            staff.startX = minStart;
            staff.endX = maxEnd;
        }
        mp.repaint();
    }

    public void resetViewBW() {
        blackWhite = toBoolean(imgMatrixBW);
        setPanel();
        mp.repaint();
    }

    public void measureIdentification() {
        for (Staff staff : staffs) {
            ArrayList<Measure> staffMeasures = new ArrayList();
            int start = staff.startX;
            int end = staff.endX;
            staffMeasures.add(new Measure(start));
            for (int i = start; i < end; i++) {
                if (!blackWhite[i][staff.startY + 1]) {
                    boolean measure = true;
                    for (int j = staff.startY + 1; j < staff.endY; j++) {
                        if (blackWhite[i][j]) {
                            measure = false;
                            break;
                        }
                    }
                    //below
                    for (int j = 0; j < 15; j++) {
                        if (!blackWhite[i][staff.endY + 2 + j]) {
                            if (!blackWhite[i - 2][staff.endY + 2 + j]
                                    || !blackWhite[i + 2][staff.endY + 2 + j]) {
                                measure = false;
                            }
                        }
                    }
                    //above
                    for (int j = 0; j < 15; j++) {
                        if (!blackWhite[i][staff.startY - 2 - j]) {
                            if (!blackWhite[i - 2][staff.startY - 2 - j]
                                    || !blackWhite[i + 2][staff.startY - 2 - j]) {
                                measure = false;
                            }
                        }
                    }
                    if (measure) {
                        staffMeasures.add(new Measure(i));
                    }
                }
            }
            for (int i = 0; i < staffMeasures.size() - 1; i++) {
                if (staffMeasures.get(i + 1).xCoorStart - staffMeasures.get(i).xCoorStart > staff.avgInterval) {
                    staffMeasures.get(i).xCoorEnd = staffMeasures.get(i + 1).xCoorStart;
                    staff.measures.add(staffMeasures.get(i));
                }
            }
            if (!staff.measures.isEmpty()) {
                if (staff.measures.get(staff.measures.size() - 1).xCoorEnd < staff.endX - 40) {
                    staff.measures.add(new Measure(staff.measures.get(staff.measures.size() - 1).xCoorEnd, staff.endX));
                }
            }
        }

        boolean[][] newBW = removeStaffLines(blackWhite);

        for (Staff staff : staffs) {

            staff.setGridLayouts(newBW);
            staff.currentGrid("Note");
        }
    }

    public void drawStaffs(Graphics2D g) {
        g.setColor(Color.red);

        for (Staff staff : staffs) {
            staff.paint(g);
        }

    }

    public void drawMeasures(Graphics2D g) {
        g.setColor(Color.red);

        for (Staff staff : staffs) {
            staff.paintMeasures(g);

        }
        //System.out.println("Measures Drawn");

    }

    public void drawMatrices(Graphics2D g) {
        g.setColor(new Color(0, 0, 255, 40));

//        for (Staff staff : staffs) {
//            for (int i = 0; i < staff.matrix.length; i++) {
//                for (int j = 0; j < staff.matrix[i].length; j++) {
//                    g.draw(staff.matrix[i][j]);
//                }
//            }
//        }
        //draw detected notes
        g.setStroke(new BasicStroke(2));

        for (int i = 0; i< 4;i++) {
            for (IndexItem coors : detections.get(i)) {
                g.setColor(typeColor(coors.label, detections.getType(i)));
                g.draw(Detected.getMatrix(staffs.get(coors.coors[0]),i)[coors.coors[1]][coors.coors[2]]);
            }
        }

        if (selecting) {
            g.setColor(Color.yellow);
            for (int i = areaSelection[0]; i < xNoteSelected; i++) {
                for (int j = areaSelection[1]; j < yNoteSelected; j++) {
                    g.draw(staffs.get(staffSelected).matrix[i][j]);
                }
            }
        }

        //draw delected box
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(3));
        g.draw(staffs.get(staffSelected).matrix[xNoteSelected][yNoteSelected]);
        if (staffs.get(staffSelected).selected.equals("Note")) {
            g.setColor(Color.orange);
            g.draw(staffs.get(staffSelected).nfMatrix[xNoteSelected][yNoteSelected]);
        }
        g.setStroke(new BasicStroke(1));

    }

    public Color typeColor(String label, String type){
        switch (type){
            case "Rest":
                if (label.equals("1/16")){
                    return new Color(0,255,0);
                } else if (label.equals("1/8")){
                    return new Color(0,180,80);
                }else if (label.equals("3/16")){
                    return new Color(0,160,160);
                }else if (label.equals("1")){
                    return new Color(0,80,180);
                }else if (label.equals("1 1/2")){
                    return new Color(0,00,255);
                } else {
                    return Color.white;
                }
            case "Note":
                if (label.equals("sixteenth")){
                    return new Color(255,0,0);
                } else if (label.equals("eighth")){
                    return new Color(200,120,120);
                } else if (label.equals("dotted eighth")){
                    return new Color(200,150,00);
                } else if (label.equals("quarter")){
                    return new Color(205,200,00);
                } else if (label.equals("half")){
                    return new Color(255,200,00);
                }else if (label.equals("whole")){
                    return new Color(255,255,0);
                } else {
                    return Color.white;
                }
            case "Accidental":
                if (label.equals("sharp")){
                    return new Color(140,0,255);
                } else if (label.equals("flat")){
                    return new Color(255,0,140);
                }else if (label.equals("natural")){
                    return new Color(140,0,140);
                } else {
                    return Color.white;
                }
            case "Clef":
                if (label.equals("trebble")){
                    return new Color(255,0,0);
                } else if (label.equals("bass")){
                    return new Color(180,140,0);
                } else {
                    return Color.white;
                }
            default:
                return Color.green;
        }
    }

    public int[][] toInt(boolean[][] dumb) {
        int[][] returnMe = new int[dumb[0].length][dumb.length];

        for (int i = 0; i < returnMe.length; i++) {
            for (int j = 0; j < returnMe[i].length; j++) {
                returnMe[i][j] = dumb[j][i] ? 0 : 255;

            }
        }
        return returnMe;

    }

    public int[][] toInt(double[][] dumb) {
        int[][] returnMe = new int[dumb.length][dumb[0].length];

        for (int i = 0; i
                < returnMe.length; i++) {
            for (int j = 0; j
                    < returnMe[i].length; j++) {
                returnMe[i][j] = (int) dumb[i][j];

            }
        }
        return returnMe;

    }

    public boolean[][] toBoolean(int[][] dumb) {
        boolean[][] returnMe = new boolean[dumb.length][dumb[0].length];

        for (int i = 0; i
                < returnMe.length; i++) {
            for (int j = 0; j
                    < returnMe[i].length; j++) {
                returnMe[i][j] = dumb[i][j] > threshold;

            }
        }
        return returnMe;

    }

    public void drawStaffsTrue() {
        drawStaffs = true;
        mp.repaint();

    }

    public void drawMeasuresTrue() {
        drawMeasures = true;
        mp.repaint();

    }

    public void drawMatricesTrue() {
        drawMatrices = true;
        mp.repaint();

    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;

    }

    public void upArrow() {
        if (yNoteSelected > 0) {
            yNoteSelected--;

        }
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);

    }

    public void downArrow() {
        if (staffs.get(staffSelected).subImgs[0].length - 1 > yNoteSelected) {
            yNoteSelected++;

        }
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);

    }

    public void leftArrow() {
        if (xNoteSelected > 0) {
            xNoteSelected--;
        }
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);

    }

    public void rightArrow() {
        if (staffs.get(staffSelected).subImgs.length - 1 > xNoteSelected) {
            xNoteSelected++;

        }
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);

    }

    void backDetectedNote() {
//        if (detectedNoteSelected > 0) {
//            detectedNoteSelected--;
//        }
//        xNoteSelected = detectedNotes.get(detectedNoteSelected).coors[1];
//        yNoteSelected = detectedNotes.get(detectedNoteSelected).coors[2];
//        staffSelected = detectedNotes.get(detectedNoteSelected).coors[0];
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);
    }

    void forwardDetectedNote() {
//        if (detectedNoteSelected < detectedNotes.size() - 1) {
//            detectedNoteSelected++;
//        }
//        xNoteSelected = detectedNotes.get(detectedNoteSelected).coors[1];
//        yNoteSelected = detectedNotes.get(detectedNoteSelected).coors[2];
//        staffSelected = detectedNotes.get(detectedNoteSelected).coors[0];
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);
    }

    public void upStaff() {
        if (staffSelected > 0) {
            staffSelected--;
            xNoteSelected = 0;
            yNoteSelected = 0;

        }
        newNoteFrame();
    }

    public void downStaff() {
        if (staffSelected < staffs.size() - 1) {
            staffSelected++;
            xNoteSelected = 0;
            yNoteSelected = 0;

        }
        newNoteFrame();
    }

    public void newNoteFrame() {
        if (noteAnalyser.graph.length != staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image.length
                || noteAnalyser.graph[0].length != staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image[0].length) {
            noteAnalyser
                    = new NotePanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);
            noteAnalyserFrame
                    .add(noteAnalyser, BorderLayout.CENTER);
            noteAnalyserFrame
                    .setSize(noteAnalyser.getHeight(), noteAnalyser.getWidth() + 22);

        }
        noteAnalyser.setPanel(staffs.get(staffSelected).subImgs[xNoteSelected][yNoteSelected].image);
    }

    char idNote(int yVal) {
        return "GFEDCBA".charAt(yVal % 7);
    }

    int idOctave(int yVal) {
        return 4 - (int) (yVal / 7);
    }

    void addSymbol(String info) {
        Staff currentStaff = staffs.get(staffSelected);

        Note note = new Note();
        if (currentStaff.selected.equals("Note")) {
            note.pitch = idNote(yNoteSelected);
            note.x = currentStaff.subImgs[xNoteSelected][yNoteSelected].startX;
            note.y = currentStaff.subImgs[xNoteSelected][yNoteSelected].startY;
            note.octave = idOctave(yNoteSelected);
        }

        int measureNum = -1;

        for (int i = 0; i
                < currentStaff.measures.size(); i++) {
            Measure measure = currentStaff.measures.get(i);

            if (measure.xCoorStart < currentStaff.subImgs[xNoteSelected][yNoteSelected].startX
                    && measure.xCoorEnd > currentStaff.subImgs[xNoteSelected][yNoteSelected].startX) {
                measureNum = i;

            }
        }

        if (currentStaff.selected.equals("Note")) {
            currentStaff.measures.get(measureNum).addNote(note);
        }
        System.out.println(currentStaff.toString());

        String dataType = Math.random() < .1 ? "/test/" : "/train/";
        String label = assignLabel(currentStaff.selected, info);
        System.out.println("label: "+label);

        dir = new File("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/Data/" + currentStaff.selected + dataType + label);
        if (currentStaff.selected.equals("TimeSig")){
            dir = new File("Data/" + currentStaff.selected + dataType);
        }
        int currentNum = dir.list().length;
        String zero = (currentNum < 100) ? (currentNum < 10 ? "00" : "0") : "";
        String fileName = label + "_" + zero + currentNum + ".jpg";
        File actualFile = new File(dir, fileName);
        System.out.println(actualFile.getAbsolutePath());
        int[][] toWrite = this.toInt(currentStaff.subImgs[xNoteSelected][yNoteSelected].image);
        int[] scale = getScale(currentStaff.selected);
        toWrite = ConvertToImage.scale(toWrite, scale[0], scale[1]);
        ConvertToImage.convert(toWrite, actualFile);

        if (currentStaff.selected.equals("Note")) {
            String labelF = assignLabel("NoteFull", info);
            System.out.println("NoteFull" + dataType + labelF);
            dir = new File("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/Data/" + "NoteFull" + dataType + labelF);
            int currentNumF = dir.list().length;
            String zeroF = (currentNumF < 100) ? (currentNumF < 10 ? "00" : "0") : "";
            String fileNameF = labelF + "_" + zeroF + currentNumF + ".jpg";
            File actualFileF = new File(dir, fileNameF);
            int[][] toWriteF = this.toInt(currentStaff.nfSubImgs[xNoteSelected][yNoteSelected].image);
            int[] scaleF = getScale("NoteFull");
            toWriteF = ConvertToImage.scale(toWriteF, scaleF[0], scaleF[1]);
            ConvertToImage.convert(toWriteF, actualFileF);

            if (Double.parseDouble(labelF)<=4){
                String labelQ = assignLabel("Quarter", info);
                System.out.println("Quarter" + dataType + labelQ);
                dir = new File("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/Data/" + "Quarter" + dataType + labelQ);
                int currentNumQ = dir.list().length;
                String zeroQ = (currentNumQ < 100) ? (currentNumQ < 10 ? "00" : "0") : "";
                String fileNameQ = labelQ + "_" + zeroQ + currentNumQ + ".jpg";
                File actualFileQ = new File(dir, fileNameQ);
                int[][] toWriteQ = this.toInt(currentStaff.nfSubImgs[xNoteSelected][yNoteSelected].image);
                int[] scaleQ = getScale("NoteFull");
                toWriteQ = ConvertToImage.scale(toWriteQ, scaleQ[0], scaleQ[1]);
                ConvertToImage.convert(toWriteQ, actualFileQ);
            }
        }

        measureNum++;
        String output = "Note in Staff " + staffSelected + 1
                + " in Cell (" + xNoteSelected + ", " + yNoteSelected
                + "), Measure:" ;
        //System.out.println(output);
        outputFrame.notesOutput.append("Added: " + currentStaff.selected
                + ", Info: " + info
                + ",  Filepath:"+ actualFile 
                + ", Loc: (" + xNoteSelected + ", " + yNoteSelected + ")\n");

    }

    void createSubImages() {

        noteAnalyser = new NotePanel(staffs.get(0).subImgs[xNoteSelected][yNoteSelected].image);
        noteAnalyserFrame  = new NoteFrame(noteAnalyser);
        outputFrame = new OutputFrame();
        outputFrame.notesOutput.append("Controls:\n\n\rMove \t\t Arrow Keys\nMove Up Staff \t\tq"
                + "\nMove Down Staff \t a\nSave Symbol \t\t Enter"
                + "\nBegin Selection \t [\nClose Selection \t ]");

    }

    public void filterSubImgs() {
        System.out.println("filtering...");
        for (int index = 0; index<4; index++) {
            ArrayList<IndexItem> currentDetections = new ArrayList<>();
            double ratioThreshold = setThreshold(detections.getType(index));
            for (Staff staff : staffs) {
                SubImage[][] subImgs = Detected.getSubImgs(staff,index);
                for (int i = 0; i < subImgs.length; i++) {
                    for (int j = 0; j < subImgs[i].length; j++) {
                        SubImage subImg = subImgs[i][j];

                        double imgTotal = 0;

                        for (boolean[] row : subImg.image) {
                            for (boolean pixel : row) {
                                imgTotal += pixel ? 0 : 1;

                            }
                        }
                        double ratio = imgTotal / (subImg.image.length * subImg.image[0].length);
                        subImgs[i][j].ratio = ratio;

                        if (ratio > ratioThreshold) {
                            Integer[] coors = {staff.id, i, j};
                            currentDetections.add(new IndexItem(coors));
                        }
                    }
                }
            }
            mp.repaint();
            detections.set(index,currentDetections);
        }
        System.out.println("filtered.\n");
    }

    private double setThreshold(String type) {
        switch (type){
            case "Accidental":
                return .2;
            case "Rest":
                return .1;
            case "Note":
                return .2;
            case "Clef":
                return .1;
            case "TimeSig":
                return .3;
        }
        return 0;
    }

    public void detectNotes(){
//        try {

            System.out.println("loading nets...");
            try {
                MusicNet.load();
            } catch (Exception e){
                System.out.println("loaded incorrectly");
            }
            System.out.println("loaded.\n");
            for (int i = 0; i<4;i++) {
                ArrayList<IndexItem> realDetectedNotes = new ArrayList<>();
                System.out.println("********************** Net: "+detections.getType(i)+" **********************");
                System.out.println("detecting...");
                int count = 0;
                for (IndexItem coors : detections.get(i)) {
                    SubImage[][] subImgs = Detected.getSubImgs(staffs.get(coors.coors[0]),i);
                    int[][] preImg = this.toInt(subImgs[coors.coors[1]][coors.coors[2]].image);
                    int[] scale = getScale(detections.getType(i));
                    preImg = ConvertToImage.scale(preImg, scale[0], scale[1]);
                    Image img = ConvertToImage.convert(preImg);
                    String ans = "other";
                    try {
                        ans = MusicNet.pass(img, detections.getType(i));
                    } catch (Exception e){
                        System.out.println("pass error on "+detections.getType(i)+", staff "+coors.coors[0]);
                    }
                    if (!ans.equals("other")) {
                        count += ans.length();
                        if (count>100){
                            System.out.println();
                            count = 0;
                        }
                        System.out.print(ans+", ");
                        coors.setLabel(ans);
                        realDetectedNotes.add(coors);
                    }
                }
                System.out.println("\ndetections complete. \n\nadding to list...");

                detections.set(i, realDetectedNotes);
                mp.repaint();
                System.out.println("added\n");
            }


        System.out.println("quarter analysis");

        System.out.println("********************** Net: "+detections.getType(4)+" **********************");
        System.out.println("detecting...");
        int count = 0;
        ArrayList<IndexItem> realDetectedNotes = new ArrayList<>();
        for (IndexItem coors : detections.noteDetected) {
            if (coors.label.equals("quarter")) {
                SubImage[][] subImgs = staffs.get(coors.coors[0]).nfSubImgs;
                int[][] preImg = this.toInt(subImgs[coors.coors[1]][coors.coors[2]].image);
                int[] scale = getScale("NoteFull");
                preImg = ConvertToImage.scale(preImg, scale[0], scale[1]);
                Image img = ConvertToImage.convert(preImg);
                String ans = "other";
                try {
                    ans = MusicNet.pass(img, "Quarter");
                } catch (Exception e) {
                    System.out.println("pass error on " + "Quarter" + ", staff " + coors.coors[0]);
                }
                if (!ans.equals("other")) {
                    count+=ans.length();
                    if (count > 100) {
                        System.out.println();
                        count = 0;
                    }

                    System.out.print(ans + ", ");
                    coors.setLabel(ans);
                    realDetectedNotes.add(coors);
                }
            }
        }

        for (IndexItem coors : detections.noteDetected){
            if (!coors.label.equals("quarter")){
                realDetectedNotes.add(coors);
            }
        }
        detections.noteDetected = realDetectedNotes;



//        } catch (Exception e){
//            System.out.println("Is a no go");
//        }

        if (staffs.get(staffSelected).selected.equals("Clef")) {
            for (Staff staff : staffs) {
                System.out.println("Staff " + staff.id + ": " + staff.clef+" clef");
            }
        }
        detections.restDetected = cleanDetectionsRest(detections.restDetected);
        detections.noteDetected = cleanDetectionsNote(detections.noteDetected);
        assignClefs(detections.clefDetected);
//        assignAccidentals();
        mp.repaint();
    }

    public ArrayList<IndexItem> cleanDetectionsRest(ArrayList<IndexItem> detections){
        ArrayList<IndexItem> realDetectedNotes = new ArrayList<>();
        for (int i = 0; i< detections.size(); i++){
            IndexItem coors = detections.get(i);
            Rectangle box1 = staffs.get(coors.coors[0]).restImgs.matrix[coors.coors[1]][coors.coors[2]];
            ArrayList<IndexItem> overlaps = new ArrayList<>();
            for (IndexItem testCoors:detections){
                if (testCoors.coors[0].equals(coors.coors[0])) {
                    Rectangle box2 = staffs.get(testCoors.coors[0]).restImgs.matrix[testCoors.coors[1]][testCoors.coors[2]];
                    if (box1.intersects(box2)){
                        overlaps.add(testCoors);
                    }
                }
            }
            IndexItem max = coors;
            for (IndexItem check:overlaps){
                if (max.label.equals("1/8") && check.label.equals("3/16")
                || (max.label.equals("1") && check.label.equals("1 1/2")
                || (max.label.equals(check.label)))){
                    max = check;
                }
            }
            if (!realDetectedNotes.contains(max)){
                realDetectedNotes.add(max);
            }
        }
        return realDetectedNotes;
    }

    public ArrayList<IndexItem> cleanDetectionsNote(ArrayList<IndexItem> detections){
        ArrayList<IndexItem> realDetectedNotes = new ArrayList<>();
        for (int i = 0; i< detections.size(); i++){
            IndexItem coors = detections.get(i);
            Rectangle box1 = staffs.get(coors.coors[0]).noteImgs.matrix[coors.coors[1]][coors.coors[2]];
            ArrayList<IndexItem> overlaps = new ArrayList<>();
            for (IndexItem testCoors:detections){
                if (testCoors.coors[0].equals(coors.coors[0])) {
                    Rectangle box2 = staffs.get(testCoors.coors[0]).noteImgs.matrix[testCoors.coors[1]][testCoors.coors[2]];
                    if (box1.intersects(box2) && coors.coors[2].equals(testCoors.coors[2])){
                        overlaps.add(testCoors);
                    }
                }
            }
            IndexItem max = coors;
            for (IndexItem check:overlaps){
                if (max.label.equals("1/8") && check.label.equals("3/16")
                    || (max.label.equals("1") && check.label.equals("1 1/2")
                    || (max.label.equals(check.label)))){
                    max = check;
                }
            }
            if (!realDetectedNotes.contains(max)){
                realDetectedNotes.add(max);
            }
        }
        return realDetectedNotes;
    }

    public void assignClefs(ArrayList<IndexItem> detections){
        for (IndexItem item: detections){
            staffs.get(item.coors[0]).clef = item.label;
        }
    }

    public void addNotesToStaff(){
        for (IndexItem coors: detections.noteDetected){
            staffs.get(coors.coors[0]).notes.add(coors);
        }
        for (IndexItem coors: detections.restDetected){
            staffs.get(coors.coors[0]).rests.add(coors);
        }
        for (IndexItem coors: detections.accidentalDetected){
            staffs.get(coors.coors[0]).accidentals.add(coors);
        }
        for (Staff staff:staffs){
            staff.addAllNotes();
            System.out.println(staff.toString());
        }
        printMusic();
    }

    public void setPanel() {
        for (int i = 0; i
                < width; i++) {
            for (int j = 0; j
                    < height; j++) {
                double c = blackWhite[i][j] ? 255 : 0;
                graph[i][j] = new Square(i, j, new double[]{c, c, c});
                graph[i][j].setRatio(ratio);

            }
        }
    }

    public void paint(Graphics g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graph[i][j].paint((Graphics2D) g);

            }
        }
        if (drawStaffs) {
            drawStaffs((Graphics2D) g);

        }
        if (drawMeasures) {
            drawMeasures((Graphics2D) g);

        }
        if (drawMatrices) {
            drawMatrices((Graphics2D) g);
        }
    }

    void generateSheet() {
        SheetMusicPanel panel = new SheetMusicPanel(staffs);
        new SheetMusicFrame(panel);
    }

    void    changeSymbol(String symbol) {
        for (Staff staff : staffs) {
            staff.currentGrid(symbol);
        }
        System.out.println("|" + symbol + "|");
        if (xNoteSelected > staffs.get(staffSelected).matrix.length
                || yNoteSelected > staffs.get(staffSelected).matrix[0].length) {
            xNoteSelected = 0;
            yNoteSelected = 0;
        }
        mp.repaint();
        newNoteFrame();
    }

    void startSelection() {
        selecting = true;
        areaSelection = new int[]{xNoteSelected, yNoteSelected};
        mp.repaint();
    }

    void completeSelection(String info) {
        if (selecting) {
            selecting = false;
            addSelection(xNoteSelected, yNoteSelected, info);
            mp.repaint();
        }
    }

    void addSelection(int x2, int y2, String info) {
        for (int i = areaSelection[0]; i < x2; i++) {
            for (int j = areaSelection[1]; j < y2; j++) {
                xNoteSelected = i;
                yNoteSelected = j;
                addSymbol(info);
            }
        }
    }

    private String assignLabel(String type, String info) {
        if (info.contains("o")) {
            return info;
        } else {
            switch (type) {
                case "Accidental":
                    return info;
                case "Clef":
                    return info;
                case "Rest":
                    return "" + (int) (Double.parseDouble(info) * 4);
                case "Note":
                    Scanner sN = new Scanner(info);
                    int len = (int) (sN.nextDouble() * 4);
                    return "" + (len < 8 ? "q" : (len < 16 ? "h" : "w"));
                case "Quarter":
                    Scanner sQ = new Scanner(info);
                    String qLen = ""+(int) (sQ.nextDouble() * 4);
                    if (qLen.contains("1")||qLen.contains("2")){
                        if (info.contains("b")) { qLen+="b"; }
                        if (info.contains("h")) { qLen+="h"; }
                    } return qLen;
                case "NoteFull":
                    Scanner sNF = new Scanner(info);
                    return "" + (int) (sNF.nextDouble() * 4);
                case "TimeSig":
                    return info;
                default:
                    return "";
            }
        }
    }

    private int[] getScale(String type) {
        switch (type) {
            case "Accidental":
                return new int[]{36, 108};
            case "TimeSig":
                return new int[]{25, 56};
            case "Clef":
                return new int[]{36, 108};
            case "Rest":
                return new int[]{20, 48};
            case "Note":
                return new int[]{26, 18};
            case "NoteFull":
                return new int[]{37, 120};
            default:
                return new int[]{50, 50};
        }
    }

    public void playStaffs(){
        for (Staff staff: staffs){
            staff.play();
        }
    }

    public void printMusic(){
        System.out.print("\n##40400");
        for (Staff staff:staffs){
            System.out.print(staff.ouputString());
        }
        System.out.println("\n");
    }

}
