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

import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;
import org.mxupdate.util.JPOUtil_mxJPO;

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
     * Compile current JPO.
     *
     * @param _paramCache   parameter cache
     * @return always <i>true</i>
     * @throws Exception if the compile of the JPO failed
     */
    @Override
    public boolean compile(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        MqlBuilder_mxJPO.mql().cmd("escape compile prog ").arg(this.getName()).exec(_paramCache);
        return true;
    }

    /**
     * Reads the Java source code and converts them to JPO source code.
     *
     * @param _file     file to read
     * @return JPO source code of the given file
     */
    @Override
    protected String readCode(final File _file)
        throws UpdateException_mxJPO
    {
        return JPOUtil_mxJPO.convertJavaToJPOCode(this.getName(), super.readCode(_file));
    }
}
