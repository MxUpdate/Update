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

package org.mxupdate.test.data.user;

import java.util.HashSet;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.other.SiteData;
import org.testng.Assert;

/**
 * The class is used to define all collection user objects used to create /
 * update and to export.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @param <T> class derived from abstract collection user
 */
public class AbstractCollectionUserData<T extends AbstractCollectionUserData<?>>
    extends AbstractUserData<T>
{
    /**
     * Assigned site for this collection user.
     */
    private SiteData site;

    /**
     * Parent collection users to which this collection user is assigned.
     *
     * @see #assignParent(AbstractCollectionUserData)
     * @see #checkExport(ExportParser)
     */
    private final Set<T> parent = new HashSet<T>();

    /**
     * Within export the description must be defined.
     */
    private static final Set<String> REQUIRED_EXPORT_VALUES = new HashSet<String>(3);
    static  {
        AbstractCollectionUserData.REQUIRED_EXPORT_VALUES.add("description");
    }

    /**
     * Constructor to initialize this collection user.
     *
     * @param _test         related test implementation (where this collection
     *                      user is defined)
     * @param _ci           related configuration type
     * @param _name         name of the collection user
     * @param _filePrefix   prefix for the file name
     * @param _ciPath       path of the configuration item file
     */
    public AbstractCollectionUserData(final AbstractTest _test,
                                  final AbstractTest.CI _ci,
                                  final String _name,
                                  final String _filePrefix,
                                  final String _ciPath)
    {
        super(_test, _ci, _name, _filePrefix, _ciPath, AbstractCollectionUserData.REQUIRED_EXPORT_VALUES);
    }

    /**
     * Defines related site for this collection user.
     *
     * @param _site     site to assign
     * @return this data instance
     * @see #site
     */
    @SuppressWarnings("unchecked")
    public T setSite(final SiteData _site)
    {
        this.site = _site;
        if (_site == null)  {
            this.getValues().remove("site");
        } else  {
            this.setValue("site", this.site.getName());
        }
        return (T) this;
    }

    /**
     * Returns related {@link #site} of this collection user.
     *
     * @return assigned site; or <code>null</code> if not defined
     * @see #site
     */
    public SiteData getSite()
    {
        return this.site;
    }

    /**
     * Assigns <code>_parent</code> to the list of
     * {@link #parent parent collection users}.
     *
     * @param _parent   parent collection user to assign
     * @return this collection user data instance
     * @see #parent
     */
    @SuppressWarnings("unchecked")
    public T assignParent(final T _parent)
    {
        this.parent.add(_parent);
        return (T) this;
    }

    /**
     * Returns all assigned {@link #parent parent collection users}.
     *
     * @return all assigned parent collection users
     * @see #parent
     */
    public Set<T> getParent()
    {
        return this.parent;
    }

    /**
     * Prepares the configuration item update file depending on the
     * configuration of this collection user.
     *
     * @return code for the configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod " + this.getCI().getMxType() + " \"${NAME}\"");
        // hidden flag
        if (this.isHidden())  {
            cmd.append(" \\\n    hidden");
        } else  {
            cmd.append(" \\\n    !hidden");
        }

        this.append4CIFileValues(cmd);

        // define parent collection users
        cmd.append(";\n");
        for (final T user : this.parent)  {
            cmd.append("mql escape mod ")
               .append(user.getCI().getMxType()).append(" \"").append(AbstractTest.convertTcl(user.getName()))
               .append("\" child \"${NAME}\";\n");;
        }

        // append all workspace related objects
        cmd.append(this.ciFileWorkspaceObjects());

        return cmd.toString();
    }

    /**
     * Creates this collection user.
     *
     * @throws MatrixException if create failed
     * @return this collection user data instance
     * @see #hidden
     */
    @SuppressWarnings("unchecked")
    @Override()
    public T create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            final StringBuilder cmd = new StringBuilder()
                    .append("escape add ")
                    .append(this.getCI().getMxType())
                    .append(" \"").append(AbstractTest.convertMql(this.getName())).append("\"");
            // hidden flag
            if (this.isHidden())  {
                cmd.append(" hidden");
            } else  {
                cmd.append(" !hidden");
            }
            // if site assigned, the site must be created
            if (this.site != null)  {
                this.site.create();
            }
            this.append4Create(cmd);

            cmd.append(";\n");
            for (final T user : this.parent)  {
                user.create();
                cmd.append("escape mod ")
                   .append(user.getCI().getMxType()).append(" \"").append(AbstractTest.convertMql(user.getName()))
                   .append("\" child \"").append(AbstractTest.convertMql(this.getName())).append("\";\n");;
            }

            this.getTest().mql(cmd);

            this.createWorkspaceObjects();

            this.setCreated(true);
        }
        return (T) this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * The hidden flag and the {@link #parent collection users} are checked.
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
                              "check that " + this.getCI().getMxType() + " '" + this.getName() + "' is hidden");
        } else  {
            Assert.assertTrue(main.contains("!hidden") || main.contains("!hidden \\"),
                              "check that " + this.getCI().getMxType() + " '" + this.getName() + "' is not hidden");
        }

        // check parent collection users
        final Set<String> pars = new HashSet<String>(_exportParser.getLines("/mql/@value"));
        pars.remove("escape mod " + this.getCI().getMxType() + " \"${NAME}\"");
        for (final T user : this.parent)  {
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
