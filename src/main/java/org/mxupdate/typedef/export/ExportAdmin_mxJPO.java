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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.FileHandlingUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;

import matrix.util.MatrixException;

/**
 * Exports admin object as single CI file.
 *
 * @author The MxUpdate Team
 */
public class ExportAdmin_mxJPO
    extends AbstractExportObject_mxJPO
{
    @Override
    public void export(final ParameterCache_mxJPO _paramCache,
                       final TypeDef_mxJPO _typeDef,
                       final String _mxName,
                       final File _path)
        throws IOException, MatrixException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        try  {
            final AbstractObject_mxJPO<?> clazz = _typeDef.newTypeInstance(_mxName);

            clazz.parse(_paramCache);

            // append the stored sub path of the ci object from last import
            final File file;
            final String subPath = clazz.getPropValue(_paramCache, PropertyDef_mxJPO.SUBPATH);
            if ((subPath != null) && !subPath.isEmpty())  {
                file = new File(new File(new File(_path, _typeDef.getFilePath()), subPath), FileHandlingUtil_mxJPO.calcCIFileName(_typeDef, _mxName));
            } else  {
                file = new File(new File(_path, _typeDef.getFilePath()), FileHandlingUtil_mxJPO.calcCIFileName(_typeDef, _mxName));
            }

            // create parent directories
            if (!file.getParentFile().exists())  {
                file.getParentFile().mkdirs();
            }

            final Writer out = new FileWriter(file);
            this.write(_paramCache, clazz, _typeDef, out);
            out.flush();
            out.close();
        } catch (final MatrixException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        } catch (final ParseException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
        }
    }
}
