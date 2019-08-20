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

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/**
 *
 * @author callumjohnston
 */
public class Note extends Symbol {

    char pitch;
    char accidental;
    int octave;
    int volume;

    public Note() {
        super.id = "note";
    }

    public Note(IndexItem it){
        super.x = it.coors[1];
        super.y = it.coors[2];
        super.length = lenToDouble(it.label);
        super.id = "note";
    }

    public int octave() { return octave; }

    public String pitch() {
        return "" + pitch + accidental;
    }

    public double lenToDouble(String len){
        switch (len){
            case "sixteenth":
                return .25;
            case "eighth":
                return .5;
            case "dotted eighth":
                return .75;
            case "quarter":
                return 1;
            case "half":
                return 2;
            case "whole":
                return 4;
            default:
                return 0;
        }
    }

    public void paint(Graphics2D g, int x, int y) {
        //note head
        if (length < 2) {
            g.fillOval(x, y, 15, 10);
        } else {
            g.setStroke(new BasicStroke(2));
            g.drawOval(x, y, 15, 10);
            g.setStroke(new BasicStroke(1));
        }
        if (length < 4) {
            //note line
            if ((octave - 1) * 7 + pitch < 28) {
                g.drawLine(x + 15, y + 5, x + 15, y - 50);
            } else {
                g.drawLine(x, y + 5, x, y + 50);
            }
            //note tail
            if (length < 1) {
                g.setStroke(new BasicStroke(3));
                if ((octave - 1) * 7 + pitch < 28) {
                    g.drawLine(x + 15, y - 50, x + 25, y - 50 + 12);
                } else {
                    g.drawLine(x, y + 50, x - 5, y + 50 - 12);
                }
                g.setStroke(new BasicStroke(1));
            }
        }

    }

    public String toString() {
        String returnMe = "Note: " + pitch + accidental;
        returnMe += " Octave: " + octave + " Length: " + length;
        return returnMe;
    }

}
