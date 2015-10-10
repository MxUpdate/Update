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
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

import matrix.util.MatrixException;

/**
 * Abstract class from which must be derived for exporting and importing all
 * administration (business) objects.
 *
 * @author The MxUpdate Team
 * @param <CLASS>   class derived from this class
 */
public abstract class AbstractObject_mxJPO<CLASS extends AbstractObject_mxJPO<CLASS>>
{
    /** Defines the related type definition enumeration. */
    private final TypeDef_mxJPO typeDef;

    /** MX Name of the administration object. */
    private String mxName;

    /** Description of the C object. */
    private String description = "";

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
     * Returns the {@link #typeDef type definition} instance.
     *
     * @return type definition enumeration
     */
    public final TypeDef_mxJPO getTypeDef()
    {
        return this.typeDef;
    }

    /**
     * Parses the given {@code _code} and updates this data instance.
     *
     * @param _file     file which holds the code to parse
     * @param _code     code to parse
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public abstract void parseUpdate(final File _file,
                                     final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException;

    /**
     * Parses all information for given administration object.
     *
     * @param _paramCache       parameter cache
     * @throws MatrixException  if XML export could not be created or if
     *                          another MX action failed
     * @throws ParseException   if the admin XML export can not be parsed
     */
    public abstract void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, ParseException;

    /**
     * Writes the update file content to the builder.
     *
     * @param _updateBuilder    update builder
     */
    public abstract void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder);

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
     * The method is called from the JPO caller interface.
     *
     * @param _paramCache   parameter cache
     * @param _file         file
     * @param _fileDate     date of file
     * @param _code         code to update
     * @param _create       first created
     * @throws Exception never, only dummy
     */
    abstract public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                                        final String _file,
                                        final String _fileDate,
                                        final String _code,
                                        final boolean _create)
        throws Exception;

    /**
     * Calculates the delta between given {@code _current} admin object
     * definition and this target admin object definition and appends the MQL
     * append commands to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _mql          builder to append the MQL commands
     * @param _current      current admin object definition
     * @throws UpdateException_mxJPO if update is not allowed (e.g. if data can
     *                      be potentially lost)
     */
    protected abstract void calcDelta(final ParameterCache_mxJPO _paramCache,
                                      final MultiLineMqlBuilder _mql,
                                      final CLASS _current)
        throws UpdateException_mxJPO;

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
     * Defines the {@link #mxName} of this data piece.
     *
     * @param _mxName   new MX name of the data piece
     */
    protected void setName(final String _mxName)
    {
        this.mxName = _mxName;
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
}
