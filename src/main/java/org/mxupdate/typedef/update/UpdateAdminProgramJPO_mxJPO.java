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

package org.mxupdate.typedef.update;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.program.ProgramCI_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.util.FileUtil_mxJPO;
import org.mxupdate.util.JPOUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MultiLineMqlBuilder;

/**
 * Updates given admin program JPO CI.
 *
 * @author The MxUpdate Team
 */
public class UpdateAdminProgramJPO_mxJPO
    extends UpdateObject_mxJPO
{
    /**
     * Updates this administration (business) object if the stored information
     * about the version is not the same as the file date. If an update is
     * required, the file is read and the object is updated with
     * {@link #update(ParameterCache_mxJPO, CharSequence, CharSequence, CharSequence, Map, File)}.
     *
     * @param _paramCache   parameter cache
     * @param _create       <i>true</i> if the CI object is new created (and
     *                      first update is done)
     * @param _file         file with TCL update code
     * @throws Exception if the update from the derived class failed
     */
    @Override()
    public void update(final ParameterCache_mxJPO _paramCache,
                       final TypeDef_mxJPO _typeDef,
                       final boolean _create,
                       final String _name,
                       final File _file)
        throws Exception
    {
        if (_file.getName().endsWith(ProgramCI_mxJPO.JPO_NAME_SUFFIX_EXTENDSION))  {
            final String propName = PropertyDef_mxJPO.FILEDATE.getPropName(_paramCache);

            final MultiLineMqlBuilder mql = MqlBuilderUtil_mxJPO.multiLine(_file, "");

            // check property already exists => remove property
            final String propExists = MqlBuilderUtil_mxJPO.mql().cmd("escape print program ").arg(_name).cmd(" select ").arg("property[" + propName + "]").exec(_paramCache);
            if (!propExists.isEmpty())  {
                mql.newLine().cmd("escape mod program ").arg(_name).cmd(" remove property ").arg(propName);
            }
            // define file date
            mql.newLine().cmd("escape mod program ").arg(_name).cmd(" add property ")
                    .arg(propName).cmd(" value ").arg(StringUtil_mxJPO.formatFileDate(_paramCache, new Date(_file.lastModified())));

            // insert program
            mql.newLine().cmd("escape mod program ").arg(_name).cmd(" code ").arg(JPOUtil_mxJPO.convertJavaToJPOCode(_name, FileUtil_mxJPO.readFileToString(_file).toString()));

            // and execute all
            mql.exec(_paramCache);
        } else  {
            super.update(_paramCache, _typeDef, _create, _name, _file);
        }
    }
}
