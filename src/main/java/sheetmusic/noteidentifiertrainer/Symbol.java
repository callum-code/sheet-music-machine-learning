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

/**
 *
 * @author callumjohnston
 */
public abstract class Symbol {
    String id = "";
    int x;
    int y;
    double length;

    
    public String pitch(){
        return null;
    }
    public int octave(){
        return 0;
    }
    /**
     *
     */
    public void paint(Graphics2D g, int x, int y){
        
    }
}
