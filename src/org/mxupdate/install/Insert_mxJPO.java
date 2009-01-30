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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.db.Context;
import matrix.util.MatrixException;

import static org.mxupdate.util.MqlUtil_mxJPO.execMql;
import static org.mxupdate.util.MqlUtil_mxJPO.isEscapeOn;
import static org.mxupdate.util.MqlUtil_mxJPO.setEscapeOff;
import static org.mxupdate.util.MqlUtil_mxJPO.setEscapeOn;

/**
 * The JPO is used to install the Mx Update JPO file package. Because the Mx
 * update JPO classes are using imports and Matrix could not handle imports of
 * JPO classes, the complete MQL insert is rewritten.
 *
 * @author tmoxter
 * @version $Id$
 */
public class Insert_mxJPO
{
    /**
     * File name extension of the JPO.
     */
    private static final String JPO_EXTENSION = "_" + "mxJPO";

    /**
     * File extension of a Java file.
     */
    private static final String JAVA_FILE_EXTENSION = ".java";

    /**
     * File name and file extension together.
     *
     * @see #evaluateFiles(Map, Map, File, File)
     */
    private static final String JPO_FILE_EXTENSION = JPO_EXTENSION + JAVA_FILE_EXTENSION;

    /**
     * Name of the JPO property holding the last modified date of the file.
     */
    private static final String PROP_FILEDATE = "file date";

    /**
     * Name of the JPO property holding the version.
     */
    private static final String PROP_VERSION = "version";

    /**
     * Name of the JPO property holding the installation date.
     */
    private static final String PROP_INSTALLED_DATE = "installed date";

    /**
     * Name of the JPO property holding the installer.
     *
     * @see #VALUE_INSTALLER
     */
    private static final String PROP_INSTALLER = "installer";

    /**
     * Value of the JPO property holding the installer.
     *
     * @see #PROP_INSTALLER
     */
    private static final String VALUE_INSTALLER = "The MxUpdate Team";

    /**
     * Name of the JPO property holding the author.
     *
     * @see #VALUE_AUTHOR
     */
    private static final String PROP_AUTHOR = "author";

    /**
     * Value of the JPO property holding the author.
     *
     * @see #PROP_AUTHOR
     */
    private static final String VALUE_AUTHOR = "The MxUpdate Team";

    /**
     * Name of the JPO property holding the original name.
     */
    private static final String PROP_ORIGINAL_NAME = "original name";

    /**
     * Name of the JPO property holding the application name.
     *
     * @see #VALUE_APPLICATION
     */
    private static final String PROP_APPLICATION = "application";

    /**
     * Value of the JPO property holding the application name.
     *
     * @see #PROP_APPLICATION
     */
    private static final String VALUE_APPLICATION = "MxUpdate";

    /**
     * Regular expression for the package line. The package must be removed
     * from the JPO because Matrix does internally handle all JPOs within the
     * default package.
     *
     * @see ClassFile#getCode(Map)
     */
    private static final Pattern PATTERN_PACKAGE = Pattern.compile("^package [A-Za-z0-9\\.]*;$");

    /**
     *
     *
     * @see ClassFile#getCode(Map)
     */
    private static final Pattern PATTERN_IMPORT = Pattern.compile("(?<=^import[\\ \\t]?)[A-Za-z0-9\\.]*_"+"mxJPO(?=[\\ \\t]?;[\\ \\t]?$)");

    /**
     * Regular expression for a JPO name without a package (but in the front a
     * package is defined). The expression is used to extract the JPO class
     * name from the import definition.
     *
     * @see ClassFile#getCode(Map)
     */
    private static final Pattern PATTERN_JPOWITHOUTPCK = Pattern.compile("(?<=\\.)[A-Za-z0-9]*_"+"mxJPO");

    /**
     *
     *
     * @see ClassFile#getCode(Map)
     */
    private static final Pattern PATTERN_JPO = Pattern.compile("((?<=[+ \\(\\)\\t\\r\\n@\\<,])|(^))[A-Za-z0-9\\.]*\\_"+"mxJPO");

    /**
     * MQL command line to list the installed MxUpdate JPOs and the depending
     * last modified date of the installed file.
     *
     * @see #evaluteInstalledJPOs(Context)
     */
    private static final String CMD_LISTJPOS
            = "list prog \"MxUpdate,org.mxupdate.*,net.sourceforge.mxupdate.*\" "
                    + "select name property[" + PROP_FILEDATE + "].value isjavaprogram "
                    + "dump \"\t\"";

    /**
     * Defines the date / time format used for the MxUpdate JPOs.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static  {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    }

    /**
     * Defines the date format used for the installation date.
     */
    private static final SimpleDateFormat DATE_INSTALLED = new SimpleDateFormat("MM-dd-yyyy");
    static  {
        DATE_INSTALLED.setTimeZone(TimeZone.getTimeZone("GMT+00"));
    }

    /**
     *
     * @param _context  context for this request
     * @param _args
     * @throws Exception
     */
    public void mxMain(final Context _context,
                       final String[] _args)
            throws Exception
    {
        final File rootPath = new File(_args[0]);
        final String version = _args[1];


        // get installed JPOs
        final Map<String,Date> installedProgs = evaluteInstalledJPOs(_context);

        final Map<String,Map<String,ClassFile>> mapPckFiles = new TreeMap<String,Map<String,ClassFile>>();
        final Map<String,ClassFile> jpoMap = new TreeMap<String,ClassFile>();
        evaluateFiles(mapPckFiles, jpoMap, rootPath, new File(""));

        // delete obsolete JPOs
        for (final String progName : installedProgs.keySet())  {
            if (!jpoMap.containsKey(progName))  {
System.out.println("delete jpo '" + progName + "'");
                execMql(_context,
                        new StringBuilder()
                                .append("delete program '").append(progName).append('\''));
            }
        }

        // create new / update JPOs
        final boolean isMqlEscapeOn = isEscapeOn(_context);
        try  {
            setEscapeOn(_context);

            for (final Map.Entry<String,Map<String,ClassFile>> newPckFilesEntry : mapPckFiles.entrySet())  {
                // evaluate JPOs from current package
                final Map<String,String> class2Pck = new HashMap<String,String>();
                for (final ClassFile classFile : newPckFilesEntry.getValue().values())  {
                    class2Pck.put(classFile.className, classFile.completeName);
                }
                // install all JPOs from current package
                for (final ClassFile classFile : newPckFilesEntry.getValue().values())  {
                    if (!installedProgs.containsKey(classFile.jpoName))  {
System.out.println("install jpo '" + classFile.jpoName + "'");
                        classFile.create(_context, class2Pck, version);
                    } else  {
                        final Date mxDate = installedProgs.get(classFile.jpoName);
                        if ((mxDate == null) || !mxDate.equals(classFile.getLastModified()))  {
System.out.println("update jpo '" + classFile.jpoName + "'");
                            classFile.update(_context, class2Pck, version);
                        }
                    }
                }
            }
        }
        finally
        {
            if (!isMqlEscapeOn)  {
                setEscapeOff(_context);
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
        final String jpos = execMql(_context, CMD_LISTJPOS);
        final Map<String,Date> installedProgs = new TreeMap<String,Date>();
        for (final String oneJPO : jpos.split("\n"))  {
            final String[] oneJPOArr = oneJPO.split("\t");
            final String name = oneJPOArr[0];
            final String modDate = (oneJPOArr.length > 1) ? oneJPOArr[1] : "";
            final String isJava = (oneJPOArr.length > 2) ? oneJPOArr[2].trim() : "";
            if ("TRUE".equalsIgnoreCase(isJava))  {
                Date mxDate;
                try  {
                    mxDate = DATE_FORMAT.parse(modDate);
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
     *
     * @param _mapPckFiles  map of packages and their JPOs
     * @param _jpoSet       map of all JPO programs
     * @param _rootPath     path of the root (where the JPO code is located)
     * @param _packagePath  path of the package depending on the root path
     */
    protected void evaluateFiles(final Map<String,Map<String,ClassFile>> _mapPckFiles,
                                 final Map<String,ClassFile> _jpoMap,
                                 final File _rootPath,
                                 final File _packagePath)
    {
        final File path = new File(_rootPath, _packagePath.toString());
        for (final File file : path.listFiles())  {
            if (file.isDirectory())  {
                evaluateFiles(_mapPckFiles, _jpoMap, _rootPath, new File(_packagePath, file.getName()));
            } else if (file.getName().endsWith(JPO_FILE_EXTENSION))  {
                final ClassFile classFile = new ClassFile(_packagePath, file);
                Map<String,ClassFile> pckFiles = _mapPckFiles.get(classFile.pckName);
                if (pckFiles == null)  {
                    pckFiles = new TreeMap<String,ClassFile>();
                    _mapPckFiles.put(classFile.pckName, pckFiles);
                }
                pckFiles.put(classFile.className, classFile);
                _jpoMap.put(classFile.jpoName, classFile);
            }
        }
    }


    private class ClassFile
    {
        /**
         * Link to the JPO file.
         */
        final File jpoFile;

        /**
         * Name of the package in which the class is defined.
         */
        final String pckName;

        /**
         * Name of the class without package but with &quot;mxJPO&quot;
         * extension).
         */
        final String className;

        /**
         * Internal MX used name of the JPO (excl. the &quot;mxJPO&quot;
         * extension).
         */
        final String jpoName;

        /**
         * Holds the complete name of the class (package name and class name).
         */
        final String completeName;

        ClassFile(final File _packagePath,
                  final File _jpoFile)
        {
            this.jpoFile = _jpoFile;
            this.pckName = _packagePath.getPath()
                                       .replace(File.separatorChar, '.')
                                       .replaceAll("^\\.", "");
            this.className = _jpoFile.getName().replaceAll(JAVA_FILE_EXTENSION + "$", "");
            this.completeName = "".equals(this.pckName)
                                ? this.className
                                : this.pckName + "." + this.className;
            this.jpoName = this.completeName.replaceAll(JPO_EXTENSION + "$", "");
        }

        /**
         * Returns the modified date of the JPO file.
         *
         * @return modified date of the JPO file
         * @see #jpoFile
         */
        public Date getLastModified()
        {
            return new Date(this.jpoFile.lastModified());
        }

        /**
         * Creates the JPO new and appends the properties for the author,
         * installer, installed date, application and original name.
         *
         * @param _context      context for this request
         * @param _class2Pck    used JPO name within the code and the related
         *                      class name
         * @param _version      application version
         * @throws IOException if the JPO file could not be read
         * @throws MatrixException if the JPO could not created
         * @see #update(Context, Map, String)
         */
        public void create(final Context _context,
                           final Map<String,String> _class2Pck,
                           final String _version)
                throws IOException, MatrixException
        {
            execMql(_context,
                    new StringBuilder()
                    .append("add program '").append(this.jpoName).append("' java ")
                    .append("property \"").append(Insert_mxJPO.PROP_AUTHOR).append("\" value \"")
                            .append(Insert_mxJPO.VALUE_AUTHOR).append("\" ")
                    .append("property \"").append(Insert_mxJPO.PROP_INSTALLER).append("\" value \"")
                            .append(Insert_mxJPO.VALUE_INSTALLER).append("\" ")
                    .append("property \"").append(Insert_mxJPO.PROP_INSTALLED_DATE).append("\" value \"")
                            .append(Insert_mxJPO.DATE_INSTALLED.format(new Date())).append("\" ")
                    .append("property \"").append(Insert_mxJPO.PROP_APPLICATION).append("\" value \"")
                            .append(Insert_mxJPO.VALUE_APPLICATION).append("\" ")
                    .append("property \"").append(Insert_mxJPO.PROP_ORIGINAL_NAME).append("\" value \"")
                            .append(this.jpoName).append("\" "));
            this.update(_context, _class2Pck, _version);
        }

        /**
         * Because there is a different behavior for backslashes between MX
         * versions, the JPO program must be included. So the source code is
         * only updated so that the source code includes for all used classes
         * the depending package names.
         *
         * @param _context      context for this request
         * @param _class2Pck    used JPO name within the code and the related
         *                      class name
         * @param _version      application version
         * @throws IOException if the JPO file could not be read
         * @throws MatrixException if the JPO could not updated
         * @see #getCode(Map)
         */
        public void update(final Context _context,
                           final Map<String,String> _class2Pck,
                           final String _version)
                throws IOException, MatrixException
        {
            final CharSequence code = this.getCode(_class2Pck);

            final File tmpInqFile = File.createTempFile(this.jpoFile.getName(), "");
            try  {
                tmpInqFile.delete();
                tmpInqFile.mkdir();

                final File file = new File(tmpInqFile, this.jpoFile.getName());

                try  {
                    final Writer outTCL = new FileWriter(file);
                    outTCL.append(code.toString().trim());
                    outTCL.flush();
                    outTCL.close();

                    execMql(_context,
                            new StringBuilder()
                                .append("insert program '").append(file.toString().replaceAll("\\\\", "/")).append("';")
                                .append("mod program '").append(this.jpoName).append("'")
                                        .append(" add property \"").append(Insert_mxJPO.PROP_FILEDATE).append("\" value \"")
                                                .append(Insert_mxJPO.DATE_FORMAT.format(getLastModified())).append('\"')
                                        .append(" add property \"").append(Insert_mxJPO.PROP_VERSION).append("\" value \"")
                                                .append(_version).append("\";"));
                } finally  {
                    file.delete();
                }
            } finally  {
                tmpInqFile.delete();
            }
        }

        /**
         *
         * @param _class2Pck    used JPO name within the code and the related
         *                      class name
         * @return code in the MQL syntax
         * @throws IOException if the file could not be read
         */
        private CharSequence getCode(final Map<String,String> _class2Pck)
                throws IOException
        {
            final StringBuilder code = new StringBuilder();

            final Map<String,String> class2Pck = new HashMap<String,String>(_class2Pck);

            final BufferedReader reader = new BufferedReader(new FileReader(this.jpoFile));
            try  {
                String line = reader.readLine();
                while (line != null)  {
                    final Matcher pckMatch = Insert_mxJPO.PATTERN_PACKAGE.matcher(line);
                    if (pckMatch.find())  {
                        code.append(pckMatch.group()).append('\n');
                    } else  {
                        final Matcher impMatch = Insert_mxJPO.PATTERN_IMPORT.matcher(line);
                        if (impMatch.find())  {
                            final String impClass= impMatch.group();
                            // extract class name from imported name
                            final Matcher classWithoutPckMatch = Insert_mxJPO.PATTERN_JPOWITHOUTPCK.matcher(impClass);
                            classWithoutPckMatch.find();
                            class2Pck.put(classWithoutPckMatch.group(), impClass);
                        } else  {
                            final Matcher matcher = Insert_mxJPO.PATTERN_JPO.matcher(line);
                            int start = 0;
                            final StringBuilder newLine = new StringBuilder();
                            while (matcher.find())  {
                                if (className.equals(matcher.group()))  {
                                    newLine.append(line.substring(start, matcher.start()))
                                           .append(className);
                                    start = matcher.start() + matcher.group().length();
                                } else  {
                                    final String clazzName = class2Pck.containsKey(matcher.group())
                                                             ? class2Pck.get(matcher.group())
                                                             : matcher.group();
                                    newLine.append(line.substring(start, matcher.start()))
                                           .append(clazzName);
                                    start = matcher.start() + matcher.group().length();
                                }
                            }
                            newLine.append(line.substring(start, line.length()));

                            code.append(newLine.toString()/*.replaceAll("\\\\", "\\\\\\\\\\\\\\\\").replaceAll("\"", "\\\\\"")*/).append('\n');
                        }
                    }
                    line = reader.readLine();
                }
            } finally  {
                reader.close();
            }

            return code;
        }
    }
}
