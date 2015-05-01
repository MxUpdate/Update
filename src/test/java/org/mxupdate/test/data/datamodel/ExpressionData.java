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

package org.mxupdate.test.data.datamodel;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractAdminData;

/**
 * Used to define an expression, create them and test the result.
 *
 * @author The MxUpdate Team
 */
public class ExpressionData
    extends AbstractAdminData<ExpressionData>
{
    /**
     * Initialize this expression data with given <code>_name</code>.
     *
     * @param _test     related test implementation (where this expression is
     *                  defined)
     * @param _name     name of the expression
     */
    public ExpressionData(final AbstractTest _test,
                          final String _name)
    {
        super(_test, AbstractTest.CI.DM_EXPRESSION, _name);
    }
}
