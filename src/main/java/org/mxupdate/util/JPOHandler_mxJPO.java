/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The classes handles the source code conversion from &quot;standard&quot;
 * Java classes to source code which could be understand by MX.
 *
 * @author The MxUpdate Team
 */
public class JPOHandler_mxJPO
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
     * @see #evaluateFiles(File, File)
     */
    private static final String JPO_FILE_EXTENSION
            = JPOHandler_mxJPO.JPO_EXTENSION + JPOHandler_mxJPO.JAVA_FILE_EXTENSION;

    /**
     * Regular expression for the package line. The package must be removed
     * from the JPO because Matrix does internally handle all JPOs within the
     * default package.
     *
     * @see ClassFile#getCode()
     */
    private static final Pattern PATTERN_PACKAGE = Pattern.compile("^package [A-Za-z0-9\\.]*;$");

    /**
     * Regular expression for the import statement of JPOs.
     *
     * @see ClassFile#getCode()
     */
    private static final Pattern PATTERN_IMPORT
            = Pattern.compile("(?<=^import[\\ \\t]?)[A-Za-z0-9\\.\\_]*_"+"mxJPO(?=[\\ \\t]?;[\\ \\t]?$)");

    /**
     * Regular expression for a JPO name without a package (but in the front a
     * package is defined). The expression is used to extract the JPO class
     * name from the import definition.
     *
     * @see ClassFile#getCode()
     */
    private static final Pattern PATTERN_JPOWITHOUTPCK = Pattern.compile("(?<=\\.)[A-Za-z0-9]*_"+"mxJPO");

    /**
     * Regular expression for a referenced JPO within the Java source code.
     *
     * @see ClassFile#getCode()
     */
    private static final Pattern PATTERN_JPO
            = Pattern.compile("((?<=[+ \\(\\)\\t\\r\\n@\\<,!])|(^))[A-Za-z0-9\\.]*\\_"+"mxJPO");

    /**
     * Map between the JPO name and the related class file instance.
     */
    private final Map<String,JPOHandler_mxJPO.ClassFile> jpoMap = new TreeMap<String,JPOHandler_mxJPO.ClassFile>();

    /**
     * Map depending on the package name and the related JPO name.
     */
    private final Map<String,Map<String,JPOHandler_mxJPO.ClassFile>> mapPckFiles
            = new TreeMap<String,Map<String,JPOHandler_mxJPO.ClassFile>>();

    /**
     * Holds the maps for each package depending on the class name and the
     * related complete class name including the package.
     *
     * @see #evaluatePackage()
     */
    private final Map<String,Map<String,String>> mapPckLocalNames = new HashMap<String,Map<String,String>>();

    /**
     * Reads from the depending root path all information about all defined
     * classes.
     *
     * @param _rootPath     root path of the all classes
     */
    public JPOHandler_mxJPO(final File _rootPath)
    {
        this.evaluateFiles(_rootPath, new File(""));
        this.evaluatePackage();
    }

    /**
     * Returns the list of all JPO names.
     *
     * @return all defined JPO names
     */
    public Set<String> getJpoNames()
    {
        return this.jpoMap.keySet();
    }

    /**
     * Returns the list of all class files.
     *
     * @return all class files
     */
    public Collection<JPOHandler_mxJPO.ClassFile> getClassFiles()
    {
        return this.jpoMap.values();
    }

    /**
     *
     * @param _rootPath     path of the root (where the JPO code is located)
     * @param _packagePath  path of the package depending on the root path
     */
    protected void evaluateFiles(final File _rootPath,
                                 final File _packagePath)
    {
        final File path = new File(_rootPath, _packagePath.toString());
        for (final File file : path.listFiles())  {
            if (file.isDirectory())  {
                this.evaluateFiles(_rootPath, new File(_packagePath, file.getName()));
            } else if (file.getName().endsWith(JPOHandler_mxJPO.JPO_FILE_EXTENSION))  {
                final JPOHandler_mxJPO.ClassFile classFile = new JPOHandler_mxJPO.ClassFile(_packagePath, file);
                Map<String,JPOHandler_mxJPO.ClassFile> pckFiles = this.mapPckFiles.get(classFile.pckName);
                if (pckFiles == null)  {
                    pckFiles = new TreeMap<String,JPOHandler_mxJPO.ClassFile>();
                    this.mapPckFiles.put(classFile.pckName, pckFiles);
                }
                pckFiles.put(classFile.className, classFile);
                this.jpoMap.put(classFile.jpoName, classFile);
            }
        }
    }

    /**
     * For each package the related set of JPOs is stored. They are needed to
     * identify local defined Java classes.
     *
     * @see #mapPckLocalNames
     */
    protected void evaluatePackage()
    {
        for (final Map.Entry<String,Map<String,JPOHandler_mxJPO.ClassFile>> newPckFilesEntry
                                                                : this.mapPckFiles.entrySet())  {
            // evaluate JPOs from current package
            final Map<String,String> class2Pck = new HashMap<String,String>();
            for (final JPOHandler_mxJPO.ClassFile classFile : newPckFilesEntry.getValue().values())  {
                class2Pck.put(classFile.className, classFile.completeName);
            }

            this.mapPckLocalNames.put(newPckFilesEntry.getKey(), class2Pck);
        }
    }

    /**
     * The class is used to hold the definition of one class file.
     */
    public final class ClassFile
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
        private final String jpoName;

        /**
         * Holds the complete name of the class (package name and class name).
         */
        final String completeName;

        /**
         * Constructor to initialize a single class file.
         *
         * @param _packagePath  path of the package
         * @param _jpoFile      reference to the JPO file
         */
        private ClassFile(final File _packagePath,
                          final File _jpoFile)
        {
            this.jpoFile = _jpoFile;
            this.pckName = _packagePath.getPath()
                                       .replace(File.separatorChar, '.')
                                       .replaceAll("^\\.", "");
            this.className = _jpoFile.getName().replaceAll(JPOHandler_mxJPO.JAVA_FILE_EXTENSION + "$", "");
            this.completeName = "".equals(this.pckName)
                                ? this.className
                                : this.pckName + "." + this.className;
            this.jpoName = this.completeName.replaceAll(JPOHandler_mxJPO.JPO_EXTENSION + "$", "");
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
         * Converts the original Java source code to a source code which could
         * be understand from MX. All
         *
         * @return code in the MQL syntax
         * @throws IOException if the file could not be read
         */
        public CharSequence getCode()
                throws IOException
        {
            final Map<String,String> class2Pck
                    = new HashMap<String,String>(JPOHandler_mxJPO.this.mapPckLocalNames.get(this.pckName));

            final StringBuilder code = new StringBuilder();

            final BufferedReader reader = new BufferedReader(new FileReader(this.jpoFile));
            try  {
                String line = reader.readLine();
                while (line != null)  {
                    final Matcher pckMatch = JPOHandler_mxJPO.PATTERN_PACKAGE.matcher(line);
                    if (pckMatch.find())  {
                        code.append(pckMatch.group()).append('\n');
                    } else  {
                        final Matcher impMatch = JPOHandler_mxJPO.PATTERN_IMPORT.matcher(line);
                        if (impMatch.find())  {
                            final String impClass= impMatch.group();
                            // extract class name from imported name
                            final Matcher classWithoutPckMatch
                                    = JPOHandler_mxJPO.PATTERN_JPOWITHOUTPCK.matcher(impClass);
                            classWithoutPckMatch.find();
                            class2Pck.put(classWithoutPckMatch.group(), impClass);
                            code.append('\n');
                        } else  {
                            final Matcher matcher = JPOHandler_mxJPO.PATTERN_JPO.matcher(line);
                            int start = 0;
                            final StringBuilder newLine = new StringBuilder();
                            while (matcher.find())  {
                                if (this.className.equals(matcher.group()))  {
                                    newLine.append(line.substring(start, matcher.start()))
                                           .append(this.className);
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

                            code.append(newLine.toString()).append('\n');
                        }
                    }
                    line = reader.readLine();
                }
            } finally  {
                reader.close();
            }

            return code;
        }

        /**
         * Returns the name of JPO of this Java class.
         *
         * @return name of the JPO of this Java class
         * @see #jpoName
         */
        public String getJpoName()
        {
            return this.jpoName;
        }

        /**
         * Returns the name of the file without any path.
         *
         * @return name of the file (without any path)
         * @see #jpoFile
         */
        public String getJpoFileName()
        {
            return this.jpoFile.getName();
        }

        /**
         * Returns the name of the package of the file.
         *
         * @return package of the file
         * @see #pckName
         */
        public String getPckName()
        {
            return this.pckName;
        }
    }
}
