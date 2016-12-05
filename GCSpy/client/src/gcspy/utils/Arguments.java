/**
 * * $Id: Arguments.java 28 2005-06-20 13:13:35Z rej $
 * * Copyright (c) 2002 Sun Microsystems, Inc.
 * *
 * * See the file "license.terms" for information on usage and redistribution
 * * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 **/

package gcspy.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages the shell arguments passed to the main method
 * @author Tony Printezis
 * @author <a href="http://www/cs/kent.ac.uk">Richard Jones</a>
 */
public class Arguments {

    /** Type for string-valued arguments */
    static public final int STRING_TYPE = 0;
    /** Type for interger-valued arguments */
    static public final int INT_TYPE = 1;
    /** Type for positive integer-valued arguments */
    static public final int POS_INT_TYPE = 2;

    private HashMap<String, Argument> arguments;
    private String error;
    private boolean requiresError;
    private boolean excludedError;

  
  /*
   * Three type of arguments compulsory with parameters (num > 0, compulsory ==
   * true) optional with parameters (num > 0, compulsory == false) toggle (no
   * parameters) (num == 0, compulsory == false)
   */

    private class Argument {
        boolean compulsory;
        int num;
        int types[];
        String requires[];
        String excluded[];
        boolean set;
        Object values[];

        Argument(String name, boolean compulsory, int num, int types[],
                 String requires[], String excluded[], Object values[]) {
            this.compulsory = compulsory;
            this.num = num;
            this.types = types;
            this.requires = requires;
            this.excluded = excluded;
            this.values = values;

            set = false;
        }
    }

    /** Create new arguments */
    public Arguments() {
        arguments = new HashMap<String, Argument>();
        error = null;
    }

    /**
     * Add an argument
     * @param name The name of the argument
     * @param compulsory Whether it is compulsory
     * @param types The types of the argument's parameters (INT, STRING, POS)
     * @param values The values of the argument's parameters
     * @param requires
     * @param excluded
     */
    public void add(String name, boolean compulsory, int types[],
                    Object values[], String requires[], String excluded[]) {
        Argument arg = new Argument(name, compulsory,
                (types != null) ? types.length : 0, types, requires, excluded, values);
        arguments.put(name, arg);
    }

    /**
     * Add an argument (no values set)
     * @param name The name of the argument
     * @param compulsory Whether it is compulsory
     * @param types The types of the argument's parameters (INT, STRING, POS)
     * @param requires
     * @param excluded
     */
    public void add(String name, boolean compulsory, int types[],
                    String requires[], String excluded[]) {
        add(name, compulsory, types, null, requires, excluded);
    }

    /**
     * Add an argument (no requirements or exclusions)
     * @param name The name of the argument
     * @param compulsory Whether it is compulsory
     * @param types The types of the argument's parameters (INT, STRING, POS)
     * @param values The values of the argument's parameters
     */
    public void add(String name, boolean compulsory, int types[], Object values[]) {
        add(name, compulsory, types, values, null, null);
    }

    /**
     * Add an argument (no values, requirements nor exclusions)
     * @param name The name of the argument
     * @param compulsory Whether it is compulsory
     * @param types The types of the argument's parameters (INT, STRING, POS)
     */
    public void add(String name, boolean compulsory, int types[]) {
        add(name, compulsory, types, null);
    }

    /**
     * Add a compulsory  argument (no values)
     * @param name The name of the argument
     * @param requires
     * @param excluded
     */
    public void add(String name, String requires[], String excluded[]) {
        add(name, true, null, null, requires, excluded);
    }

    /**
     * Add a compulsory argument
     * @param name
     */
    public void add(String name) {
        add(name, null, null);
    }

    private void setError(String error) {
        this.error = error;
    }

    private void setError(String name, String error) {
        setError(name + " :  " + error);
    }

    private void appendNotSet(String name) {
        if (error == null) {
            error = "Following argument(s) not set:";
        }
        error += "\n" + "        " + name;
    }

    private void appendRequires(String name, String arg) {
        if (!requiresError) {
            setError(name, "it requires the following argument(s)");
            requiresError = true;
        }
        error += "\n" + "        " + arg;
    }

    private void appendExcluded(String name, String arg) {
        if (!excludedError) {
            setError(name, "it is mutually excluded with the following argument(s)");
            excludedError = true;
        }
        error += "\n" + "        " + arg;
    }

    /**
     * Get the argument error message
     * @return the parsing errors
     * */
    public String getError() {
        return error;
    }

    /**
     * Parse arguments
     * @param args The strings to parse
     * @return true if parsed successfully
     */
    public boolean parse(String args[]) {
        for (int i = 0; i < args.length; ) {
            String name = args[i++];
            if (!arguments.containsKey(name)) {
                setError(name, "unknown argument");
                return false;
            }
            Argument arg = arguments.get(name);
            if (arg.set) {
                setError(name, "argument already set");
                return false;
            }

            if (arg.num > 0) {
                Object values[] = new Object[arg.num];
                for (int j = 0; j < arg.num; ++j) {
                    if (i == args.length) {
                        setError(name, "not enough parameters");
                        return false;
                    }
                    String param = args[i++];

                    switch (arg.types[j]) {
                        case STRING_TYPE:
                            values[j] = param;
                            break;
                        case INT_TYPE:
                            int intVal;
                            try {
                                intVal = Integer.parseInt(param);
                            } catch (NumberFormatException e) {
                                setError(name, "parameter " + (j + 1) + " invalid ('" + param
                                        + "' not an integer)");
                                return false;
                            }
                            values[j] = intVal;
                            break;
                        case POS_INT_TYPE:
                            int posVal;
                            try {
                                posVal = Integer.parseInt(param);
                                if (posVal < 0)
                                    throw new NumberFormatException();
                            } catch (NumberFormatException e) {
                                setError(name, "parameter " + (j + 1) + " invalid ('" + param
                                        + "' not a positive integer)");
                                return false;
                            }
                            values[j] = posVal;
                            break;
                    }
                }
                arg.values = values;
            }
            arg.set = true;
        }

        return check();
    }

    private boolean check() {
        boolean ok = true;
        Set<Map.Entry<String, Argument>> set = arguments.entrySet();
        for (Map.Entry<String, Argument> entry : set) {
            String name = entry.getKey();
            Argument arg = entry.getValue();
            if (!arg.set) {
                if ((arg.num != 0) && (arg.compulsory)) {
                    ok = false;
                    appendNotSet(name);
                }
            } else {
                String requires[] = arg.requires;
                if (requires != null) {
                    for (String require : requires) {
                        Argument arg2 = arguments.get(require);
                        if (!arg2.set) {
              /*
               * this will overwrite any errors set by appendNotSet
               */
                            appendRequires(name, require);
                            ok = false;
                        }
                    }
                    if (!ok)
                        return false;
                }

                String excluded[] = arg.excluded;
                if (excluded != null) {
                    for (String anExcluded : excluded) {
                        Argument arg2 = arguments.get(anExcluded);
                        if (arg2.set) {
              /*
               * this will overwrite any errors set by appendNotSet
               */
                            appendExcluded(name, anExcluded);
                            ok = false;
                        }
                    }
                    if (!ok)
                        return false;
                }
            }
        }
        return ok;
    }

    /**
     * Get an argument's parameter values
     * @param name The name of the argument
     * @return Its parameter values
     */
    public Object[] getValues(String name) {
        Argument arg = arguments.get(name);
        return arg.values;
    }

    /**
     * Is an argument set?
     * @param name The argument's name
     * @return true if it is set
     */
    public boolean isSet(String name) {
        Argument arg = arguments.get(name);
        return arg.set;
    }

}
