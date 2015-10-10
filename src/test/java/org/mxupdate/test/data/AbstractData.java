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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.AbstractTest.CI;
import org.mxupdate.test.ExportParser;
import org.mxupdate.test.data.util.KeyNotDefinedList;
import org.mxupdate.test.data.util.KeyValueList;
import org.mxupdate.test.data.util.SingleValueList;
import org.mxupdate.test.data.util.StringValueList;
import org.mxupdate.test.test.update.WrapperCIInstance;
import org.mxupdate.test.util.Version;
import org.mxupdate.typedef.TypeDef_mxJPO;
import org.mxupdate.update.AbstractObject_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO.ErrorKey;
import org.testng.Assert;

import matrix.util.MatrixException;

/**
 * Defines common information from administration objects used to create,
 * update and check them.
 *
 * @param <DATA>    class which is derived from this class
 * @author The MxUpdate Team
 */
public abstract class AbstractData<DATA extends AbstractData<?>>
{
    /** Related test case where this data piece was created. */
    private final AbstractTest test;

    /** Related configuration item of this data piece. */
    private final AbstractTest.CI ci;

    /** Name of the data piece. */
    private final String name;

    /** Single values of this data piece. */
    private final SingleValueList singles = new SingleValueList();
    /** Values of this data piece. */
    private final StringValueList values = new StringValueList();
    /** All properties for this data piece. */
    private final KeyValueList keyValues = new KeyValueList();
    /** All not defined keys. */
    private final KeyNotDefinedList keyNotDefineds = new KeyNotDefinedList();

    /** Flag to indicate that this data piece is created.*/
    private boolean created;

    /** Minimum version. */
    private Version versionMax;
    /** Maximum version. */
    private Version versionMin;
    /** Set of versions where this definition is NOT supported. */
    private final Set<Version> versionsNotSupported = new HashSet<>();

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the administration object
     */
    protected AbstractData(final AbstractTest _test,
                           final AbstractTest.CI _ci,
                           final String _name)
    {
        this.test = _test;
        this.ci = _ci;
        if ((_ci != null) && (_ci.getBusType() != null) && (_ci.getMxType() == null))  {
            this.name = _name;
        } else  {
            this.name = (_name != null) ? AbstractTest.PREFIX + _name : null;
        }
    }

    /**
     * Returns related test case with the MQL console.
     *
     * @return related test case
     * @see #test
     */
    protected AbstractTest getTest()
    {
        return this.test;
    }

    /**
     * Returns related configuration item type.
     *
     * @return configuration item type
     * @see #ci
     */
    public CI getCI()
    {
        return this.ci;
    }

    /**
     * Returns the name of the abstract data element.
     *
     * @return name of the abstract data element
     * @see #name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Defines a new value entry which is put into {@link #singles}.
     *
     * @param _key      key of the value (e.g. &quot;description&quot;)
     * @param _value    value of the value
     * @return this original data instance
     */
    @SuppressWarnings("unchecked")
    public DATA setSingle(final String _key,
                          final String _value)
    {
        this.singles.def(_key, _value);
        return (DATA) this;
    }

    /**
     * Returns all defined {@link #values}.
     *
     * @return defined values
     */
    public SingleValueList getSingles()
    {
        return this.singles;
    }

    /**
     * Defines a new value entry which is put into {@link #values}.
     *
     * @param _key      key of the value (e.g. &quot;description&quot;)
     * @param _value    value of the value
     * @return this original data instance
     * @see #values
     */
    @SuppressWarnings("unchecked")
    public DATA setValue(final String _key,
                         final String _value)
    {
        this.values.def(_key, _value);
        return (DATA) this;
    }

    /**
     * Returns the value for given key from the {@link #values} map.
     *
     * @param _key      name of the searched key
     * @return value for related <code>_key</code>; if not found
     *         <code>null</code>
     */
    @Deprecated()
    public String getValue(final String _key)
    {
        return this.values.getValue(_key);
    }

    /**
     * Returns all defined {@link #values}.
     *
     * @return defined values
     */
    public StringValueList getValues()
    {
        return this.values;
    }

    /**
     * Defines key / value for given {@code _tag}.
     *
     * @param _tag          used tag (name) of the flag
     * @param _key          key
     * @param _value        value
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA setKeyValue(final String _tag,
                            final String _key,
                            final String _value)
    {
        this.keyValues.addKeyValue(_tag, _key, _value);
        return (DATA) this;
    }

    /**
     * Returns the {@link #keyValues key/value list}.
     *
     * @return key/value list
     */
    public KeyValueList getKeyValues()
    {
        return this.keyValues;
    }

    /**
     * Defines key / value for given {@code _tag}.
     *
     * @param _tag          used tag (name) of the flag
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA defKeyNotDefined(final String _tag)
    {
        this.keyNotDefineds.defKeyNotDefined(_tag);
        return (DATA) this;
    }

    /**
     * Returns the name of the configuration item file.
     *
     * @return name of the CI file
     */
    public String getCIFileName()
    {
        final char[] charName = this.name.toCharArray();
        final StringBuilder fileName = new StringBuilder().append(this.getCI().filePrefix);
        for (final char ch : charName) {
            if (ch == '@')  {
                fileName.append("@@");
            } else if (((ch < '(') || (ch > ')'))
                    && ((ch < '+') || (ch > '.'))
                    && ((ch < '0') || (ch > '9'))
                    && ((ch < 'A') || (ch > 'Z'))
                    && ((ch < 'a') || (ch > 'z'))
                    && (ch != ' ') && (ch != '=') && (ch != '_'))  {

                final String hex = String.valueOf(Integer.toHexString(ch));
                fileName.append('@');
                switch (hex.length())  {
                    case 1:
                        fileName.append('0').append(hex);
                        break;
                    case 3:
                        fileName.append("u0").append(hex);
                        break;
                    case 4:
                        fileName.append('u').append(hex);
                        break;
                    default:
                        fileName.append(hex);
                        break;
                }
            } else  {
                fileName.append(ch);
            }
        }
        fileName.append(".mxu");

        return fileName.toString();
    }

    /**
     * Returns the expected name of the configuration item file from the
     * export.
     *
     * @return name of the CI file
     */
    public String getCIFileNameFromExport()
    {
        return this.getCIFileName();
    }


    /**
     * Returns the update definition of this configuration item.
     *
     * @return file content of the configuration item used to make an update
     */
    public abstract String ciFile();

    /**
     * Defines if the related object in MX is already created.
     *
     * @param _created  new created value
     * @see #created
     */
    protected void setCreated(final boolean _created)
    {
        this.created = _created;
    }

    /**
     * Returns <i>true</i> if the related object in MX is created.
     *
     * @return <i>true</i> if the related object in MX is created; otherwise
     *         <i>false</i>
     * @see #created
     */
    protected boolean isCreated()
    {
        return this.created;
    }

    /**
     * Defines {@link #versionsNotSupported not supported versions}.
     *
     * @param _versions     not supported versions
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA defNotSupported(final Version... _versions)
    {
        this.versionsNotSupported.addAll(Arrays.asList(_versions));
        return (DATA) this;
    }

    /**
     * Defines {@link #versionsNotSupported not supported versions}.
     *
     * @param _versions     not supported versions
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA defMaxSupported(final Version _version)
    {
        this.versionMax = _version;
        return (DATA) this;
    }

    /**
     * Defines {@link #versionsNotSupported not supported versions}.
     *
     * @param _versions     not supported versions
     * @return this data instance
     */
    @SuppressWarnings("unchecked")
    public DATA defMinSupported(final Version _version)
    {
        this.versionMin = _version;
        return (DATA) this;
    }

    /**
     * Checks if current MX version supports this data definition.
     *
     * @param _version      version to test
     * @return <i>true</i> if {@code _version} is supported; otherwise
     *         <i>false</i>
     * @see #versionsNotSupported
     */
    public boolean isSupported(final Version _version)
    {
        return !this.versionsNotSupported.contains(_version)
                && ((this.versionMax == null) || _version.max(this.versionMax))
                && ((this.versionMin == null) || _version.min(this.versionMin));
    }

    /**
     * Creates the related MX object for this data piece.
     *
     * @return this instance
     * @throws MatrixException if create failed
     */
    public abstract DATA create()
        throws MatrixException;

    /**
     * Creates all depending administration objects for given this instance.
     *
     * @return this data instance
     * @throws MatrixException if create failed
     */
    public abstract DATA createDependings()
        throws MatrixException;

    /**
     * Updates current configuration item.
     *
     * @param _params   parameters
     * @return this data instance
     * @throws Exception if update failed
     */
    public DATA update(final String _expLogText,
                       final String... _params)
        throws Exception
    {
        return this.updateWithCode(this.ciFile(), _expLogText, _params);
    }

    /**
     * Updates current configuration item and checks that given
     * <code>_error</code> is thrown.
     *
     * @param _error    error code
     * @param _params   parameters
     * @return this data instance
     * @throws Exception if update failed
     */
    public DATA failureUpdate(final ErrorKey _error,
                              final String... _params)
        throws Exception
    {
        return this.failedUpdateWithCode(this.ciFile(), _error, _params);
    }

    /**
     * Makes an update for given {@code _code} and checks if the log contains
     * {@code _expLogText}.
     *
     * @param _code         TCL update code
     * @param _expLogText   log contains expected text (if not null)
     * @param _params       parameters
     * @return this data instance
     * @throws Exception  if update failed
     */
    @SuppressWarnings("unchecked")
    public DATA updateWithCode(final String _code,
                               final String _expLogText,
                               final String... _params)
        throws Exception
    {
        final Map<String,String> files = new HashMap<>();
        files.put(this.getCIFileName(), _code);
        final Map<String,String> params = new HashMap<>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }
        final Map<?,?> bck = this.getTest().executeEncoded("Update", params, "FileContents", files);

        if (_expLogText != null)  {
            Assert.assertTrue(((String) bck.get("log")).contains(_expLogText), "Log message not contained: " + _expLogText + ", have log " + (String) bck.get("log"));
        }

        if (bck.get("exception") != null)  {
            throw new Exception((Exception) bck.get("exception"));
        }
        return (DATA) this;
    }

    /**
     * Makes an update for given <code>_code</code> and checks that given
     * <code>_error</code> is thrown.
     *
     * @param _code         TCL update code
     * @param _error        error which must be thrown
     * @param _params       parameters
     * @return this data instance
     * @throws Exception  if update failed
     */
    @SuppressWarnings("unchecked")
    public DATA failedUpdateWithCode(final String _code,
                                     final ErrorKey _error,
                                     final String... _params)
        throws Exception
    {
        final Map<String,String> files = new HashMap<>();
        files.put(this.getCIFileName(), _code);
        final Map<String,String> params = new HashMap<>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx], _params[idx + 1]);
            }
        }
        final Map<?,?> bck = this.getTest().executeEncoded("Update", params, "FileContents", files);
        Assert.assertTrue(
                bck.containsKey("exception"),
                "check exception exists");
        Assert.assertNotNull(
                bck.get("exception"),
                "check exception is not null");
        Assert.assertTrue(
                ((Exception) bck.get("exception")).getMessage().indexOf("UpdateError #" + _error.getCode() + ":") >= 0,
                "check for correct error code #" + _error.getCode() + " (have: " + ((Exception) bck.get("exception")).getMessage() + ")");

        return (DATA) this;
    }

    /**
     * Exports this data piece from MX.
     *
     * @param _params       parameters
     * @return parsed export
     * @throws Exception if export failed
     */
    public ExportParser export(final Object... _params)
        throws Exception
    {
        final Map<String,String> params = new HashMap<>();
        if (_params != null)  {
            for (int idx = 0; idx < _params.length; idx += 2)  {
                params.put(_params[idx].toString(), _params[idx + 1].toString());
            }
        }

        final ParameterCache_mxJPO paramCache = new ParameterCache_mxJPO(this.getTest().getContext(), false);
        final TypeDef_mxJPO typeDef = paramCache.getMapping().getTypeDef(this.getCI().updateType);

        final WrapperCIInstance<AbstractObject_mxJPO<?>> resultWrapper = new WrapperCIInstance<AbstractObject_mxJPO<?>>(typeDef.newTypeInstance(this.getName()));
        resultWrapper.parse(paramCache);

        return new ExportParser(this.getCI(), resultWrapper.write(paramCache), "");
    }

    /**
     * Returns the parsed export instance.
     *
     * @param _ci       related configuration item type
     * @param _code     code of the exported configuration item
     * @param _log      logging from the server
     * @return parsed export instance
     */
    protected ExportParser parseExport(final AbstractTest.CI _ci,
                                       final String _code,
                                       final String _log)
    {
        return new ExportParser(_ci, _code, _log);
    }

    /**
     * Checks the exported values.
     *
     * @param _params   parameters for the export
     * @return this instance
     * @throws Exception if check failed
     * @see #checkExport(ExportParser)
     * @see #export(String...)
     */
    @SuppressWarnings("unchecked")
    public DATA checkExport(final Object... _params)
        throws Exception
    {
        this.checkExport(this.export(_params));
        return (DATA) this;
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     */
    public void checkExport(final ExportParser _exportParser)
    {
        this.singles        .check4Export(_exportParser, "");
        this.values         .check4Export(_exportParser, "");
        this.keyValues      .check4Export(_exportParser, "");
        this.keyNotDefineds .check4Export(_exportParser, "");
    }

    /**
     * Checks for given <code>_tag</code> in the <code>_exportParser</code>
     * if the <code>_value</code> exists and is correct defined. If
     * <code>_value</code> is <code>null</code>, it is checked that no value is
     * defined.
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _value            value to check (or <code>null</code> if value
     *                          is not defined)
     */
    public void checkSingleValue(final ExportParser _exportParser,
                                 final String _kind,
                                 final String _tag,
                                 final String _value)
    {
        if (_value != null)  {
            Assert.assertEquals(_exportParser.getLines("/mxUpdate/" + _tag + "/@value").size(),
                                1,
                                "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is defined");
            Assert.assertEquals(_exportParser.getLines("/mxUpdate/" + _tag + "/@value").get(0),
                                _value,
                                "check " + _kind + " for '" + this.getName() + "' that " + _tag + " is " + _value);

        } else  {
            Assert.assertEquals(_exportParser.getLines("/mxUpdate/" + _tag + "/@value").size(),
                                0,
                                "check " + _kind + " '" + this.getName() + "' that no " + _tag + " is defined");
        }
    }

    /**
     * Checks in the {@code _exportParser} that given {@code _tag} does not
     * exits.
     *
     * @param _exportParser     parsed export
     * @param _kind             kind of the check
     * @param _tag              tag to check
     * @param _value            value to check (or <code>null</code> if value
     *                          is not defined)
     * @deprecated use methods from export parser
     */
    @Deprecated()
    protected void checkNotExistingSingleValue(final ExportParser _exportParser,
                                               final String _kind,
                                               final String _tag)
    {
        Assert.assertEquals(_exportParser.getLines("/mxUpdate/" + _tag + "/@value").size(),
                            0,
                            "check " + _kind + " '" + this.getName() + "' that no " + _tag + " is defined");
    }

    /**
     * Returns the string representation of this data piece as concatenation of
     * the configuration type {@link #ci} and the {@link #name}.
     *
     * @return string representation of this data piece
     */
    @Override()
    public String toString()
    {
        return "[" + (this.ci == null ? "NONE" : this.ci.updateType) + " '" + this.name + "']";
    }
}
