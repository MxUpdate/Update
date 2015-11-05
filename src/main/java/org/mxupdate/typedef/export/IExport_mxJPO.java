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

package org.mxupdate.typedef.export;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.ParameterCache_mxJPO;

import matrix.util.MatrixException;

/**
 *
 *
 * @author The MxUpdate Team
 */
public interface IExport_mxJPO
{
    /**
     * Export given administration (business) object with given name into given
     * path. The name of the file where is written through is evaluated within
     * this export method.
     *
     * @param _paramCache       parameter cache
     * @param _typeDef          type definition
     * @param _path             path to write through (if required also
     *                          including depending file path defined from the
     *                          information annotation)
     * @throws MatrixException  if some MQL statement failed
     * @throws ParseException   if the XML export of the object could not
     *                          parsed (for admin objects)
     * @throws IOException      if the TCL update code could not be written
     * @throws InvocationTargetException    initialize of type failed
     * @throws IllegalAccessException       initialize of type failed
     * @throws InstantiationException       initialize of type failed
     * @throws NoSuchMethodException        initialize of type failed
     * @throws ClassNotFoundException       initialize of type failed
     */
    void export(final ParameterCache_mxJPO _paramCache,
                final TypeDef_mxJPO _typeDef,
                final String _mxName,
                final File _path)
        throws IOException, MatrixException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException;
}
