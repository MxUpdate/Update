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

package org.mxupdate.update;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import matrix.util.MatrixException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.xml.sax.SAXException;

/**
 * Abstract class from which must be derived for exporting and importing all
 * administration (business) objects.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractObject_mxJPO
{
    /** Defines the related type definition enumeration. */
    private final TypeDef_mxJPO typeDef;

    /** MX Name of the administration object. */
    private final String mxName;

    /** Description of the C object. */
    private String description = "";

    ////////////////////////////////////////////////////////////////////////////
    // global methods start

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
     * @param _matches      list of string which must be matched (if
     *                      {@code null}, name matched!)
     * @return <i>true</i> if the given MX name matches; otherwise <i>false</i>
     */
    public boolean matchMxName(final ParameterCache_mxJPO _paramCache,
                               final String _mxName,
                               final Collection<String> _matches)
    {
        return StringUtil_mxJPO.match(_mxName, _matches);
    }

    /**
     * Extract for given {@code _files} all mx names for this type.
     *
     * @param _paramCache   parameter cache
     * @param _files        files
     * @param _matches      matches (if {@code null}, all is valid}
     * @return map of MX names and depending file for this type definition
     * @throws UpdateException_mxJPO if evaluate failed
     */
    public SortedMap<String,File> evalMatching(final ParameterCache_mxJPO _paramCache,
                                               final Collection<File> _files,
                                               final Collection<String> _matches)
        throws UpdateException_mxJPO
    {
        final SortedMap<String,File> ret = new TreeMap<String,File>();

        for (final File file : _files)  {
            final String mxName = this.evalMxName(_paramCache, file);
            if ((mxName != null)  && this.matchMxName(_paramCache, mxName, _matches))  {
                ret.put(mxName, file);
            }
        }

        return ret;
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
     * @throws UpdateException_mxJPO if the configuration item name could not
     *                               be extracted from the file name
     */
    public String evalMxName(final ParameterCache_mxJPO _paramCache,
                             final File _file)
        throws UpdateException_mxJPO
    {
        final String suffix = this.getTypeDef().getFileSuffix();
        final int suffixLength = (suffix != null) ? suffix.length() : 0;
        final String prefix = this.getTypeDef().getFilePrefix();
        final int prefixLength = (prefix != null) ? prefix.length() : 0;

        final String fileName = _file.getName();
        final String mxName;
        if (((prefix == null) || fileName.startsWith(prefix)) && ((suffix == null) || fileName.endsWith(suffix)))  {
            mxName = StringUtil_mxJPO.convertFromFileName(fileName.substring(0, fileName.length() - suffixLength).substring(prefixLength));
        } else  {
            mxName = null;
        }
        return mxName;
    }

    // global methods end
    ////////////////////////////////////////////////////////////////////////////

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
     * Returns the {@link #typeDef type definition} instance.
     *
     * @return type definition enumeration
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
     * @param _paramCache       parameter cache
     * @param _path             path to write through (if required also
     *                          including depending file path defined from the
     *                          information annotation)
     * @throws MatrixException  if some MQL statement failed
     * @throws SAXException     if the XML export of the object could not
     *                          parsed (for admin objects)
     * @throws IOException      if the TCL update code could not be written
     */
    public void export(final ParameterCache_mxJPO _paramCache,
                       final File _path)
        throws MatrixException, SAXException, IOException
    {
        try  {
            this.parse(_paramCache);

            // append the stored sub path of the ci object from last import
            final File file;
            final String subPath = this.getPropValue(_paramCache, PropertyDef_mxJPO.SUBPATH);
            if ((subPath != null) && !subPath.isEmpty())  {
                file = new File(new File(_path, subPath), this.getFileName());
            } else  {
                file = new File(_path, this.getFileName());
            }

            // create parent directories
            if (!file.getParentFile().exists())  {
                file.getParentFile().mkdirs();
            }

            final Writer out = new FileWriter(file);
            this.write(_paramCache, out);
            out.flush();
            out.close();
        } catch (final MatrixException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        } catch (final SAXException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        } catch (final IOException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        }
    }

    /**
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance where the TCL update code is
     *                      written
     * @throws MatrixException  if some MQL statement failed
     * @throws SAXException     if the XML export of the object could not
     *                          parsed (for admin objects)
     * @throws IOException      if the TCL update code could not be written
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
     * @throws MatrixException  if XML export could not be created or if
     *                          another MX action failed
     * @throws SAXException     if the XML document could not be parsed
     * @throws IOException      if the XML document could not be opened (should
     *                          never happen)
     */
    protected abstract void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, SAXException, IOException;

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
     * @param _paramCache   parameter cache
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _file         reference to the file to update
     * @throws Exception if update failed
     */
    public abstract void update(final ParameterCache_mxJPO _paramCache,
                                final boolean _create,
                                final File _file)
        throws Exception;

    /**
     * Compiles this administration object. Because typically ad administration
     * object must not be compile, nothing is done here.
     *
     * @param _paramCache       parameter cache
     * @return <i>true</i> if administration object is compiled; otherwise
     *         <i>false</i> (and here used always)
     * @throws Exception if the compile failed
     */
    public boolean compile(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        return false;
    }

    /**
     * <p>Returns the stored value within Matrix for administration object
     * with given property name. For performance reason the method should use
     * &quot;print&quot; commands, because a complete XML parse including a
     * complete export takes longer time.</p>
     *
     * @param _paramCache   parameter cache
     * @param _prop         property for which the value is searched
     * @return value for given property
     * @throws MatrixException if the property value could not be extracted
     */
    public abstract String getPropValue(final ParameterCache_mxJPO _paramCache,
                                        final PropertyDef_mxJPO _prop)
        throws MatrixException;

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
     * Returns the file name for this MxUpdate administration object. The file
     * name is a concatenation of the defined file prefix within the
     * information annotation , the name of the MX object and the file suffix
     * within the information annotation. All special characters are converted
     * automatically from {@link StringUtil_mxJPO#convertToFileName(String)}.
     *
     * @return file name of this administration (business) object
     * @see #export(ParameterCache_mxJPO, File)
     */
    public String getFileName()
    {
        final StringBuilder ret = new StringBuilder();
        if (this.getTypeDef().getFilePrefix() != null)  {
            ret.append(this.getTypeDef().getFilePrefix());
        }
        ret.append(this.getName());
        if (this.getTypeDef().getFileSuffix() != null)  {
            ret.append(this.getTypeDef().getFileSuffix());
        }
        return StringUtil_mxJPO.convertToFileName(ret.toString());
    }
}
