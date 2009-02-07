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

package org.mxupdate.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import matrix.db.Context;
import matrix.util.MatrixException;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.mapping.Mapping_mxJPO.AdminPropertyDef;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.xml.sax.SAXException;

import static org.mxupdate.update.util.StringUtil_mxJPO.match;
import static org.mxupdate.util.MqlUtil_mxJPO.execMql;

/**
 * Abstract class from which must be derived for exporting and importing all
 * administration (business) objects.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public abstract class AbstractObject_mxJPO
        implements Serializable
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -5505850566853070973L;

    /**
     * Stores the version information of this object. If the value is
     * <code>null</code>, the version information is not defined.
     *
     * @see #getVersion()
     * @see #setVersion(String)
     */
    private String version = null;

    /**
     * Defines the related type definition enumeration.
     *
     * @see #getTypeDef()
     * @see #AbstractObject_mxJPO(TypeDef_mxJPO)
     */
    private final TypeDef_mxJPO typeDef;

    /**
     * Name of the matrix administration object.
     *
     * @see #setName(String)
     * @see #getName()
     */
    private String name = null;

    /**
     * Author of the matrix administration object.
     *
     * @see #setAuthor(String)
     * @see #getAuthor()
     */
    private String author;

    /**
     * Application of the matrix administration object.
     *
     * @see #setApplication(String)
     * @see #getApplication()
     */
    private String application;

    /**
     * Description of the matrix administration object.
     *
     * @see #setDescription(String)
     * @see #getDescription()
     */
    private String description = "";

    /**
     * Installation date of the matrix administration object.
     *
     * @see #setInstallationDate(String)
     * @see #getInstallationDate()
     */
    private String installationDate;

    /**
     * Installer of the matrix administration object.
     *
     * @see #setInstaller(String)
     * @see #getInstaller()
     */
    private String installer;

    /**
     * Original name of the matrix administration object.
     *
     * @see #setOriginalName(String)
     * @see #getOriginalName()
     */
    private String originalName;

    /**
     * Initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     */
    protected AbstractObject_mxJPO(final TypeDef_mxJPO _typeDef)
    {
        this.typeDef = _typeDef;
    }

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
     * Returns the type definition instance.
     *
     * @return type definition enumeration
     * @see #typeDef
     */
    public final TypeDef_mxJPO getTypeDef()
    {
        return this.typeDef;
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
     * @param _paramCache   parameter cache
     * @param _path         path to write through (if required also including
     *                      depending file path defined from the information
     *                      annotation)
     * @param _name         name of object to export
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     */
    public void export(final ParameterCache_mxJPO _paramCache,
                       final File _path,
                       final String _name)
            throws MatrixException, SAXException, IOException
    {
        this.parse(_paramCache, _name);
        final File file = new File(_path, this.getFileName());
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        this.write(_paramCache, out);
        out.flush();
        out.close();
    }

    protected abstract void write(final ParameterCache_mxJPO _paramCache,
                                  final Writer _out)
            throws IOException, MatrixException;

    /**
     * Parses all information for given administration object.
     *
     * @param _paramCache   parameter cache
     * @param _name         name of administration object which must be parsed
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     */
    protected abstract void parse(final ParameterCache_mxJPO _paramCache,
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
        final int suffixLength = (suffix != null) ? suffix.length() : 0;
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;

        for (final File file : _files)  {
            final String fileName = file.getName();
            for (final String match : _matches)  {
                if (((prefix == null) || fileName.startsWith(prefix)) && ((suffix == null) || fileName.endsWith(suffix)))  {
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
        final int suffixLength = (suffix != null) ? suffix.length() : 0;
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;
        for (final File file : _files)  {
            final String fileName = file.getName();
            if (((prefix == null) || fileName.startsWith(prefix))
                    && ((prefix == null) || fileName.endsWith(suffix)))  {
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
     * @param _paramCache       parameter cache
     * @param _name             name of the administration (business) object
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update failed
     */
    public abstract void update(final ParameterCache_mxJPO _paramCache,
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
     * Returns the stored value within Matrix for administration object
     * with given property name. For performance reason the method uses
     * &quot;print&quot; commands, because a complete XML parse including a
     * complete export takes longer time.
     *
     * @param _context      context for this request
     * @param _name         name of administration object
     * @param _prop         property for which the value is searched
     * @return value for given property
     * @throws MatrixException
     */
    public String getPropValue(final Context _context,
                               final String _name,
                               final AdminPropertyDef _prop)
            throws MatrixException
    {
        final String curVersion;
        // check for existing administration type...
        if (this.getTypeDef().getMxAdminName() != null)  {
            final String tmp = execMql(_context, new StringBuilder()
                    .append("print ").append(this.getTypeDef().getMxAdminName())
                    .append(" \"").append(_name).append("\" ")
                    .append(this.getTypeDef().getMxAdminSuffix())
                    .append(" select property[").append(_prop.getPropName()).append("] dump"));
            final int length = 7 + _prop.getPropName().length();
            curVersion = (tmp.length() >= length)
                         ? tmp.substring(length)
                         : "";
        // otherwise we have a business object....
        } else  {
            final String[] nameRev = _name.split("________");
            curVersion = execMql(_context, new StringBuilder()
                    .append("print bus \"")
                    .append(this.getTypeDef().getMxBusType())
                    .append("\" \"").append(nameRev[0])
                    .append("\" \"").append((nameRev.length > 1) ? nameRev[1] : "")
                    .append("\" select attribute[").append(_prop.getAttrName()).append("] dump"));
        }

        return curVersion;
    }

    /**
     * Getter method for instance variable {@link #name}.
     *
     * @return value of instance variable {@link #name}.
     */
    protected String getName()
    {
        return this.name;
    }

    /**
     * Setter method for instance variable {@link #name}.
     *
     * @param _name new value for instance variable {@link #name}.
     */
    protected void setName(final String _name)
    {
        this.name = _name;
    }

    /**
     * Getter method for instance variable {@link #author}.
     *
     * @return value of instance variable {@link #author}.
     * @see #author
     */
    protected String getAuthor()
    {
        return this.author;
    }

    /**
     * Setter method for instance variable {@link #author}.
     *
     * @param _author new value for instance variable {@link #author}
     * @see #author
     */
    protected void setAuthor(final String _author)
    {
        this.author = _author;
    }

    /**
     * Getter method for instance variable {@link #application}.
     *
     * @return value of instance variable {@link #application}.
     * @see #application
     */
    protected String getApplication()
    {
        return this.application;
    }

    /**
     * Setter method for instance variable {@link #application}.
     *
     * @param _author new value for instance variable {@link #application}
     * @see #application
     */
    protected void setApplication(final String _application)
    {
        this.application = _application;
    }

    /**
     * Getter method for instance variable {@link #description}.
     *
     * @return value of instance variable {@link #description}.
     */
    protected String getDescription()
    {
        return this.description;
    }

    /**
     * Setter method for instance variable {@link #description}.
     *
     * @param _description new value for instance variable {@link #description}.
     */
    protected void setDescription(final String _description)
    {
        this.description = _description;
    }

    /**
     * Getter method for instance variable {@link #installationDate}.
     *
     * @return value of instance variable {@link #installationDate}.
     * @see #installationDate
     */
    protected String getInstallationDate()
    {
        return this.installationDate;
    }

    /**
     * Setter method for instance variable {@link #installationDate}.
     *
     * @param _author new value for instance variable {@link #installationDate}
     * @see #installationDate
     */
    protected void setInstallationDate(final String _installationDate)
    {
        this.installationDate = _installationDate;
    }

    /**
     * Getter method for instance variable {@link #installer}.
     *
     * @return value of instance variable {@link #installer}.
     * @see #installer
     */
    protected String getInstaller()
    {
        return this.installer;
    }

    /**
     * Setter method for instance variable {@link #installer}.
     *
     * @param _author new value for instance variable {@link #installer}
     * @see #installer
     */
    protected void setInstaller(final String _installer)
    {
        this.installer = _installer;
    }

    /**
     * Getter method for instance variable {@link #originalName}.
     *
     * @return value of instance variable {@link #originalName}.
     */
    protected String getOriginalName()
    {
        return this.originalName;
    }

    /**
     * Setter method for instance variable {@link #originalName}.
     *
     * @param _originalName     new value for instance variable
     *                          {@link #originalName}.
     */
    protected void setOriginalName(final String _originalName)
    {
        this.originalName = _originalName;
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

    /**
     * Returns the file name for this MxUpdate administration object. The file
     * name is a concatenation of the defined file prefix within the
     * information annotation , the name of the Mx object and the file suffix
     * within the information annotation.
     *
     * @return file name of this administration (business) object
     */
    protected String getFileName() {
        final StringBuilder ret = new StringBuilder();
        if (this.getTypeDef().getFilePrefix() != null)  {
            ret.append(this.getTypeDef().getFilePrefix());
        }
        ret.append(this.getName());
        if (this.getTypeDef().getFileSuffix() != null)  {
            ret.append(this.getTypeDef().getFileSuffix());
        }
        return ret.toString();
    }
}