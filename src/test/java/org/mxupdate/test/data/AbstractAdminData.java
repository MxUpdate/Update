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
import java.util.List;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.util.DataList;
import org.mxupdate.test.data.util.FlagList;
import org.mxupdate.test.data.util.FlagList.Create;
import org.mxupdate.test.data.util.PropertyDef;
import org.mxupdate.test.data.util.PropertyDefList;

import matrix.util.MatrixException;

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
    /** All defined data elements. */
    private final DataList<AbstractAdminData<?>> datas = new DataList<>();
    /** All properties for this data piece. */
    private final PropertyDefList properties = new PropertyDefList();

    /** Update lines. */
    private final List<String> ciLines = new ArrayList<>();

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
                                final String _name)
    {
        super(_test, _ci, _name);
        this.symbolicName = (_ci != null)
                            ? _ci.getMxType() + "_" + this.getName().replaceAll(AbstractAdminData.NOT_ALLOWED_CHARS, "")
                            : null;
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
     * @param _tag          tag (name) of the flag
     * @param _value        <i>true</i> to activate the flag; otherwise
     *                      <i>false</i>; to undefine set to {@code null}
     * @return this data instance
     * @see #flags
     */
    @SuppressWarnings("unchecked")
    public DATA setFlag(final String _tag,
                        final Boolean _value)
    {
        this.flags.setFlag(_tag, _value);
        return (DATA) this;
    }

    /**
     * Defines the flag and the value.
     *
     * @param _tag          tag (name) of the flag
     * @param _value        <i>true</i> to activate the flag; otherwise
     *                      <i>false</i>; to undefine set to {@code null}
     * @param _createConf   create configuration
     * @return this data instance
     * @see #flags
     */
    @SuppressWarnings("unchecked")
    public DATA setFlag(final String _tag,
                        final Boolean _value,
                        final Create _createConf)
    {
        this.flags.setFlag(_tag, _value, _createConf);
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
     * Defines a {@code _data} for given {@code _tag}.
     *
     * @param _tag          used tag (name) of the data
     * @param _data         data instance
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA defData(final String _tag,
                        final AbstractAdminData<?> _data)
    {
        this.datas.add(_tag, _data);
        return (DATA) this;
    }

    /**
     * Defines 'all' given {@code _tag}.
     *
     * @param _tag          used tag (name)
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA defDataAll(final String _tag)
    {
        this.datas.addAll(_tag);
        return (DATA) this;
    }

    /**
     * Returns the {@link #datas data list}.
     *
     * @return data list
     */
    public DataList<AbstractAdminData<?>> getDatas()
    {
        return this.datas;
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
        this.properties.addProperty(_property);
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
     * Returns the content for the configuration item update file for this
     * admin data instance.
     *
     * @return configuration item update file
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder strg = new StringBuilder();
        this.append4CIFileHeader(strg);
        strg.append("mxUpdate " + this.getCI().getMxType() + " \"${NAME}\" {\n");

        this.flags          .append4Update("    ", strg);
        this.getValues()    .append4Update("    ", strg);
        this.getSingles()   .append4Update("    ", strg);
        this.getKeyValues() .append4Update("    ", strg);
        this.datas          .append4Update("    ", strg);
        this.properties     .append4Update("    ", strg);
        for (final String ciLine : this.getCILines())  {
            strg.append("    ").append(ciLine).append('\n');
        }
        strg.append("}");

        return strg.toString();
    }

    /**
     * Creates this data piece.
     *
     * @return this admin data instance
     * @throws MatrixException if create failed
     */
    @Override()
    @SuppressWarnings("unchecked")
    public DATA create()
        throws MatrixException
    {
        if (!this.isCreated())  {
            this.createDependings();

            final StringBuilder cmd = new StringBuilder()
                    .append("escape add " + this.getCI().getMxType() + " \"" + AbstractTest.convertMql(this.getName()) + "\"");

            this.append4Create(cmd);

            this.getTest().mql(cmd);

            this.getTest().mql(new  StringBuilder()
               .append("escape add property ").append(this.getSymbolicName())
               .append(" on program eServiceSchemaVariableMapping.tcl")
               .append(" to " + this.getCI().getMxType() + " \"").append(AbstractTest.convertMql(this.getName())).append("\""));

            this.setCreated(true);
        }

        return (DATA) this;
    }

    @Override()
    @SuppressWarnings("unchecked")
    public DATA createDependings()
        throws MatrixException
    {
        this.datas      .createDependings();
        this.properties .createDependings();

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
        this.getSingles()   .append4Create(_cmd);
        this.getValues()    .append4Create(_cmd);
        this.getKeyValues() .append4Create(_cmd);
        this.flags          .append4Create(_cmd);
        this.datas          .append4Create(_cmd);
        this.properties     .append4Create(_cmd);
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
    {
        super.checkExport(_exportParser);

        this.flags          .check4Export(_exportParser, "");
        this.datas          .check4Export(_exportParser, "");
        this.properties     .check4Export(_exportParser, "");
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
