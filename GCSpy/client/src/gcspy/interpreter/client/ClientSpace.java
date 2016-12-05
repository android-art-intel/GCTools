/**
 * * $Id: ClientSpace.java 21 2005-06-11 00:25:23Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.interpreter.client;

import gcspy.comm.BufferedInput;
import gcspy.interpreter.Space;
import gcspy.interpreter.Stream;

/**
 * Represents a client-side space
 *
 * @author Tony Printezis
 */
public class ClientSpace extends Space {

    /** ****************** Utilities ******************* */

    /**
     * Calculate the maximum value in each stream that uses a
     * <code>PRESENTATION_MAX_VAR</code> presentation style
     */
    public void calcMaxima() {
        for (int i = 0; i < streamNum; ++i) {
            Stream stream = getStream(i);
            stream.calcMaxIfNecessary();
        }
    }

    /** ****************** Presentation ******************* */

    /**
     * Represent a tile as a string
     * @param index the tile index
     * @return its string representaton
     */
    public String presentTile(int index) {
        String str = title + index;
        if (!isControlBackground(control[index])) {
            String tileName = tileNames[index];
            str += tileName + "\n";
            str += blockInfo;
        } else {
            str += "\n";
        }
        if (isControlUsed(control[index])) {
            for (int i = 0; i < streamNum; ++i) {
                Stream stream = streams[i];
                // DataAccessor accessor = stream.getAccessor();
                // int val = accessor.get(index);
                // str += stream.presentData(val) + "\n";
                str += stream.presentData(index) + "\n";
            }
        } else if (isControlBackground(control[index])) {
            str += "BACKGROUND";
        } else if (isControlUnused(control[index])) {
            str += unusedString;
        }
        return str;
    }

    /**
     * Represent a summary of the space as a string
     * @param prefix1 A string to prefix the summary
     * @param prefix2 A string to prefix the summary if the style is <code>PRESENTATION_ENUM</code>
     * @return a string representation of the summary
     */
    public String presentSummary(String prefix1, String prefix2) {
        String str = "";
        if (spaceInfo != null)
            str += spaceInfo;
        for (int i = 0; i < streamNum; ++i) {
            Stream stream = streams[i];
            str += stream.presentFullSummary(prefix1, prefix2);
        }
        return str;
    }

    /** ****************** Serialisation / Deserialisation ******************* */

    /**
     * Deserialise this space
     * @param input The BufferedInput to use
     */
    public void deserialise(BufferedInput input) {
        super.deserialise(input);
    }

    /** ****************** Constructors ******************* */

    /** Create s new client space */
    public ClientSpace() {
    }

}
