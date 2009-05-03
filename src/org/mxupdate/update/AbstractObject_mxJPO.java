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
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.MqlUtil_mxJPO;
import org.xml.sax.SAXException;

/**
 * Abstract class from which must be derived for exporting and importing all
 * administration (business) objects.
 *
 * @author The MxUpdate Team
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
     * @see #AbstractObject_mxJPO(TypeDef_mxJPO, String)
     */
    private final TypeDef_mxJPO typeDef;

    /**
     * MX Name of the administration object.
     *
     * @see #getName()
     */
    private final String mxName;

    /**
     * Author of the MX administration object.
     *
     * @see #setAuthor(String)
     * @see #getAuthor()
     */
    private String author;

    /**
     * Application of the MX administration object.
     *
     * @see #setApplication(String)
     * @see #getApplication()
     */
    private String application;

    /**
     * Description of the MX administration object.
     *
     * @see #setDescription(String)
     * @see #getDescription()
     */
    private String description = "";

    /**
     * Installation date of the MX administration object.
     *
     * @see #setInstallationDate(String)
     * @see #getInstallationDate()
     */
    private String installationDate;

    /**
     * Installer of the MX administration object.
     *
     * @see #setInstaller(String)
     * @see #getInstaller()
     */
    private String installer;

    /**
     * Original name of the MX administration object.
     *
     * @see #setOriginalName(String)
     * @see #getOriginalName()
     */
    private String originalName;

    /**
     * All current defined symbolic names for MX administration objects (not
     * for business administration objects!) are stored.
     */
    private final Set<String> symbolicNames = new TreeSet<String>();

    /**
     * Initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractObject_mxJPO(final TypeDef_mxJPO _typeDef,
                                   final String _mxName)
    {
        this.typeDef = _typeDef;
        this.mxName = _mxName;
    }

    /**
     * Returns the path where the file is located of this matrix object. The
     * method used the information annotation.
     *
     * @return sub path
     * @see #getTypeDef()
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
     * Export given administration (business) object with given name into given
     * path. The name of the file where is written through is evaluated within
     * this export method.
     *
     * @param _paramCache   parameter cache
     * @param _path         path to write through (if required also including
     *                      depending file path defined from the information
     *                      annotation)
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     */
    public void export(final ParameterCache_mxJPO _paramCache,
                       final File _path)
            throws MatrixException, SAXException, IOException
    {
        this.parse(_paramCache);
        final File file = new File(_path, StringUtil_mxJPO.convertToFileName(this.getFileName()));
        if (!file.getParentFile().exists())  {
            file.getParentFile().mkdirs();
        }
        final Writer out = new FileWriter(file);
        this.write(_paramCache, out);
        out.flush();
        out.close();
    }

    /**
     *
     * @param _paramCache
     * @param _out
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     * @see #parse(ParameterCache_mxJPO)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    public void export(final ParameterCache_mxJPO _paramCache,
                       final Appendable _out)
            throws MatrixException, SAXException, IOException
    {
        this.parse(_paramCache);
        this.write(_paramCache, _out);
    }
    /**
     *
     * @param _paramCache       parameter cache
     * @param _out              appendable instance to write the TCL update
     *                          code
     * @throws IOException      if write of the TCL update failed
     * @throws MatrixException  if MQL commands failed
     */
    protected abstract void write(final ParameterCache_mxJPO _paramCache,
                                  final Appendable _out)
            throws IOException, MatrixException;

    /**
     * Parses all information for given administration object.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException
     * @throws SAXException
     * @throws IOException
     */
    protected abstract void parse(final ParameterCache_mxJPO _paramCache)
            throws MatrixException, SAXException, IOException;

    /**
     * Returns a list of names exists within MX.
     *
     * @param _paramCache   parameter cache
     * @return set of names of this administration type
     * @throws MatrixException if the search within MX failed
     */
    public abstract Set<String> getMxNames(final ParameterCache_mxJPO _paramCache)
            throws MatrixException;

    /**
     * Checks if given MX name without prefix and suffix matches given match
     * string.
     *
     * @param _paramCache   parameter cache
     * @param _mxName       name of the administration object to check
     * @param _match        string which must be matched
     * @return <i>true</i> if the given MX name matches; otherwise <i>false</i>
     */
    public boolean matchMxName(final ParameterCache_mxJPO _paramCache,
                               final String _mxName,
                               final String _match)
    {
        return StringUtil_mxJPO.match(_mxName, _match);
    }

    /**
     * Extracts the MX name from given file name if the file prefix and suffix
     * matches. If the file prefix and suffix not matches a <code>null</code>
     * is returned.
     *
     * @param _paramCache   parameter cache
     * @param _file         file for which the MX name is searched
     * @return MX name or <code>null</code> if the file is not an update file
     *         for current type definition
     */
    public String extractMxName(final ParameterCache_mxJPO _paramCache,
                                final File _file)
    {
        final String suffix = this.getTypeDef().getFileSuffix();
        final int suffixLength = (suffix != null) ? suffix.length() : 0;
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;

        final String fileName = _file.getName();
        final String mxName;
        if (((prefix == null) || fileName.startsWith(prefix)) && ((suffix == null) || fileName.endsWith(suffix)))  {
            mxName = StringUtil_mxJPO.convertFromFileName(fileName.substring(0, fileName.length() - suffixLength)
                                                 .substring(prefixLength));
        } else  {
            mxName = null;
        }
        return mxName;
    }


    /**
     * Deletes administration object with given name.
     *
     * @param _paramCache       parameter cache
     * @throws Exception if delete failed
     */
    public abstract void delete(final ParameterCache_mxJPO _paramCache)
            throws Exception;

    /**
     * Creates a new administration object with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create failed
     */
    public abstract void create(final ParameterCache_mxJPO _paramCache)
            throws Exception;

    /**
     * Updated this administration (business) object.
     *
     * @param _paramCache       parameter cache
     * @param _file             reference to the file to update
     * @param _newVersion       new version which must be set within the update
     *                          (or <code>null</code> if the version must not
     *                          be set).
     * @throws Exception if update failed
     */
    public abstract void update(final ParameterCache_mxJPO _paramCache,
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
     * @param _paramCache   parameter cache
     * @param _prop         property for which the value is searched
     * @return value for given property
     * @throws MatrixException if the property value could not be extracted
     */
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
            throws MatrixException
    {
        final String curVersion;
        // check for existing administration type...
        if (this.getTypeDef().getMxAdminName() != null)  {
            final String tmp = MqlUtil_mxJPO.execMql(_paramCache.getContext(), new StringBuilder()
                    .append("print ").append(this.getTypeDef().getMxAdminName())
                    .append(" \"").append(this.getName()).append("\" ")
                    .append(this.getTypeDef().getMxAdminSuffix())
                    .append(" select property[").append(_prop.getPropName(_paramCache)).append("] dump"));
            final int length = 7 + _prop.getPropName(_paramCache).length();
            curVersion = (tmp.length() >= length)
                         ? tmp.substring(length)
                         : "";
        // otherwise we have a business object....
        } else  {
            final String[] nameRev = this.getName().split("________");
            curVersion = MqlUtil_mxJPO.execMql(_paramCache.getContext(), new StringBuilder()
                    .append("print bus \"")
                    .append(this.getTypeDef().getMxBusType())
                    .append("\" \"").append(nameRev[0])
                    .append("\" \"").append((nameRev.length > 1) ? nameRev[1] : "")
                    .append("\" select attribute[").append(_prop.getAttrName(_paramCache)).append("] dump"));
        }

        return curVersion;
    }

    /**
     * Getter method for instance variable {@link #mxName}.
     *
     * @return value of instance variable {@link #mxName}.
     * @see #mxName
     */
    public String getName()
    {
        return this.mxName;
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
     * @param _application      new value for instance variable
     *                          {@link #application}
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
     * @param _installationDate     new value for instance variable
     *                              {@link #installationDate}
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
     * @param _installer new value for instance variable {@link #installer}
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
     * method is the getter method for {@link #version}.
     *
     * @return version string
     * @see #version
     */
    protected String getVersion()
    {
        return this.version;
    }

    /**
     * Returns the set of all defined symbolic names of this administration
     * (not business!) object. The method is the getter method for
     * {@link #symbolicNames}.
     *
     * @return all defined symbolic names
     * @see #symbolicNames
     */
    protected Set<String> getSymblicNames()
    {
        return this.symbolicNames;
    }

    /**
     * Sets the new version string for this administration (business) object.
     * It is the setter method for {@link #version}.
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