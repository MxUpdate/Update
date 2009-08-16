/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.mxupdate.util.JPOHandler_mxJPO.ClassFile;

/**
 * The JPO is used to install the Mx Update JPO file package. Because the Mx
 * update JPO classes are using imports and Matrix could not handle imports of
 * JPO classes, the complete MQL insert is rewritten.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Insert_mxJPO
{
    /**
     * Name of the JPO property holding the last modified date of the file.
     */
    private static final String PROP_FILEDATE = "file date";

    /**
     * Name of the JPO property holding the information if a MxUpdate JPO is
     * already compiled. The value <i>false</i> means that a complete compile
     * with force update must be done.
     */
    private static final String PROP_COMPILED = "compiled";

    /**
     * MQL command line to list the installed MxUpdate JPOs and the depending
     * last modified date of the installed file.
     *
     * @see #evaluteInstalledJPOs(Context)
     */
    private static final String CMD_LISTJPOS
            = "list prog \"MxUpdate,org.mxupdate.*,net.sourceforge.mxupdate.*\" "
                    + "select name isjavaprogram property[" + Insert_mxJPO.PROP_FILEDATE + "].value "
                    + "dump \"\t\"";

    /**
     * Defines the date / time format used for the MxUpdate JPOs.
     */
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static  {
        Insert_mxJPO.DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    }

    /**
     * Defines the date format used for the installation date.
     */
    static final SimpleDateFormat DATE_INSTALLED = new SimpleDateFormat("MM-dd-yyyy");
    static  {
        Insert_mxJPO.DATE_INSTALLED.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    }

    /**
     *
     * @param _context  context for this request
     * @param _args     arguements from the MX console including as first
     *                  parameter the root path and as second parameter the
     *                  MxUpdate version
     * @throws Exception if installation of the MxUpdate JPOs failed
     */
    public void mxMain(final Context _context,
                       final String[] _args)
            throws Exception
    {
        final File rootPath = new File(_args[0]);
        final String version = _args[1];

        // get installed JPOs
        final Map<String,Date> installedProgs = this.evaluteInstalledJPOs(_context);

        final org.mxupdate.util.JPOHandler_mxJPO jpoHandler
                = new org.mxupdate.util.JPOHandler_mxJPO(rootPath);

        // delete obsolete JPOs
        final Set<String> newJPOs = jpoHandler.getJpoNames();
        for (final String progName : installedProgs.keySet())  {
            if (!newJPOs.contains(progName))  {
System.out.println("delete jpo '" + progName + "'");
                this.execMql(_context,
                        new StringBuilder()
                                .append("delete program \"").append(this.convertMql(progName)).append('\"'));
            }
        }

        // create new / update JPOs
        for (final ClassFile classFile : jpoHandler.getClassFiles())  {
            // install all JPOs from current package
            if (!installedProgs.containsKey(classFile.getJpoName()))  {
System.out.println("install jpo '" + classFile.getJpoName() + "'");
                this.update(_context, classFile, version);
            } else  {
                final Date mxDate = installedProgs.get(classFile.getJpoName());
                if ((mxDate == null) || !mxDate.equals(classFile.getLastModified()))  {
System.out.println("update jpo '" + classFile.getJpoName() + "'");
                    this.update(_context, classFile, version);
                }
            }
        }
    }

    /**
     * Searches for the installed JPOs and returns them including the
     * information about the last modified date of the installed file. If a
     * program is found which is not a JPO and does not have the extension
     * <code>.properties</code>, an information is printed that the program is
     * ignored.
     *
     * @param _context  context for this request
     * @return map of already installed JPOs and the last modified date of the
     *         installed file
     * @throws MatrixException if the installed JPOs could not be evaluated
     * @see #CMD_LISTJPOS
     */
    protected Map<String,Date> evaluteInstalledJPOs(final Context _context)
            throws MatrixException
    {
        final String jpos = this.execMql(_context, Insert_mxJPO.CMD_LISTJPOS);
        final Map<String,Date> installedProgs = new TreeMap<String,Date>();
        for (final String oneJPO : jpos.split("\n"))  {
            final String[] oneJPOArr = oneJPO.split("\t");
            final String name = oneJPOArr[0];
            final String isJava = (oneJPOArr.length > 1) ? oneJPOArr[1].trim() : "";
            final String modDate = (oneJPOArr.length > 2) ? oneJPOArr[2] : "";
            if ("TRUE".equalsIgnoreCase(isJava))  {
                Date mxDate;
                try  {
                    mxDate = Insert_mxJPO.DATE_FORMAT.parse(modDate);
                } catch (final ParseException e)  {
                    mxDate = null;
                }
                installedProgs.put(name, mxDate);
            } else if (!name.endsWith(".properties")) {
System.out.println("program '" + name + "' is ignored because not a JPO");
            }
        }
        return installedProgs;
    }

    /**
     * Because there is a different behavior for backslashes between MX
     * versions, the JPO program must be included. So the source code is
     * only updated so that the source code includes for all used classes
     * the depending package names.
     *
     * @param _context      context for this request
     * @param _classFile    class file instance (represents the JPO which must
     *                      be updates)
     * @param _version      application version
     * @throws IOException if the JPO file could not be read
     * @throws MatrixException if the JPO could not updated
     * @see #getCode(Map)
     */
    public void update(final Context _context,
                       final ClassFile _classFile,
                       final String _version)
            throws IOException, MatrixException
    {
        final CharSequence code = _classFile.getCode();

        final File tmpInqFile = File.createTempFile(_classFile.getJpoFileName(), "");
        try  {
            tmpInqFile.delete();
            tmpInqFile.mkdir();

            final File file = new File(tmpInqFile, _classFile.getJpoFileName());

            try  {
                final Writer outTCL = new FileWriter(file);
                outTCL.append(code.toString().trim());
                outTCL.flush();
                outTCL.close();

                this.execMql(_context,
                        new StringBuilder()
                            .append("escape insert program \"")
                                            .append(this.convertMql(file.toString().replaceAll("\\\\", "/")))
                                            .append("\";")
                            .append("escape mod program \"")
                                            .append(this.convertMql(_classFile.getJpoName())).append('\"')
                                    .append(" add property \"")
                                            .append(this.convertMql(Insert_mxJPO.PROP_COMPILED))
                                            .append("\" value \"false\";")
                            .append("escape mod program \"")
                                            .append(this.convertMql(_classFile.getJpoName())).append('\"')
                                    .append(" add property \"")
                                            .append(this.convertMql(Insert_mxJPO.PROP_FILEDATE)).append("\" value \"")
                                            .append(this.convertMql(Insert_mxJPO.DATE_FORMAT
                                                    .format(_classFile.getLastModified())))
                                            .append('\"'));
            } finally  {
                file.delete();
            }
        } finally  {
            tmpInqFile.delete();
        }
    }

    /**
     * Executes given MQL command and returns the trimmed result of the MQL
     * execution.
     *
     * @param _context              MX context for this request
     * @param _cmd                  MQL command to execute
     * @return trimmed result of the MQL execution
     * @throws MatrixException if MQL execution failed; includes the MQL
     *                         command if <code>_includeMQLCommand</code> is
     *                         set to <i>true</i>
     */
    protected String execMql(final Context _context,
                             final CharSequence _cmd)
            throws MatrixException
    {
        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(_context, _cmd.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  {
            throw new MatrixException(mql.getError()
                    + "\nMQL command was:\n" + _cmd);
        }
        return mql.getResult().trim();
    }

    /**
     * Converts given string by escaping the &quot; so that in escape mode on
     * string could be handled with &quot; and '.
     *
     * @param _text     character stream to convert
     * @return converted string
     */
    public String convertMql(final CharSequence _text)
    {
        return (_text != null)
               ? _text.toString().replaceAll("\\\\", "\\\\\\\\")
                                 .replaceAll("\\\"", "\\\\\"")
               : "";
    }
}
