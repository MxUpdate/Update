/*
 *  This file is part of MxUpdate <http://www.mxupdate.org>.
 *
 *  MxUpdate is a deployment tool for a PLM platform to handle
 *  administration objects as single update files (configuration item).
 *
 *  Copyright (C) 2008-2016 The MxUpdate Team - All Rights Reserved
 *
 *  You may use, distribute and modify MxUpdate under the terms of the
 *  MxUpdate license. You should have received a copy of the MxUpdate
 *  license with this file. If not, please write to <info@mxupdate.org>,
 *  or visit <www.mxupdate.org>.
 *
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

import org.mxupdate.util.JPOHandler_mxJPO.ClassFile;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * The JPO is used to install the Mx Update JPO file package. Because the Mx
 * update JPO classes are using imports and Matrix could not handle imports of
 * JPO classes, the complete MQL insert is rewritten.
 *
 * @author The MxUpdate Team
 */
public class Insert_mxJPO
{
    /** Name of the JPO property holding the last modified date of the file. */
    private static final String PROP_FILEDATE = "file date";
    /** Name of the JPO property holding the information if a MxUpdate JPO is already compiled. The value <i>false</i> means that a complete compile with force update must be done. */
    private static final String PROP_COMPILED = "compiled";
    /** Name of the properties file program. */
    private static final String NAME_PROPERTY_FiLE = "org.mxupdate.mapping.properties";

    /** Defines the date format used for the installation date. */
    private static final SimpleDateFormat DATE_INSTALLED = new SimpleDateFormat("MM-dd-yyyy");
    static  {
        Insert_mxJPO.DATE_INSTALLED.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    }

    /**
     * Inserts all MxUpdate JPOs which are changed.
     *
     * @param _context  context for this request
     * @param _args     arguments from the MX console:
     *                  <ul>
     *                  <li>first parameter is the root path</li>
     *                  <li>second parameter is the MxUpdate version</li>
     *                  <li>third parameter is the format of the date /
     *                      time</li>
     *                  </ul>
     * @throws Exception if installation of the MxUpdate JPOs failed
     */
    public void mxMain(final Context _context,
                       final String[] _args)
            throws Exception
    {
        final File rootPath = new File(_args[0]);
        final String version = _args[1];
        final SimpleDateFormat dateFormat = new SimpleDateFormat(_args[2]);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00"));

        // get installed JPOs
        final Map<String,Date> installedProgs = this.evaluteInstalledJPOs(_context, dateFormat);

        final org.mxupdate.util.JPOHandler_mxJPO jpoHandler
                = new org.mxupdate.util.JPOHandler_mxJPO(rootPath);

        // delete obsolete JPOs
        final Set<String> newJPOs = jpoHandler.getJpoNames();
        for (final String progName : installedProgs.keySet())  {
            if (!newJPOs.contains(progName))  {
System.out.println("delete jpo '" + progName + "'");
                org.mxupdate.util.MqlBuilderUtil_mxJPO.mql().cmd("escape delete program ").arg(progName).exec(_context);
            }
        }

        // create new / update JPOs
        for (final ClassFile classFile : jpoHandler.getClassFiles())  {
            // install all JPOs from current package
            if (!installedProgs.containsKey(classFile.getJpoName()))  {
System.out.println("install jpo '" + classFile.getJpoName() + "'");
                this.update(_context, classFile, version, dateFormat);
            } else  {
                final Date mxDate = installedProgs.get(classFile.getJpoName());
                // the compare must be done via string compare (because of
                // delivered milliseconds in TCL)
                if ((mxDate == null) || !dateFormat.format(classFile.getLastModified()).equals(dateFormat.format(mxDate)))  {
System.out.println("update jpo '" + classFile.getJpoName() + "'");
                    this.update(_context, classFile, version, dateFormat);
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
     * @param _context      context for this request
     * @param _dateFormat   date / time formatter
     * @return map of already installed JPOs and the last modified date of the
     *         installed file
     * @throws MatrixException if the installed JPOs could not be evaluated
     * @see #CMD_LISTJPOS
     */
    protected Map<String,Date> evaluteInstalledJPOs(final Context _context,
                                                    final SimpleDateFormat _dateFormat)
            throws MatrixException
    {
        final String jpos = org.mxupdate.util.MqlBuilderUtil_mxJPO.mql()
                .cmd("escape list program ").arg("MxUpdate,org.mxupdate.*").cmd(" ")
                        .cmd("select ").arg("name").cmd(" ").arg("isjavaprogram").cmd(" ").arg("property[" + Insert_mxJPO.PROP_FILEDATE + "].value").cmd(" ")
                        .cmd("dump ").arg("\t")
                .exec(_context);

        final Map<String,Date> installedProgs = new TreeMap<>();
        for (final String oneJPO : jpos.split("\n"))  {
            final String[] oneJPOArr = oneJPO.split("\t");
            final String name = oneJPOArr[0];
            final String isJava = (oneJPOArr.length > 1) ? oneJPOArr[1].trim() : "";
            final String modDate = (oneJPOArr.length > 2) ? oneJPOArr[2] : "";
            if ("TRUE".equalsIgnoreCase(isJava))  {
                Date mxDate;
                try  {
                    mxDate = _dateFormat.parse(modDate);
                } catch (final ParseException e)  {
                    mxDate = null;
                }
                installedProgs.put(name, mxDate);
            } else if (!name.equals(Insert_mxJPO.NAME_PROPERTY_FiLE)) {
                installedProgs.put(name, null);
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
     * @param _dateFormat   date / time formatter
     * @throws IOException if the JPO file could not be read
     * @throws MatrixException if the JPO could not updated
     * @see ClassFile#getCode()
     */
    public void update(final Context _context,
                       final ClassFile _classFile,
                       final String _version,
                       final SimpleDateFormat _dateFormat)
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

                org.mxupdate.util.MqlBuilderUtil_mxJPO.mql()
                        .cmd("escape insert program ").arg(file.toString().replaceAll("\\\\", "/"))
                        .exec(_context);
                org.mxupdate.util.MqlBuilderUtil_mxJPO.mql()
                        .cmd("escape modify program ").arg(_classFile.getJpoName())
                                .cmd(" add property ").arg(Insert_mxJPO.PROP_COMPILED).cmd(" value ").arg("false").cmd(" ")
                                .cmd(" add property ").arg(Insert_mxJPO.PROP_FILEDATE).cmd(" value ").arg(_dateFormat.format(_classFile.getLastModified()))
                        .exec(_context);
            } finally  {
                file.delete();
            }
        } finally  {
            tmpInqFile.delete();
        }
    }
}
