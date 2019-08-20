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

/**
 *
 * @author callumjohnston
 */
public class SubImage {
    boolean[][] image;
    double ratio;
    int startX;
    int startY;

    @Override
    public String toString() {
        String s = "Image:\n";

        for (int i = 0; i
                < image[0].length; i++) {
            for (int j = 0; j
                    < image.length; j++) {
                s += (image[j][i] ? 1 : 0);

            }
            s += "\n";

        }
        s += "\n";
        return s;
    }
    
    
}
