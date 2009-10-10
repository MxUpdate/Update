/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.test.data.user.workspace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.Assert;

/**
 * The class is used to define all common things for workspace objects related
 * to users used to create / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <DATA> workspace object class
 * @param <USER> for which user class the workspace object class is defined
 */
abstract class AbstractWorkspaceObjectData<DATA extends AbstractWorkspaceObjectData<?,USER>, USER extends AbstractUserData<?>>
    extends AbstractData<DATA>
{
    /**
     * Related user to which is workspace object belongs through.
     *
     * @see #AbstractWorkspaceObjectData(AbstractTest, AbstractUserData, String)
     */
    private final USER user;

    /**
     * Defines for which user the workspace object is visible.
     */
    private final Set<String> visible = new TreeSet<String>();

    /**
     * MX administration type.
     */
    private final String mxAdminType;

    /**
     * Default constructor.
     *
     * @param _test                 related test case
     * @param _mxAdminType          MX administration type of the workspace
     *                              object
     * @param _user                 user for which this workspace object is
     *                              defined
     * @param _name                 name of the workspace object
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     */
    AbstractWorkspaceObjectData(final AbstractTest _test,
                                final String _mxAdminType,
                                final USER _user,
                                final String _name,
                                final Set<String> _requiredExportValues)
    {
        super(_test, null, _name, null, null, _requiredExportValues);
        this.mxAdminType = _mxAdminType;
        this.user = _user;
    }


    /**
     * Returns the user for which this workspace object is define.
     *
     * @return related user of this workspace object
     */
    public USER getUser()
    {
        return this.user;
    }

    /**
     * Returns the {@link #mxAdminType MX administration type} of this
     * workspace object.
     *
     * @return MX administration type
     */
    public String getMxAdminType()
    {
        return this.mxAdminType;
    }

    /**
     * Defines for which users this workspace object is visible.
     *
     * @param _users    users for which this workspace object is visible
     * @return this workspace object instance
     * @see #visible
     */
    @SuppressWarnings("unchecked")
    public DATA setVisible(final String... _users)
    {
        this.visible.addAll(Arrays.asList(_users));
        return (DATA) this;
    }

    /**
     * Creates this workspace object.
     *
     * @return this workspace object instance
     * @throws MatrixException if create failed
     */
    @SuppressWarnings("unchecked")
    @Override()
    public DATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            final StringBuilder cmd = new StringBuilder()
                    .append("escape add ").append(this.mxAdminType).append(" \"")
                    .append(AbstractTest.convertMql(this.getName()))
                    .append("\" user \"")
                    .append(AbstractTest.convertMql(this.user.getName()))
                    .append('\"');

            // append values
            this.append4Create(cmd);

            this.getTest().mql(cmd);

            this.setCreated(true);
        }
        return (DATA) this;
    }

    /**
     * Appends the MQL commands to define the hidden flag and the
     * {@link #visible users}Êwithin a create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #values
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // hidden flag
        if (this.isHidden())  {
            _cmd.append(" hidden");
        } else  {
            _cmd.append(" !hidden");
        }

        // visible users
        if (!this.visible.isEmpty())  {
            _cmd.append(" visible ");
            boolean first = true;
            for (final String user : this.visible)  {
                if (first)  {
                    first = false;
                } else  {
                    _cmd.append(',');
                }
                _cmd.append('\"').append(AbstractTest.convertMql(user)).append('\"');
            }
        }

        // properties
        for (final PropertyDef property : this.getProperties())  {
            _cmd.append(" property \"").append(AbstractTest.convertMql(property.getName())).append("\"");
            if (property.getTo() != null)  {
                property.getTo().create();
                _cmd.append(" to ").append(property.getTo().getCI().getMxType()).append(" \"")
                    .append(AbstractTest.convertMql(property.getTo().getName())).append("\"");
            }
            if (property.getValue() != null)  {
                _cmd.append(" value \"").append(AbstractTest.convertMql(property.getValue())).append("\"");
            }
        }
    }

    /**
     * Returns the part of the CI file to create this workspace object of an
     * user.
     *
     * @return part of the CI file to create this workspace object of an user
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape add ").append(this.mxAdminType).append(" \"")
                .append(AbstractTest.convertTcl(this.getName()))
                .append("\" \\\n    user \"${NAME}\"");

        // hidden flag
        if (this.isHidden())  {
            cmd.append(" \\\n    hidden");
        } else  {
            cmd.append(" \\\n    !hidden");
        }

        // visible users
        if (!this.visible.isEmpty())  {
            for (final String user : this.visible)  {
                cmd.append(" \\\n    visible \"")
                   .append(AbstractTest.convertTcl(user))
                   .append('\"');
            }
        }

        // properties
        for (final PropertyDef property : this.getProperties())  {
            cmd.append(" property \"").append(AbstractTest.convertTcl(property.getName())).append("\"");
            if (property.getTo() != null)  {
                cmd.append(" to ").append(property.getTo().getCI().getMxType()).append(" \"")
                    .append(AbstractTest.convertTcl(property.getTo().getName())).append("\"");
            }
            if (property.getValue() != null)  {
                cmd.append(" value \"").append(AbstractTest.convertTcl(property.getValue())).append("\"");
            }
        }

        this.append4CIFileValues(cmd);

        return cmd.toString();
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * The hidden flag and the {@link #visible visible users} are checked.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);

        // check hidden flag
        final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
        if (this.isHidden())  {
            Assert.assertTrue(main.contains("hidden") || main.contains("hidden \\"),
                              "check that " + this.mxAdminType + " '" + this.getName() + "' is hidden");
        } else  {
            Assert.assertTrue(main.contains("!hidden") || main.contains("!hidden \\"),
                              "check that " + this.mxAdminType + " '" + this.getName() + "' is not hidden");
        }

        // check visible users
        final List<String> foundVisible = _exportParser.getLines("/mql/visible/@value");
        for (final String user : foundVisible)  {
            final String tmpUser = user.replaceAll("^\"", "").replaceAll("\"$", "");
            Assert.assertTrue(this.visible.contains(tmpUser),
                              "check that '" + tmpUser + "' was defined visible");
        }
        Assert.assertEquals(foundVisible.size(),
                            this.visible.size(),
                            "check that all visible users are defined");

        // check properties
        final Set<String> foundProps = new HashSet<String>(_exportParser.getLines("/mql/property/@value"));
        for (final PropertyDef property : this.getProperties())  {
            final StringBuilder key = new StringBuilder()
                    .append("\"").append(AbstractTest.convertTcl(property.getName())).append("\"");
            if (property.getTo() != null)  {
                key.append(" to ").append(property.getTo().getCI().getMxType()).append(" \"")
                    .append(AbstractTest.convertTcl(property.getTo().getName())).append("\"");
            }
            if (property.getValue() != null)  {
                key.append(" value \"").append(AbstractTest.convertTcl(property.getValue())).append("\"");
            }
            foundProps.remove(key.toString());
        }
        Assert.assertTrue(foundProps.isEmpty(), "check that all properties are defined");
    }
}
