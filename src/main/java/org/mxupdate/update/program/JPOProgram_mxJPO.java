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

package org.mxupdate.update.program;

import java.io.File;
import java.util.Map;

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;

/**
 * The class is used to export, create, delete and update JPOs within MX.
 *
 * @author The MxUpdate Team
 */
public class JPOProgram_mxJPO
    extends AbstractProgram_mxJPO<JPOProgram_mxJPO>
{
    /** String with name suffix (used also from the extract routine from Matrix). */
    public static final String NAME_SUFFIX_EXTENDSION = JPOProgram_mxJPO.NAME_SUFFIX + ".java";

    public static final int NAME_SUFFIX_EXTENDSION_LENGTH = JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION.length();

    /** String with name suffix (used also from the extract routine from Matrix). */
    public static final String NAME_SUFFIX = "_" + "mxJPO";

    /**
     * Constructor used to initialize the type definition enumeration and the
     * name.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the JPO object
     */
    public JPOProgram_mxJPO(final TypeDef_mxJPO _typeDef,
                            final String _mxName)
    {
        super(Kind.JAVA, _typeDef, _mxName);
    }

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
                       final boolean _create,
                       final File _file)
        throws Exception
    {
        if (_file.getName().endsWith(JPOProgram_mxJPO.NAME_SUFFIX_EXTENDSION))  {
            MqlBuilder_mxJPO.mql().cmd("escape insert program ").arg(_file.toString()).exec(_paramCache);
        } else  {
            super.update(_paramCache, _create, _file);
        }
    }

    /**
     * Compile current JPO.
     *
     * @param _paramCache   parameter cache
     * @return always <i>true</i>
     * @throws Exception if the compile of the JPO failed
     */
    @Override()
    public boolean compile(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        MqlBuilder_mxJPO.mql().cmd("escape compile prog ").arg(this.getName()).exec(_paramCache);
        return true;
    }
}
