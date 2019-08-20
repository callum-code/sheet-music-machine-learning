/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author callumijohnston
 */
public class MatrixGraphics {

    static JFrame dialogueFrame;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new MatrixFrame().setVisible(true);
//        System.out.println(new ImageReader().getRMatrix());
        dialogueFrame = new JFrame();
        dialogueFrame.setVisible(true);
        dialogueFrame.setSize(400,300);
        dialogueFrame.setLocation(300,300);
        dialogueFrame.setTitle("Main Menu");
        JButton b = new JButton();
        b.setText("Train");
        dialogueFrame.add(b);
        
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                bActionPerformed(evt);
            }
        });
        
        
        
        
    }
    
    private static void bActionPerformed(ActionEvent evt){
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "gif", "png", "tiff");
        fc.setFileFilter(filter);
        File currentFile = new File("/Users/callumjohnston/dl4j-examples/dl4j-examples/dl4j-examples/dl4j-examples/Data/Sheets");
        fc.setCurrentDirectory(currentFile);
        int returnVal = fc.showOpenDialog(dialogueFrame);
        File imgFile = fc.getSelectedFile();
        new MenuFrame(imgFile);
    }

    public static void test() {
        MatrixConverter test = new MatrixConverter("Data/matrixgraphics/image1");
        int xLength = test.matrix.length;
        int yLength = test.matrix[0].length;
        System.out.println("x=" + xLength + "\ny=" + yLength);
        for (int j = 0; j < yLength; j++) {
            for (int i = 0; i < xLength; i++) {
                System.out.print(test.matrix[i][j] + "  ");
            }
            System.out.print("\n");
        }
    }

}
