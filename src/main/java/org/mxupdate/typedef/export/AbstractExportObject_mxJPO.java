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

import java.io.IOException;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateBuilder_mxJPO;
import org.mxupdate.util.FileUtils_mxJPO;

/**
 * Abstract definition of the export definition.
 *
 * @author The MxUpdate Team
 */
abstract class AbstractExportObject_mxJPO
    implements IExport_mxJPO
{
    /**
     * Writes the complete update code.
     *
     * @param _paramCache   parameter cache
     * @param _out          writer instance
     * @throws IOException  if the write of the TCL update to the writer
     *                      instance failed
     */
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final AbstractObject_mxJPO<?> _object,
                         final TypeDef_mxJPO _typeDef,
                         final Appendable _out)
        throws IOException
    {
        final UpdateBuilder_mxJPO updateBuilder = new UpdateBuilder_mxJPO(FileUtils_mxJPO.calcCIFileName(_typeDef, _object.getName()), _paramCache);

        updateBuilder.start(_typeDef);

        _object.writeUpdate(updateBuilder);

        updateBuilder.end();

        _out.append(updateBuilder.toString());
    }
}
