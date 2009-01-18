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

package net.sourceforge.mxupdate.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import matrix.db.Context;
import matrix.util.MatrixException;

import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.AdminPropertyDef;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.AdminTypeDef;
import net.sourceforge.mxupdate.util.Mapping_mxJPO.TypeDef;

import org.xml.sax.SAXException;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.match;
import static net.sourceforge.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * Abstract class from which must be derived for exporting and importing all
 * administration (business) objects.
 *
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractObject_mxJPO
        implements Serializable
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -5505850566853070973L;

    // TODO: must be defined in the Property Class
    protected final static Set<String> IGNORED_PROPERTIES = new HashSet<String>();
    static  {
        IGNORED_PROPERTIES.add("version");
        IGNORED_PROPERTIES.add("installed date");
        IGNORED_PROPERTIES.add("original name");
        IGNORED_PROPERTIES.add("application");
        IGNORED_PROPERTIES.add("installer");
        IGNORED_PROPERTIES.add("author");
    }

    /**
     * Stores the version information of this object. If the value is
     * <code>null</code>, the version information is not defined.
     *
     * @see #getVersion()
     * @see #setVersion(String)
     */
    private String version = null;

    /**
     * Returns the path where the file is located of this matrix object. The
     * method used the information annotation.
     *
     * @return sub path
     * @see #getInfoAnno()
     */
    public String getPath()
    {
        return this.getTypeDef().getFilePath();
    }

    /**
     * Evaluates for this instance of export / import class the related
     * information annotations and returns that. If the information annotation
     * does not exists directly in the class, the information annotation is
     * searched in the super classes.
     *
     * @return instance of the related information annotation
     */
    public final InfoAnno_mxJPO getInfoAnno()
    {
        InfoAnno_mxJPO ret = this.getClass().getAnnotation(InfoAnno_mxJPO.class);
        if (ret == null)  {
            Class<?> clazz = this.getClass().getSuperclass();
            while ((clazz != null) && (ret == null))  {
                ret = clazz.getAnnotation(InfoAnno_mxJPO.class);
                clazz = clazz.getSuperclass();
            }
        }
        return ret;
    }

    /**
     * Returns the type definition instance.
     *
     * @return type definition instance (if administration type definition
     *         exists else business type definition)
     */
    public final TypeDef getTypeDef()
    {
        final InfoAnno_mxJPO infoAnno = this.getInfoAnno();
        return (infoAnno.adminType() != AdminTypeDef.Undef)
               ? infoAnno.adminType()
               : infoAnno.busType();
    }

    /**
     * Returns for the list of strings to match (parameter
     * <code>_matches</code>) the list of matching administration (business)
     * object names.
     *
     * @param _context  context for this request
     * @param _matches  collection of match strings
     * @return set of names of this type matching the collection of strings
     * @throws MatrixException
     */
    public abstract Set<String> getMatchingNames(final Context _context,
                                                 final Collection<String> _matches)
            throws MatrixException;

    /**
     * Export given administration (business) object with given name into given
     * path. The name of the file where is written through is evaluated within
     * this export method.
     *
     * @param _context  context for this request
     * @param _path     path to write through (if required also including
     *                  depending file path defined from the information
     *                  annotation)
     * @param _name     name of object to export
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     */
    public abstract void export(final Context _context,
                                final File _path,
                                final String _name)
            throws MatrixException, SAXException, IOException;

    /**
     * Evaluates for given set of files all matching files and returns them as
     * map (key is the file name, value is the name of the matrix
     * administration (business) object).<br/>
     * If the file name without prefix and suffix matches one of the collection
     * match strings, the file is added to the map and is returned.
     *
     * @param _files            set of files used to found matching files
     * @param _matches          collection of match strings
     * @return map of files (as key) with the related matrix name (as value)
     * @see #getMatchingFileNames(Set)
     */
    public Map<File, String> getMatchingFileNames(final Set<File> _files,
                                                  final Collection<String> _matches)
    {
        final Map<File,String> ret = new TreeMap<File,String>();

        final String suffix = this.getTypeDef().getFileSuffix();
        final int suffixLength = suffix.length();
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;

        for (final File file : _files)  {
            final String fileName = file.getName();
            for (final String match : _matches)  {
                if (((prefix == null) || fileName.startsWith(prefix)) && fileName.endsWith(suffix))  {
                    final String name = fileName.substring(0, fileName.length() - suffixLength)
                                                .substring(prefixLength);
                    if (match(name, match))  {
                        ret.put(file, name);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Checks for all files if the prefix and file extension are fulfilled. For
     * this files, the Matrix name is extracted and returned together with
     * the file in a map.
     *
     * @param _files    files to check if they are defining an update
     * @return map of files (as key) with the related matrix name (as value)
     * @see #getMatchingFileNames(Set, Collection)
     */
    public Map<File, String> getMatchingFileNames(final Set<File> _files)
    {
        final Map<File,String> ret = new TreeMap<File,String>();

        final String suffix = this.getTypeDef().getFileSuffix();
        final int suffixLength = suffix.length();
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = prefix.length();
        for (final File file : _files)  {
            final String fileName = file.getName();
            if (fileName.startsWith(prefix) && fileName.endsWith(suffix))  {
                final String name = fileName.substring(0, fileName.length() - suffixLength)
                                            .substring(prefixLength);
                ret.put(file, name);
            }
        }
        return ret;
    }

    /**
     * Deletes administration object with given name.
     *
     * @param _context      context for this request
     * @param _name         name of object to delete
     * @throws Exception if delete failed
     */
    public abstract void delete(final Context _context,
                                final String _name)
            throws Exception;

    /**
     * Creates a new administration object with given name.
     *
     * @param _context      context for this request
     * @param _file         file with code to create
     * @param _name         name of object to create
     * @throws Exception if create failed
     */
    public abstract void create(final Context _context,
                                final File _file,
                                final String _name)
            throws Exception;

    /**
     * Updated this administration (business) object.
     *
     * @param _context          context for this request
     * @param _name             name of the administration (business) object
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update failed
     */
    public abstract void update(final Context _context,
                                final String _name,
                                final File _file,
                                final String _newVersion)
            throws Exception;

    /**
     * Reads for given file the code and returns them.
     *
     * @param _file     file to read the code
     * @return read code of the file
     * @throws IOException if the file could not be opened or read
     */
    protected StringBuilder getCode(final File _file)
            throws IOException
    {
        // read code
        final StringBuilder code = new StringBuilder();
        final BufferedReader reader = new BufferedReader(new FileReader(_file));
        String line = reader.readLine();
        while (line != null)  {
            code.append(line).append('\n');
            line = reader.readLine();
        }
        reader.close();

        return code;
    }

    /**
     * Returns the stored file date within Matrix for administration object
     * with given name. For performance reason the method uses
     * &quot;print&quot; commands, because a complete XML parse including a
     * complete export takes longer time.
     *
     * @param _context      context for this request
     * @param _name         name of update object
     * @return modified date of given update object
     * @throws MatrixException
     */
    public Date getMxFileDate(final Context _context,
                              final String _name)
            throws MatrixException
    {
        final String curVersion;
        // check for existing administration type...
        if (this.getInfoAnno().adminType() != AdminTypeDef.Undef)  {
            final String tmp = execMql(_context, new StringBuilder()
                    .append("print ").append(this.getInfoAnno().adminType().getMxName())
                    .append(" \"").append(_name).append("\" ")
                    .append(this.getInfoAnno().adminType().getMxSuffix())
                    .append(" select property[version] dump"));
            curVersion = (tmp.length() >= 14)
                         ? tmp.substring(14)
                         : "";
        // otherwise we have a business object....
        } else  {
            final String[] nameRev = _name.split("________");
            curVersion = execMql(_context, new StringBuilder()
                    .append("print bus \"")
                    .append(this.getInfoAnno().busType().getMxName())
                    .append("\" \"").append(nameRev[0])
                    .append("\" \"").append((nameRev.length > 1) ? nameRev[1] : "")
                    .append("\" select attribute[").append(AdminPropertyDef.VERSION.getAttrName()).append("] dump"));
        }
        return (curVersion.matches("^[0-9]++$"))
               ? new Date(Long.parseLong(curVersion) * 1000)
               : null;
    }

    /**
     * Returns the version string of this administration (business) object. The
     * method is the getter method for {@see #version).
     *
     * @return version string
     * @see #version
     */
    protected String getVersion()
    {
        return this.version;
    }

    /**
     * Sets the new version string for this administration (business) object.
     * It is the setter method for {@see #version}.
     *
     * @param _version  new version to set
     * @see #version
     */
    protected void setVersion(final String _version)
    {
        this.version = _version;
    }
}