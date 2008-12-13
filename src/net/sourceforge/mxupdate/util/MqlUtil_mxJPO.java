/*
 * Copyright 2008 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package net.sourceforge.mxupdate.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

/**
 * The JPO class holds utilities for easy MQL access.
 *
 * @author tmoxter
 * @version $Id$
 */
public final class MqlUtil_mxJPO
{
    /**
     * Dummy constructor so that the MQL utility class could not be
     * instanciated.
     */
    private MqlUtil_mxJPO()
    {
    }

    /**
     * Executes given MQL command and returns the trimmed result of the MQL
     * execution.
     *
     * @param _context  context for this request
     * @param _cmd      MQL command to execute
     * @return trimmed result of the MQL execution
     * @throws MatrixException if MQL execution fails
     */
    public static String execMql(final Context _context,
                                 final CharSequence _cmd)
            throws MatrixException
    {
        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(_context, _cmd.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  {
            throw new MatrixException(mql.getError() + "\nMQL command was:\n" + _cmd);
        }
        return mql.getResult().trim();
    }

    /**
     * Checks if the escape is on.
     *
     * @param _context  context for this request
     * @return <i>true</i> if the escape is on; otherwise <i>false</i>
     * @throws MatrixException
     */
    public static boolean isEscapeOn(final Context _context)
            throws MatrixException
    {
        return "Escape processing is on".equals(execMql(_context, "print escape"));
    }

    /**
     * Sets the escape on.
     *
     * @param _context  context for this request
     * @throws MatrixException if the escape could not be set to on
     */
    public static void setEscapeOn(final Context _context)
            throws MatrixException
    {
        execMql(_context, "set escape on");
    }

    /**
     * Sets the escape off.
     *
     * @param _context  context for this request
     * @throws MatrixException if the escape could not be set to off
     */
    public static void setEscapeOff(final Context _context)
            throws MatrixException
    {
        execMql(_context, "set escape off");
    }

    /**
     * Sets the history on.
     *
     * @param _context  context for this request
     * @throws MatrixException if the history could not be set to on
     */
    public static void setHistoryOn(final Context _context)
            throws MatrixException
    {
        execMql(_context, "history on");
    }

    /**
     * Sets the history off.
     *
     * @param _context  context for this request
     * @throws MatrixException if the history could not be set to off
     */
    public static void setHistoryOff(final Context _context)
            throws MatrixException
    {
        execMql(_context, "history off");
    }

    /**
     * Reads the program with given name and evaluates the code of the program
     * as properties.
     *
     * @param _context      context for this request
     * @param _name         name of the property program to read
     * @throws MatrixException  if the code of the program with given name
     *                          could not be read
     * @throws IOException      if the code of the program could not be parsed
     *                          as properties
     */
    public static Properties readPropertyProgram(final Context _context,
                                                 final String _name)
            throws MatrixException, IOException
    {
        final String code = execMql(_context,
                                    new StringBuilder()
                                            .append("print prog \"").append(_name)
                                            .append("\" select code dump"));

        final InputStream is = new ByteArrayInputStream(code.getBytes());
        final Properties properties = new Properties();
        properties.load(is);

        return properties;
    }
}
