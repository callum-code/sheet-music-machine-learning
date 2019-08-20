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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author callumjohnston
 */
public class SheetMusicPanel extends JPanel{
    
    public ArrayList<Staff> staffs;

    public SheetMusicPanel(ArrayList<Staff> staffs) {
        this.staffs = staffs;
        setSize(1000,700);
    }
    
    public void paint(Graphics g){
        drawStaffs((Graphics2D) g);
        drawMeasures((Graphics2D) g);
        drawNotes((Graphics2D) g);
    }
    
    
    public void drawStaffs(Graphics2D g) {
        g.setColor(Color.black);

        for (Staff staff : staffs) {
            staff.paint(g);

        }
    }
    
    public void drawMeasures(Graphics2D g) {
        g.setColor(Color.black);

        for (Staff staff : staffs) {
            staff.paintMeasures(g);

        }
    }

    private void drawNotes(Graphics2D g) {
        for (Staff staff:staffs){
            for (Measure measure:staff.measures){
                int ratio = (measure.xCoorEnd - measure.xCoorStart)/(measure.symbols.size()+2);
                for (int i = 0; i < measure.symbols.size(); i++) {
                    Symbol symbol = measure.symbols.get(i);
                    int x = measure.xCoorStart + ratio + ratio * i;
                    int y = (int)(staff.startCoors.get(0)[1] + (2 * staff.avgInterval));
                    if (symbol.id.equals("note")){
                        y = (int)(staff.startCoors.get(0)[1] + (getSubY((Note)(symbol)) * (staff.avgInterval/2)));
                    }
                    symbol.paint(g, x, y);
                }
            }
        }
    }
    
    public int getSubY(Note note){
        int y = -1;
        y += "GFEDCBA".indexOf(note.pitch);
        y -= note.octave * 7 - 27;
        return y;
    }

}
