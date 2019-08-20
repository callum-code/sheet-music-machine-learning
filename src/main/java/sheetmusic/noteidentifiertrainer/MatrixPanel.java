/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.awt.Graphics;
import javax.swing.GroupLayout;
import javax.swing.JPanel;

/**
 *
 * @author callumijohnston
 */
public class MatrixPanel extends JPanel {

    public boolean[][] blackWhite;
    Analysis analyzer;

    public MatrixPanel(boolean[][] blackWhite, Analysis a) {
        analyzer = a;
        this.blackWhite = blackWhite;
        this.setSize(this.blackWhite.length,this.blackWhite[0].length);
        initComponents();
    }
    
    public void paint(Graphics g){
        analyzer.paint(g);
    }

    private void initComponents() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }

    

}
