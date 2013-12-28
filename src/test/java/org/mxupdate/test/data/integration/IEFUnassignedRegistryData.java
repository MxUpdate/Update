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

package org.mxupdate.test.data.integration;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.data.AbstractBusData;
import org.mxupdate.test.data.datamodel.TypeData;

/**
 * Used to define an integration unassigned registry, create them and test the
 * result.
 *
 * @author The MxUpdate Team
 */
public class IEFUnassignedRegistryData
    extends AbstractBusData<IEFUnassignedRegistryData>
{
    /**
     * Initialize this IEF unassigned registry object with given
     * <code>_name</code>.
     *
     * @param _test     related test implementation (where this IEF unassigned
     *                  registry object is defined)
     * @param _type     derived type from
     *                  <code>IEF-UnassignedIntegRegistry</code>;
     *                  <code>null</code> if the type is directly used
     * @param _name     name of the IEF unassigned registry object
     * @param _revision revision of the IEF unassigned registry object
     */
    public IEFUnassignedRegistryData(final AbstractTest _test,
                                    final TypeData _type,
                                    final String _name,
                                    final String _revision)
    {
        super(_test, AbstractTest.CI.IEF_UNASSIGNED_REGISTRY, _type, _name, _revision);
    }
}
