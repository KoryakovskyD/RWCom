/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwcom;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Дмитрий
 */
public class controllers {
    public static int countDev() {
        int count = 0;
        File dir = new File("/dev");
        File[] arrFiles = dir.listFiles();
        try {
            List<File> lst = Arrays.asList(arrFiles);
            for (File file : lst) {
                if (file.getName().contains("ttyMXUSB")) count++;
            }
        } catch (NullPointerException e) {
            System.out.println("Can't find devices /dev/ttyMXUSB");
            return 0;
        }       
        return count;
    }
}
