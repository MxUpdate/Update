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

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.util.FileUtils_mxJPO;
import org.mxupdate.util.JPOUtil_mxJPO;

import matrix.util.MatrixException;

/**
 * Exports admin program JPO as single CI file with separate java file.
 *
 * @author The MxUpdate Team
 */
public class ExportAdminProgramJPO_mxJPO
    extends AbstractExportObject_mxJPO
{
    /**
     * First writes the MXU file and then the JPO itself.
     *
     * @param _paramCache       parameter cache
     * @param _path             path to write through (if required also
     *                          including depending file path defined from the
     *                          information annotation)
     * @throws IOException      if JPO or MXU file can not be written
     * @throws MatrixException  if some MQL statement failed
     * @throws ParseException   if the XML export of the object could not
     *                          parsed (for admin objects)
     */
    @Override
    public void export(final ParameterCache_mxJPO _paramCache,
                       final TypeDef_mxJPO _typeDef,
                       final String _mxName,
                       final File _path)
        throws IOException, MatrixException, ParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException
    {
        try  {
            final File path = new File(_path, _typeDef.getFilePath());

            final ProgramCI_mxJPO clazz = (ProgramCI_mxJPO) _typeDef.newTypeInstance(_paramCache, _mxName);

            clazz.parse(_paramCache);

            if (!clazz.hasNoValuesDefined(_paramCache) || (clazz.getCode() == null) || clazz.getCode().isEmpty())  {
                final File file = new File(path, FileUtils_mxJPO.calcCIFileName(_typeDef, _mxName));
                if (!file.getParentFile().exists())  {
                    file.getParentFile().mkdirs();
                }
                final Writer out = new FileWriter(file);
                try  {
                    this.write(_paramCache, clazz, _typeDef, out);
                    out.flush();
                } finally {
                    out.close();
                }
            }

            if ((clazz.getCode() != null) && !clazz.getCode().isEmpty())  {

                // prepare name of JPO to extract
                final int index = clazz.getName().lastIndexOf('.');
                final String fileName = new StringBuilder()
                        .append((index >= 0) ? clazz.getName().substring(index + 1) : clazz.getName())
                        .append(JPOUtil_mxJPO.JPO_NAME_SUFFIX_EXTENDSION)
                        .toString();
                // prepare path
                final StringBuilder pathStr = new StringBuilder().append(path);
                if (index >= 0)  {
                    pathStr.append('/').append(clazz.getName().substring(0, index).replaceAll("\\.", "/"));
                }

                final File file = new File(pathStr.toString(), fileName);
                if (!file.getParentFile().exists())  {
                    file.getParentFile().mkdirs();
                }

                final Writer out = new FileWriter(file);
                final String code = JPOUtil_mxJPO.convertJPOToJavaCode(clazz.isBackslashUpgraded(), clazz.getName(), clazz.getCode());
                try  {
                    out.write(code);
                    out.flush();
                } finally {
                    out.close();
                }
            }
        } catch (final IOException e)  {
            if (_paramCache.getValueBoolean(ValueKeys.ParamContinueOnError))  {
                _paramCache.logError(e.toString());
            } else {
                throw e;
            }
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
