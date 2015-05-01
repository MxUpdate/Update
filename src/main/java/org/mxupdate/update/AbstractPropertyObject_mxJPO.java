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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import matrix.util.MatrixException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Abstract definition for objects with properties.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractPropertyObject_mxJPO<CLASS extends AbstractPropertyObject_mxJPO<CLASS>>
    extends AbstractObject_mxJPO
{
    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractPropertyObject_mxJPO(final TypeDef_mxJPO _typeDef,
                                           final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses the given {@code _code} and updates this data instance.
     *
     * @param _code     code to parse
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public abstract void parseUpdate(final String _code)
        throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException;

    /**
     * Writes the complete update code.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException  if the write of the TCL update to the writer
     *                      instance failed
     * @throws MatrixException if an execution of a MQL command failed
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
        throws IOException
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(this.getFileName(), _paramCache);

        updateBuilder.start(this.getTypeDef());

        this.writeUpdate(updateBuilder);

        updateBuilder.end();

        _out.append(updateBuilder.toString());
    }

    /**
     * Writes the update file content to the builder.
     *
     * @param _updateBuilder    update builder
     */
    protected abstract void writeUpdate(final UpdateBuilder_mxJPO _updateBuilder);

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
}
