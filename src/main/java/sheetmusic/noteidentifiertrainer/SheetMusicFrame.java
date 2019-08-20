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

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author callumjohnston
 */
public class SheetMusicFrame extends JFrame{
    
    SheetMusicPanel panel;

    public SheetMusicFrame(SheetMusicPanel panel) {
        this.panel = panel;
        setLayout(new BorderLayout()); 
        add(this.panel);
        setLocation(200, 0);
        setSize(this.panel.getWidth(),this.panel.getHeight()+22);
        setVisible(false);
    }

}
