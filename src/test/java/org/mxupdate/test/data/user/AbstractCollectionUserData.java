/*
 * Copyright 2008-2010 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.test.data.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.testng.Assert;

/**
 * The class is used to define all collection user objects used to create /
 * update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <DATA> class derived from abstract collection user
 */
public class AbstractCollectionUserData<DATA extends AbstractCollectionUserData<?>>
    extends AbstractUserData<DATA>
{
    /**
     * Parent collection users to which this collection user is assigned.
     *
     * @see #assignParents(AbstractCollectionUserData)
     * @see #checkExport(ExportParser)
     */
    private final Set<DATA> parents = new HashSet<DATA>();

    /**
     * Within export the description must be defined.
     */
    private static final Map<String,String> REQUIRED_EXPORT_VALUES = new HashMap<String,String>();
    static  {
        AbstractCollectionUserData.REQUIRED_EXPORT_VALUES.put("description", "");
    }

    /**
     * Constructor to initialize this collection user.
     *
     * @param _test         related test implementation (where this collection
     *                      user is defined)
     * @param _ci           related configuration type
     * @param _name         name of the collection user
     */
    public AbstractCollectionUserData(final AbstractTest _test,
                                      final AbstractTest.CI _ci,
                                      final String _name)
    {
        super(_test, _ci, _name, AbstractCollectionUserData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Assigns <code>_parent</code> to the list of
     * {@link #parents parent collection users}.
     *
     * @param _parent   parent collection user to assign
     * @return this collection user data instance
     * @see #parents
     */
    @SuppressWarnings("unchecked")
    public DATA assignParents(final DATA _parent)
    {
        this.parents.add(_parent);
        return (DATA) this;
    }

    /**
     * Returns all {@link #parents}.
     *
     * @return assigned parents of this user
     * @see #parents
     */
    public Set<DATA> getParents()
    {
        return this.parents;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this collection user. This includes:
     * <ul>
     * <li>{@link #parents parent collection users}</li>
     * </ul>
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder().append(super.ciFile());

        // define parent collection users
        for (final DATA user : this.parents)  {
            cmd.append("mql escape mod ")
               .append(user.getCI().getMxType()).append(" \"").append(AbstractTest.convertTcl(user.getName()))
               .append("\" child \"${NAME}\";\n");;
        }

        return cmd.toString();
    }

    /**
     * Creates this collection user.
     *
     * @return this collection user data instance
     * @throws MatrixException if create failed
     */
    @SuppressWarnings("unchecked")
    @Override()
    public DATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            super.create();

            // assign parent objects
            final StringBuilder cmd = new StringBuilder();
            for (final DATA user : this.parents)  {
                user.create();
                cmd.append("escape mod ")
                   .append(user.getCI().getMxType()).append(" \"").append(AbstractTest.convertMql(user.getName()))
                   .append("\" child \"").append(AbstractTest.convertMql(this.getName())).append("\";\n");;
            }
            this.getTest().mql(cmd);
        }
        return (DATA) this;
    }

    /**
     * {@inheritDoc}
     * Created depending {@link #parents}.
     *
     * @see #parents
     */
    @Override()
    @SuppressWarnings("unchecked")
    public DATA createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create all parent groups
        for (final DATA parent : this.parents)  {
            parent.create();
        }

        return (DATA) this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * The {@link #parents collection users} are checked.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check parent collection users
        final Set<String> pars = new HashSet<String>(_exportParser.getLines("/mql/@value"));
        pars.remove("escape mod " + this.getCI().getMxType() + " \"${NAME}\"");
        for (final DATA user : this.parents)  {
            pars.remove("escape mod " + user.getCI().getMxType() + " \""
                    + AbstractTest.convertTcl(user.getName()) + "\" child \"${NAME}\"");
        }
        for (final String par : new HashSet<String>(pars))  {
            if (par.startsWith("escape add"))  {
                pars.remove(par);
            }
        }
        Assert.assertTrue(pars.isEmpty(),
                          "check that all parent " + this.getCI().getMxType() + "s are correct defined " + pars);
    }
}
