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

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author callumjohnston
 */
public class NotePanel extends JPanel {

    int height;
    int width;
    Square[][] graph;

    public NotePanel(Square[][] square) {
        this.graph = square;
        height = square.length;
        width = square[0].length;
        setSize(width * 10, height * 10);
    }

    public NotePanel(boolean[][] bw) {
        this.graph = new Square[bw.length][bw[0].length];
        height = graph.length;
        width = graph[0].length;
        int larger = width > height ? width : height;
        setSize(width * 200 / larger, height * 200 / larger);
        setPanel(bw);
    }

    public void setPanel(boolean[][] blackWhite) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double c = blackWhite[i][j] ? 255 : 0;
                graph[i][j] = new Square(i, j, new double[]{c, c, c});
                int larger = width > height ? width : height;
                graph[i][j].setRatio(200 / larger);
            }
        }
        repaint();
    }

    public void paint(Graphics g) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                graph[i][j].paint((Graphics2D) g);
            }
        }
    }
}
