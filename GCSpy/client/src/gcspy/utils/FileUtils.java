/**
 * * $Id: FileUtils.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.utils;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.RenderedImage;
import java.io.*;

/**
 * Utilities for file manipulation
 * @author Tony Printezis
 */
public class FileUtils {

    /**
     * Does a file exist.
     * @param fileName
     * @return true if it exists
     */
    static public boolean fileExists(String fileName) {
        // What's wrong with
        // return new FIle(fileName).exists()?
        FileInputStream input;
        try {
            input = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            input.close();
        } catch (IOException e) {
        }
        return true;
    }

    /**
     * Return the lenght of a file
     * @param fileName The file's path name
     * @return Its length
     */
    static public long fileLength(String fileName) {
        File file = new File(fileName);
        return file.length();
    }

    /**
     * A read from an input stream into a byte array
     * @param in The input stream
     * @param array The target array
     * @param offset The offset in the file
     * @param len The number of bytes to read
     * @throws IOException
     */
    static public void read(InputStream in,
                            byte array[], int offset, int len)
            throws IOException {
        int soFar = 0;
        while (soFar < len) {
            int curr = in.read(array, offset + soFar, len - soFar);
            soFar += curr;
        }
    }

    /**
     * Export a RenderedImage to a TIFF file
     * @param image The image
     * @param file The file
     * @param overwrite Should the file be overwritten
     * @return true if the write succeeds
     */
    static public boolean exportToTIFF(RenderedImage image,
                                       File file,
                                       boolean overwrite) {
        return exportToTIFF(image, file.toString(), overwrite);
    }

    /**
     * Export a RenderedImage to a TIFF file
     * @param image The image
     * @param fileName The file's pathe name
     * @param overwrite Should the file be overwritten
     * @return true if the write succeeds
     */
    static public boolean exportToTIFF(RenderedImage image,
                                       String fileName,
                                       boolean overwrite) {
        if (!overwrite && fileExists(fileName))
            return false;

        RenderedOp op = JAI.create("filestore", image, fileName, "tiff");
        return true;
    }

}
