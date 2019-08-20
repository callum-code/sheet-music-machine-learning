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

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 *
 * @author callumjohnston
 */
public class Measure {

    int timeSigTop;
    int timeSigBottom;
    int xCoorStart;
    int xCoorEnd;
    ArrayList<Symbol> symbols = new ArrayList<>();

    public Measure(int xCoorStart) {
        this.xCoorStart = xCoorStart;
    }

    public Measure(int xCoorStart, int xCoorEnd) {
        this.xCoorStart = xCoorStart;
        this.xCoorEnd = xCoorEnd;
    }

    public void addNote(Note note) {
        if (symbols.isEmpty()) {
            symbols.add(note);
        } else if (symbols.get(0).x > note.x) {
            symbols.add(0, note);
        } else if (symbols.get(symbols.size()-1).x < note.x) {
            symbols.add(note);
        } else {
            for (int i = 0; i < symbols.size()-1; i++) {
                if (symbols.get(i).x < note.x && symbols.get(i+1).x > note.x){
                    symbols.add(i+1, note);
                    break;
                }
                if (symbols.get(i).x == note.x){
                    symbols.add(i + 1, note);
                    break;
                }
            }
            if (symbols.get(symbols.size()-1).x == note.x){
                    symbols.add(note);
            }
        }
    }
    

    void paint(Graphics2D g, int startY, int endY) {

        //draw measure lines
        g.drawLine(xCoorStart, startY,
                xCoorStart, endY);
        g.drawLine(xCoorEnd, startY,
                xCoorEnd, endY);

        //draw brackets
        g.drawLine(xCoorStart, startY,
                xCoorStart + 15, startY - 15);
        g.drawLine(xCoorEnd, startY,
                xCoorEnd - 15, startY - 15);
        g.drawLine(xCoorStart, endY,
                xCoorStart + 15, endY + 15);
        g.drawLine(xCoorEnd, endY,
                xCoorEnd - 15, endY + 15);
    }
    
    public String toString(){
        String returnMe = "";
        returnMe += ("Measure: ");
        for (Symbol s: symbols) {
            returnMe += s.toString() + ", ";
        }
        return returnMe;
    }

}
