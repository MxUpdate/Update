/*
 * Copyright 2008-2014 The MxUpdate Team
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
 */

package org.mxupdate.test.data.program;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.AbstractAdminData;
import org.mxupdate.test.data.user.AbstractPersonAdminData;
import org.mxupdate.test.data.user.AbstractUserData;
import org.mxupdate.test.data.util.PropertyDef;
import org.testng.Assert;

/**
 * The class is used to define all program objects used to create / update and
 * to export.
 *
 * @author The MxUpdate Team
 * @param <T>   defines the class which is derived from this class
 */
public abstract class AbstractProgramData<T extends AbstractProgramData<?>>
    extends AbstractAdminData<T>
{
    /**
     * Related code of this program.
     *
     * @see #setCode(String)
     * @see #getCode()
     */
    private String code = "";

    /**
     * Assign used of this program.
     *
     * @see #setUser(AbstractPersonAdminData)
     * @see #getUser()
     */
    private AbstractPersonAdminData<?> user;

    /**
     * Execution of the program is deferred.
     *
     * @see #setDeferred(boolean)
     */
    private boolean deferred = false;

    /**
     * Needs business object.
     *
     * @see #setNeedsBusinessObject(boolean)
     */
    private boolean needsBusinessObject = false;

    /**
     * Program is downloadable.
     *
     * @see #setDownloadable(boolean)
     */
    private boolean downloadable = false;

    /**
     * Program uses pipes.
     *
     * @see #setPipe(boolean)
     */
    private boolean pipe = false;

    /**
     * Program is pooled (used for TCL).
     *
     * @see #setPooled(boolean)
     */
    private boolean pooled = false;

    /**
     * Initialize the values for program objects.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the program
     */
    protected AbstractProgramData(final AbstractTest _test,
                                  final AbstractTest.CI _ci,
                                  final String _name)
    {
        super(_test, _ci, _name, null, null);
    }

    /**
     * The related configuration item file is the {@link #code} of the program.
     *
     *  @return {@link #code} of the program
     *  @see #code
     */
    @Override()
    public String ciFile()
    {
        return this.code;
    }

    /**
     * Defines the {@link #code} for this program.
     *
     * @param _code     new code of this program
     * @return this program instance
     * @see #code
     */
    @SuppressWarnings("unchecked")
    public T setCode(final String _code)
    {
        this.code = _code;
        return (T) this;
    }

    /**
     * Returns the {@link #code} of this program.
     *
     * @return related {@link #code} of this program
     * @see #code
     */
    public String getCode()
    {
        return this.code;
    }

    /**
     * Defines the {@link #user} for this program.
     *
     * @param _user     new user
     * @return this program instance
     * @see #user
     */
    @SuppressWarnings("unchecked")
    public T setUser(final AbstractPersonAdminData<?> _user)
    {
        this.user = _user;
        return (T) this;
    }

    /**
     * Returns the {@link #user} of this program.
     *
     * @return related {@link #user} of this program
     * @see #user
     */
    public AbstractUserData<?> getUser()
    {
        return this.user;
    }

    /**
     * Defines the {@link #deferred} for this program.
     *
     * @param _deferred     <i>true</i> if program execution is deferred
     * @return this program instance
     * @see #deferred
     */
    @SuppressWarnings("unchecked")
    public T setDeferred(final boolean _deferred)
    {
        this.deferred = _deferred;
        return (T) this;
    }

    /**
     * Defines the {@link #needsBusinessObject} for this program.
     *
     * @param _needsBusinessObject  <i>true</i> if program needs business
     *                              object context
     * @return this program instance
     * @see #needsBusinessObject
     */
    @SuppressWarnings("unchecked")
    public T setNeedsBusinessObject(final boolean _needsBusinessObject)
    {
        this.needsBusinessObject = _needsBusinessObject;
        return (T) this;
    }

    /**
     * Defines the {@link #downloadable} for this program.
     *
     * @param _downloadable     <i>true</i> if program is downloadable
     * @return this program instance
     * @see #downloadable
     */
    @SuppressWarnings("unchecked")
    public T setDownloadable(final boolean _downloadable)
    {
        this.downloadable = _downloadable;
        return (T) this;
    }

    /**
     * Defines the {@link #pipe} for this program.
     *
     * @param _pipe     <i>true</i> if program input / output is piped
     * @return this program instance
     * @see #pipe
     */
    @SuppressWarnings("unchecked")
    public T setPipe(final boolean _pipe)
    {
        this.pipe = _pipe;
        return (T) this;
    }

    /**
     * Defines the {@link #pooled} for this program.
     *
     * @param _pooled   <i>true</i> if program is pooled
     * @return this program instance
     * @see #pooled
     */
    @SuppressWarnings("unchecked")
    public T setPooled(final boolean _pooled)
    {
        this.pooled = _pooled;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     * The assigned {@link #user} is created.
     *
     * @see #user
     */
    @Override()
    @SuppressWarnings("unchecked")
    public T createDependings()
        throws MatrixException
    {
        super.createDependings();

        // create user
        if (this.user != null)  {
            this.user.create();
        }

        return (T) this;
    }

    /**
     * Appends the program related MQL commands to create this program. This
     * includes:
     * <ul>
     * <li>{@link #user}</li>
     * <li>{@link #isHidden() hidden flag}</li>
     * <li>{@link #deferred}</li>
     * <li>{@link #needsBusinessObject needs business object}</li>
     * <li>{@link #downloadable} flag</li>
     * <li>{@link #pipe} flag</li>
     * <li>{@link #pooled} flag</li>
     * </ul>
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     */
    @Override()
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        super.append4Create(_cmd);

        // user
        if (this.getUser() != null)  {
            this.getUser().create();
            _cmd.append(" execute user \"").append(AbstractTest.convertMql(this.getUser().getName())).append("\"");
        }
        // hidden flag
        if ((this.getFlags().get("hidden") != null) && this.getFlags().get("hidden"))  {
            _cmd.append(" hidden");
        }
        // deferred flag
        if (this.deferred)  {
            _cmd.append(" execute deferred");
        }
        // needs business object
        if (this.needsBusinessObject)  {
            _cmd.append(" needsbusinessobject");
        }
        // downloable flag
        if (this.downloadable)  {
            _cmd.append(" downloadable");
        }
        // pipe flag
        if (this.pipe)  {
            _cmd.append(" pipe");
        }
        // pooled flag
        if (this.pooled)  {
            _cmd.append(" pooled");
        }
    }

    /**
     * Appends the program related MQL commands to the TCL update code of the
     * configuration item file. This includes:
     * <ul>
     * <li>{@link #user}</li>
     * <li>{@link #isHidden() hidden flag}</li>
     * <li>{@link #deferred}</li>
     * <li>{@link #downloadable} flag</li>
     * <li>{@link #pipe} flag</li>
     * <li>{@link #pooled} flag</li>
     * </ul>
     *
     * @param _cmd  string builder with the TCL commands of the configuration
     *              item file
     */
    @Override()
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        super.append4CIFileValues(_cmd);

        // user
        if (this.getUser() != null)  {
            _cmd.append(" execute user \"").append(AbstractTest.convertTcl(this.getUser().getName())).append("\"");
        }
        // hidden flag
        if ((this.getFlags().get("hidden") != null) && this.getFlags().get("hidden"))  {
            _cmd.append(" hidden");
        }
        // deferred flag
        if (this.deferred)  {
            _cmd.append(" execute deferred");
        }
        // needs business object
        if (this.needsBusinessObject)  {
            _cmd.append(" needsbusinessobject");
        }
        // downloable flag
        if (this.downloadable)  {
            _cmd.append(" downloadable");
        }
        // pipe flag
        if (this.pipe)  {
            _cmd.append(" pipe");
        }
        // pooled flag
        if (this.pooled)  {
            _cmd.append(" pooled");
        }
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     * Because the handling of a program is different (the TCL update code
     * is embedded), the values must be checked directly via MQL commands.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        Assert.assertTrue(!"".equals(this.getTest().mql("escape list program \"" + AbstractTest.convertMql(this.getName()) + "\"")),
                          "check program is created");
        Assert.assertTrue(!"".equals(this.getTest().mql("escape list property to program \"" + AbstractTest.convertMql(this.getName()) + "\"")),
                          "check that the program is registered");
        Assert.assertEquals(this.getTest().mql("escape list property to program \"" + AbstractTest.convertMql(this.getName()) + "\""),
                            this.getSymbolicName()
                                    + " on program eServiceSchemaVariableMapping.tcl to program "
                                    + this.getName(),
                            "check that the MQL program is registered with correct symbolic name");
        // check hidden
        Assert.assertEquals(Boolean.valueOf(this.getTest().mql("escape print program \""
                                        + AbstractTest.convertMql(this.getName()) + "\" select hidden dump")).booleanValue(),
                            (this.getFlags().get("hidden") != null) ? this.getFlags().get("hidden").booleanValue() : false,
                            "hidden flag correct defined");
        // check description
        Assert.assertEquals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName()) + "\" select description dump"),
                            (this.getValue("description") != null) ? this.getValue("description") : "",
                            "correct description defined");
        // check user
        Assert.assertEquals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName()) + "\" select user dump"),
                            (this.getUser() != null) ? this.getUser().getName() : "",
                            "correct user assigned");
        // execution is deferred
        Assert.assertEquals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName()) + "\" select execute dump"),
                            (this.deferred) ? "deferred" : "immediate",
                            "check execution");
        // needs business object
        Assert.assertEquals(Boolean.valueOf(this.getTest().mql("escape print program \""
                                    + AbstractTest.convertMql(this.getName()) + "\" select doesneedcontext dump")).booleanValue(),
                            this.needsBusinessObject,
                            "needs business object flag");
        // downloadable
        Assert.assertEquals(Boolean.valueOf(this.getTest().mql("escape print program \""
                                    + AbstractTest.convertMql(this.getName()) + "\" select downloadable dump")).booleanValue(),
                            this.downloadable,
                            "downloadable flag");
        // pipe
        Assert.assertEquals(Boolean.valueOf(this.getTest().mql("escape print program \""
                                    + AbstractTest.convertMql(this.getName()) + "\" select ispipedprogram dump")).booleanValue(),
                            this.pipe,
                            "pipe flag");

        // check properties
        for (final PropertyDef prop : this.getProperties())  {
            Assert.assertTrue(!"".equals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName())
                                            + "\" select property[" + prop.getName() + "] dump")),
                              "property '" + prop.getName() + "' is defined");
            Assert.assertEquals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName())
                                            + "\" select property[" + prop.getName() + "].value dump"),
                                (prop.getValue() != null) ? prop.getValue() : "",
                                "value of property '" + prop.getName() + "' is defined");
            if (prop.getTo() != null)  {
                Assert.assertEquals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName())
                                    + "\" select property[" + prop.getName() + "].to.name dump"),
                                    prop.getTo().getName(),
                                    "to of property '" + prop.getName() + "' is defined");
            } else  {
                Assert.assertEquals(this.getTest().mql("escape print program \"" + AbstractTest.convertMql(this.getName())
                                            + "\" select property[" + prop.getName() + "].to dump"),
                                    "",
                                    "to of property '" + prop.getName() + "' is not defined");
            }
        }
    }
}
