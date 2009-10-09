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
import org.testng.Assert;

/**
 * The class is used to define all cue objects related to used used to create
 * / update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <T> class of the related user for which this cue is defined
 */
public class CueData<T extends AbstractUserData<?>>
    extends AbstractData<CueData<?>>
{
    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
        CueData.REQUIRED_EXPORT_VALUES.add("user");
        CueData.REQUIRED_EXPORT_VALUES.add("appliesto");
        CueData.REQUIRED_EXPORT_VALUES.add("type");
        CueData.REQUIRED_EXPORT_VALUES.add("name");
        CueData.REQUIRED_EXPORT_VALUES.add("revision");
        CueData.REQUIRED_EXPORT_VALUES.add("vault");
        CueData.REQUIRED_EXPORT_VALUES.add("owner");
    }

    /**
     * Related user to which is cue belongs through.
     *
     * @see #CueData(AbstractTest, AbstractUserData, String)
     */
    private final T user;

    /**
     * Is the cue active?
     *
     * @see #setActive(boolean)
     */
    private boolean active = true;

    /**
     * Defines for which user the cue is visible.
     */
    private final Set<String> visible = new TreeSet<String>();

    /**
     * Default constructor.
     *
     * @param _test     related test case
     * @param _user     user for which this cue is defined
     * @param _name     name of the cue
     */
    public CueData(final AbstractTest _test,
                   final T _user,
                   final String _name)
    {
        super(_test, null, _name, null, null, CueData.REQUIRED_EXPORT_VALUES);
        this.user = _user;
        this.setValue("appliesto", "all");
        this.setValue("type", "*");
        this.setValue("name", "*");
        this.setValue("revision", "*");
        this.setValue("vault", "*");
        this.setValue("owner", "*");
    }

    /**
     * Returns the user for which this cue is define.
     *
     * @return related user of this cue
     * @param <X> class of the user which must be returned
     */
    @SuppressWarnings("unchecked")
    public <X extends AbstractUserData<?>> X getUser()
    {
        return (X) this.user;
    }

    /**
     * Defines if this cue is active or not.
     *
     * @param _active   <i>true</i> if the cue is active; otherwise
     *                  <i>false</i>
     * @return this cue instance
     */
    public CueData<T> setActive(final boolean _active)
    {
        this.active = _active;
        return this;
    }

    /**
     * Defines for which users this cue is visible.
     *
     * @param _users    users for which this cue is visible
     * @return this cue instance
     * @see #visible
     */
    public CueData<T> setVisible(final String... _users)
    {
        this.visible.addAll(Arrays.asList(_users));
        return this;
    }

    /**
     * Returns the part of the CI file to create this cue of a user.
     *
     * @return part of the CI file to create this cue of a user
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape add cue \"")
                .append(AbstractTest.convertTcl(this.getName()))
                .append("\" \\\n    user \"${NAME}\"");

        // hidden flag
        if (this.isHidden())  {
            cmd.append(" \\\n    hidden");
        } else  {
            cmd.append(" \\\n    !hidden");
        }

        // active flag
        if (this.active)  {
            cmd.append(" \\\n    active");
        } else  {
            cmd.append(" \\\n    !active");
        }

        // visible users
        if (!this.visible.isEmpty())  {
            for (final String user : this.visible)  {
                cmd.append(" \\\n    visible \"")
                   .append(AbstractTest.convertTcl(user))
                   .append('\"');
            }
        }

        this.append4CIFileValues(cmd);

        return cmd.append('\n').toString();
    }

    /**
     * @return this cue instance
     * @throws MatrixException if create failed
     */
    @Override()
    public CueData<?> create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            final StringBuilder cmd = new StringBuilder()
                    .append("escape add cue  \"")
                    .append(AbstractTest.convertMql(this.getName()))
                    .append("\" user \"")
                    .append(AbstractTest.convertMql(this.user.getName()))
                    .append('\"');

            // hidden flag
            if (this.isHidden())  {
                cmd.append(" hidden");
            } else  {
                cmd.append(" !hidden");
            }

            // active flag
            if (this.active)  {
                cmd.append(" active");
            } else  {
                cmd.append(" !active");
            }

            // visible users
            if (!this.visible.isEmpty())  {
                cmd.append(" visible ");
                boolean first = true;
                for (final String user : this.visible)  {
                    if (first)  {
                        first = false;
                    } else  {
                        cmd.append(',');
                    }
                    cmd.append('\"').append(AbstractTest.convertMql(user)).append('\"');
                }
            }

            // append values
            this.append4Create(cmd);

            this.getTest().mql(cmd);

            this.setCreated(true);
        }
        return this;
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
                              "check that cue '" + this.getName() + "' is hidden");
        } else  {
            Assert.assertTrue(main.contains("!hidden") || main.contains("!hidden \\"),
                              "check that cue '" + this.getName() + "' is not hidden");
        }

        // check active flag
        if (this.active)  {
            Assert.assertTrue(main.contains("active") || main.contains("active \\"),
                              "check that cue '" + this.getName() + "' is active");
        } else  {
            Assert.assertTrue(main.contains("!active") || main.contains("!active \\"),
                              "check that cue '" + this.getName() + "' is not active");
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
    }
}
