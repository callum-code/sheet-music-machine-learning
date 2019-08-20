/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author callumijohnston
 */
public class Square {

    private int x;
    private int y;
    private int[] c = new int[3];
    private int ratio = 1;

    public Square(int x, int y, double[] c) {
        this.x = x;
        this.y = y;
        for (int i = 0; i < c.length; i++) {
            this.c[i] = (int)c[i];
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int[] getC() {
        return c;
    }

    public void setC(int[] c) {
        this.c = c;
    }

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }
    

    public void paint(Graphics2D g) {
        g.setColor(new Color(c[0], c[1], c[2]));
        g.fillRect(x*ratio, y*ratio, ratio, ratio);
    }
}
