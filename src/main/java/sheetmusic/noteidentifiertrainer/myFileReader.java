/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sheetmusic.noteidentifiertrainer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author callumijohnston
 */
public class myFileReader {

    String[] data;

    public myFileReader(String fileName) {
        File file = new File(fileName);
        try {
            ArrayList<String> dataList = new ArrayList<String>();
            Scanner reader = new Scanner(file);
            while (reader.hasNext()) {
                String data = reader.nextLine();
                dataList.add(data);
            }
            reader.close();
            data = new String[dataList.size()];
            for (int i = 0; i < dataList.size(); i++) {
                data[i] = dataList.get(i);
            }
        } catch (Exception e) {
            System.out.println("Problem reading file. " + e);
            System.exit(0);
        }

    }

    public String toString() {
        return Arrays.toString(data);
    }
}
