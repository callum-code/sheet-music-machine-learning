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
import java.awt.TextArea;
import javax.swing.JFrame;

/**
 *
 * @author callumjohnston
 */
public class OutputFrame extends JFrame{
    
    TextArea notesOutput;
    
    public  OutputFrame() {
        notesOutput = new TextArea();
        setLayout(new BorderLayout()); 
        add(this.notesOutput,BorderLayout.CENTER);
        setLocation(0, 730);
        setSize(240,200);
        setVisible(true);
    }

}
