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

package org.mxupdate.test.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.util.FlagList;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.data.util.PropertyDefList;
import org.testng.Assert;

/**
 * Defines common information from administration objects used to create,
 * update and check them.
 *
 * @param <DATA>    class which is derived from this class
 * @author The MxUpdate Team
 */
public abstract class AbstractAdminData<DATA extends AbstractAdminData<?>>
    extends AbstractData<DATA>
{
    /**
     * Regular expression to defines the list of not allowed characters  of
     * symbolic names which are removed for the calculated symbolic name.
     *
     * @see #AbstractAdminData(AbstractTest, org.mxupdate.test.AbstractTest.CI, String, Set)
     */
    private static final String NOT_ALLOWED_CHARS
            = "[^%&()+-0123456789:=ABCDEFGHIJKLMNOPQRSTUVWXYZ^_abcdefghijklmnopqrstuvwxyz~]";

    /** Symbolic name of the data piece. */
    private String symbolicName;

    /** Defines flags for this data piece. */
    private final FlagList flags = new FlagList();

    /**
     * Defines the flags and their default value which must be defined for an
     * export. They are tested for existence from
     * {@link #checkExport(ExportParser)}. The values must be defined at
     * minimum and at maximum once in the configuration item file. The default
     * value is checked only if in {@link #flags} the value is not defined.
     *
     * @see #checkExport(ExportParser)
     */
    private final Map<String,Boolean> requiredExportFlags = new HashMap<String,Boolean>();

    /**
     * Defines the values which must be defined for exports. They are tested
     * for existence from {@link #checkExport(ExportParser)}. This values must
     * be defined minimum and maximum one time in the configuration item file.
     * The key is the name of the value, the value of the map the expected
     * default value.
     *
     * @see #checkExport(ExportParser)
     */
    private final Map<String,Object> requiredExportValues = new HashMap<String,Object>();

    /** All properties for this data piece. */
    private final PropertyDefList properties = new PropertyDefList();

    /** Update lines. */
    private final List<String> ciLines = new ArrayList<String>();

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the administration object
     * @param _requiredExportValues defines the required values of the
     *                              export within the configuration item
     *                              file
     * @param _requiredExportFlags  defines the required flags of the export
     *                              within the configuration item file
     */
    protected AbstractAdminData(final AbstractTest _test,
                                final AbstractTest.CI _ci,
                                final String _name,
                                final Map<String,Object> _requiredExportValues,
                                final Map<String,Boolean> _requiredExportFlags)
    {
        super(_test, _ci, _name);
        this.symbolicName = (_ci != null)
                            ? _ci.getMxType() + "_" + this.getName().replaceAll(AbstractAdminData.NOT_ALLOWED_CHARS, "")
                            : null;
        if (_requiredExportValues != null)  {
            this.requiredExportValues.putAll(_requiredExportValues);
        }
        if (_requiredExportFlags != null)  {
            this.requiredExportFlags.putAll(_requiredExportFlags);
        }
    }

    /**
     * Defines the symbolic name of this data piece.
     *
     * @param _symbolicName     new symbolic name
     * @return this original data instance
     * @see #symbolicName
     */
    @SuppressWarnings("unchecked")
    public DATA setSymbolicName(final String _symbolicName)
    {
        this.symbolicName = _symbolicName;
        return (DATA) this;
    }

    /**
     * Returns the symbolic name of the abstract data element.
     *
     * @return symbolic name of the abstract data element
     * @see #symbolicName
     */
    public String getSymbolicName()
    {
        return this.symbolicName;
    }

    /**
     * Defines the flag and the value.
     *
     * @param _key          key (name) of the flag
     * @param _value        <i>true</i> to activate the flag; otherwise
     *                      <i>false</i>; to undefine set to {@code null}
     * @return this data instance
     * @see #flags
     */
    @SuppressWarnings("unchecked")
    public DATA setFlag(final String _key,
                        final Boolean _value)
    {
        this.flags.setFlag(_key, _value);
        return (DATA) this;
    }

    /**
     * Defines the flag and the value.
     *
     * @param _key          key (name) of the flag
     * @param _value        <i>true</i> to activate the flag; otherwise
     *                      <i>false</i>; to undefine set to {@code null}
     * @param _createConf   create configuration
     * @return this data instance
     * @see #flags
     */
    @SuppressWarnings("unchecked")
    public DATA setFlag(final String _key,
                        final Boolean _value,
                        final Create _createConf)
    {
        this.flags.setFlag(_key, _value, _createConf);
        return (DATA) this;
    }

    /**
     * Returns all defined {@link #flags}.
     *
     * @return all defined flags
     */
    public FlagList getFlags()
    {
        return this.flags;
    }

    /**
     * Returns all {@link #requiredExportValues required export values}.
     *
     * @return required export values
     * @see #requiredExportValues
     */
    public Map<String,Object> getRequiredExportValues()
    {
        return this.requiredExportValues;
    }

    /**
     * Assigns <code>_property</code> to this data piece.
     *
     * @param _property     property to add / assign
     * @return this data piece instance
     * @see #properties
     */
    @SuppressWarnings("unchecked")
    public DATA addProperty(final PropertyDef _property)
    {
        this.properties.add(_property);
        return (DATA) this;
    }

    /**
     * Returns all assigned {@link #properties} from this data piece.
     *
     * @return all defined properties
     */
    public PropertyDefList getProperties()
    {
        return this.properties;
    }

    /**
     * Assigns {@code _line} to this data piece used in the CI file.
     *
     * @param _line     line to append
     * @return this data piece instance
     * @see #properties
     */
    @SuppressWarnings("unchecked")
    public DATA addCILine(final String _line)
    {
        this.ciLines.add(_line);
        return (DATA) this;
    }

    /**
     * Returns the {@link #ciLines lines} used only for the CI file.
     *
     * @return CI lines
     */
    public List<String> getCILines()
    {
        return this.ciLines;
    }

    /**
     * Creates all depending administration objects for given this instance.
     * Only the depending {@link #properties} could be created.
     *
     * @return this data instance
     * @throws MatrixException if create failed
     * @see #properties
     */
    @SuppressWarnings("unchecked")
    public DATA createDependings()
        throws MatrixException
    {
        this.properties.createDependings();

        return (DATA) this;
    }

    /**
     * Appends the file header for the CI file.
     *
     * @param _cmd  string builder of the CI file content
     */
    protected void append4CIFileHeader(final StringBuilder _cmd)
    {
        _cmd.append("#\n")
            .append("# SYMBOLIC NAME:\n")
            .append("# ~~~~~~~~~~~~~~\n")
            .append("# ").append(this.getSymbolicName()).append("\n\n");
    }

    /**
     * Appends the defined {@link #getValues() values} to the TCL code
     * <code>_cmd</code> of the configuration item file.
     *
     * @param _cmd  string builder with the TCL commands of the configuration
     *              item file
     * @see #values
     */
    @Deprecated()
    protected void append4CIFileValues(final StringBuilder _cmd)
    {
        // append flags
        this.flags.append4CIFileValues("    ", _cmd, "\n");
        // append values
        this.getValues().appendUpdate("    ", _cmd);

        // check for add values
        final Set<String> needAdds = new HashSet<String>();
        this.evalAdds4CheckExport(needAdds);
        for (final String needAdd : needAdds)  {
            _cmd.append(" add ").append(needAdd);
        }
        // properties
        for (final PropertyDef property : this.getProperties())  {
            _cmd.append(" property \"").append(AbstractTest.convertTcl(property.getName())).append("\"");
            if (property.getTo() != null)  {
                _cmd.append(" to ").append(property.getTo().getCI().getMxType()).append(" \"")
                    .append(AbstractTest.convertTcl(property.getTo().getName())).append("\"");
                if (property.getTo().getCI() == AbstractTest.CI.UI_TABLE)  {
                    _cmd.append(" system");
                }
            }
            if (property.getValue() != null)  {
                _cmd.append(" value \"").append(AbstractTest.convertTcl(property.getValue())).append("\"");
            }
        }
    }

    /**
     * Appends the MQL commands to define all {@link #flags},
     * {@link #getValues() values} and {@link #properties} within a create.
     *
     * @param _cmd  string builder used to append MQL commands
     * @throws MatrixException if used object could not be created
     * @see #flags
     * @see #getValues() values
     * @see #properties
     */
    protected void append4Create(final StringBuilder _cmd)
        throws MatrixException
    {
        // flags
        this.flags.append4Create(_cmd);

        // values
        for (final Map.Entry<String,Object> entry : this.getValues().entrySet())  {
            if (entry.getValue() instanceof Character)  {
                _cmd.append(' ').append(entry.getKey()).append(' ').append(entry.getValue());
            } else  {
                _cmd.append(' ').append(entry.getKey()).append(" \"")
                    .append(AbstractTest.convertMql(entry.getValue().toString()))
                    .append('\"');
            }
        }
        // properties
        this.properties.append4Create(_cmd);
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);
        this.properties.checkExportPropertiesAddFormat(_exportParser, this.getCI());
        Assert.assertEquals(_exportParser.getSymbolicName(), this.getSymbolicName(), "check symbolic name");

        // check for defined values
        this.getValues().checkExport(_exportParser);

        // check for defined flags
        this.getFlags().checkExport(_exportParser, "/" + this.getCI().getUrlTag() + "/", this.getCI().getMxType() + " " + this.getName());

        // check for all required flags
        if (!this.requiredExportFlags.isEmpty())  {
            final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
            for (final Map.Entry<String,Boolean> flag : this.requiredExportFlags.entrySet())  {
                final boolean value = this.flags.contains(flag.getKey()) && (this.flags.getValue(flag.getKey()) != null)
                                      ? this.flags.getValue(flag.getKey())
                                      : flag.getValue();
                Assert.assertEquals(
                        main.contains(flag.getKey()) || main.contains(flag.getKey() + " \\"),
                        value,
                        "check that flag '" + flag.getKey() + "' for " + this.getCI().getMxType() + " '" + this.getName() + "' is defined as " + value);
                Assert.assertEquals(
                        main.contains("!" + flag.getKey()) || main.contains("!" + flag.getKey() + " \\"),
                        !value,
                        "check that flag '" + flag.getKey() + "' for " + this.getCI().getMxType() + " '" + this.getName() + "' is defined " + value);
            }
        }
        // check for all required values
        for (final String valueName : this.requiredExportValues.keySet())  {
            Assert.assertEquals(_exportParser.getLines("/mql/" + valueName + "/@value").size(),
                                1,
                                "required check that minimum and maximum one " + valueName + " is defined");
        }
        // check for add values
        final Set<String> needAdds = new HashSet<String>();
        this.evalAdds4CheckExport(needAdds);
        final List<String> foundAdds = _exportParser.getLines("/mql/add/@value");
        Assert.assertEquals(
                foundAdds.size(),
                needAdds.size(),
                "all adds defined (found adds = " + foundAdds + "; need adds = " + needAdds + ")");
        for (final String foundAdd : foundAdds)  {
            Assert.assertTrue(needAdds.contains(foundAdd), "check that add '" + foundAdd + "' is defined (found adds = " + foundAdds + "; need adds = " + needAdds + ")");
        }

        // check hidden flag
        if (this.getCI() != null)  {
            final Set<String> main = new HashSet<String>(_exportParser.getLines("/mql/"));
            if ((this.getFlags().getValue("hidden") != null) && this.getFlags().getValue("hidden"))  {
                Assert.assertTrue(
                        main.contains("hidden") || main.contains("hidden \\"),
                        "check that " + this.getCI().getMxType() + " '" + this.getName() + "' is hidden");
                Assert.assertTrue(
                        !main.contains("!hidden") && !main.contains("!hidden \\"),
                        "check that " + this.getCI().getMxType() + " '" + this.getName() + "' is hidden");
            } else  {
                Assert.assertTrue(
                        !main.contains("hidden") && !main.contains("hidden \\"),
                        "check that " + this.getCI().getMxType() + " '" + this.getName() + "' is hidden");
// not required... especially for UI elements..
//                Assert.assertTrue(
//                        main.contains("!hidden") || main.contains("!hidden \\"),
//                        "check that " + this.getCI().getMxType() + " '" + this.getName() + "' is hidden");
            }
        }
    }

    /**
     * Evaluates all 'adds' in the configuration item file (e.g. add
     * setting, ...). Because for the abstract data no adds exists this method
     * is only a dummy.
     *
     * @param _needAdds     set with add strings used to append the adds
     */
    @Deprecated()
    protected void evalAdds4CheckExport(final Set<String> _needAdds)
    {
    }
}
