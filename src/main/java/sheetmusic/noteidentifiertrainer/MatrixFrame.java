/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author callumijohnston
 */
public class MatrixFrame extends JFrame {

    Analysis analysis;

    public MatrixFrame(File imgFile) {
        this.setTitle("Sheet Music Analyzer");
        fileToPng(imgFile);
        ImageReader read = new ImageReader(imgFile);
        analysis = new Analysis(read.BW);
        setLayout(new BorderLayout());
        setVisible(true);
        int x = analysis.width * analysis.ratio;
        int y = analysis.height * analysis.ratio + 22;
        setSize(x, y);
        setLocation(200, 10);
        initComponents();
    }

//    public MatrixFrame(String type, int[][][] matrix) {
//        this.setTitle(type);
//        image = new Analysis(matrix);
//        setLayout(new BorderLayout());   // edited line
//        setVisible(true);
//        int x = image.width * image.ratio;
//        int y = image.height * image.ratio + 22;
//        setSize(x, y);
//        initComponents();
//    }
    private void myKeyPressed(KeyEvent evt) {
        switch (evt.getKeyCode()) {
            case 37:
                analysis.leftArrow();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 39:
                analysis.rightArrow();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 38:
                analysis.upArrow();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 40:
                analysis.downArrow();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 81:
                analysis.upStaff();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 65:
                analysis.downStaff();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 79:
                analysis.backDetectedNote();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 80:
                analysis.forwardDetectedNote();
                analysis.mp.repaint();
                analysis.noteAnalyser.repaint();
                break;
            case 91:
                analysis.startSelection();
                break;
            case 93:
                analysis.completeSelection(getInfo());
                break;
            case 92:
                analysis.detectNotes();
                break;
            case 10:
                String info = getInfo();
                analysis.addSymbol(info);
                break;
            default:

        }
    }

    public String getInfo() {
        String type = analysis.staffs.get(analysis.staffSelected).selected;
        String prompt = "";
        if (type.equals("Note")) {
            prompt = "Please input a note length: ";
        } else if (type.equals("Accidental")) {
            prompt = "Please input \'s\' for sharp, \'f\' for flat, or \'n\' for natural: ";
        } else if (type.equals("Rest")) {
            prompt = "Please input a rest length: ";
        } else if (type.equals("Clef")) {
            prompt = "Please input \'g\' for trebble clef and \'f\' for bass clef";
        } else if (type.equals("TimeSig")) {
            prompt = "Please input the top number and the bottom number separated by a slash (ex: 3/4)";
        }
        String info = JOptionPane.showInputDialog(prompt);
        return info;
    }

    private void myMouseClicked(MouseEvent evt) {
        System.out.println("click");
    }

    private void myMousePressed(MouseEvent evt) {
        //analysis.startSelection(evt.getX(), evt.getY());
        System.out.println("Selection Started");
    }

    private void myMouseReleased(MouseEvent evt) {
        //analysis.completeSelection(evt.getX(), evt.getY(), getInfo());
        System.out.println("Selection Completed");
    }

    private void initComponents() {
        JScrollPane scrollPane = new JScrollPane(analysis.mp, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                myMousePressed(evt);
            }

            public void mouseReleased(MouseEvent evt) {
                myMouseReleased(evt);
            }

            public void mouseClicked(MouseEvent evt) {
                myMouseClicked(evt);
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                myKeyPressed(evt);
            }
        });
    }

    public static String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private File fileToPng(File imgFile) {
        String fileType = getFileExtension(imgFile.getPath());
        switch (fileType) {
            case "png":
                return imgFile;
            case "pdf":
                return null;
            default:
                return imgFile;
        }
    }
}
