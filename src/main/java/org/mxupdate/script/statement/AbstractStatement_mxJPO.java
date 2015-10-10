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

package org.mxupdate.script.statement;

import org.mxupdate.script.ScriptContext_mxJPO;

/**
 * One statement of the MxUpdate CI files.
 *
 * @author The MxUpdate Team
 */
public abstract class AbstractStatement_mxJPO
{
    /**
     * Executes this statement.
     *
     * @param _context      script context
     * @throws Exception in any case of an error
     */
    public abstract void execute(final ScriptContext_mxJPO _context)
        throws Exception;
}
