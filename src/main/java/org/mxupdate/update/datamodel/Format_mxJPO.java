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

package org.mxupdate.update.datamodel;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.format.FormatDefParser_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.util.UpdateException_mxJPO;

/**
 * Handles the export and the update of the format configuration item.
 *
 * @author The MxUpdate Team
 */
public class Format_mxJPO
    extends AbstractAdminObject_mxJPO
{
    /**
     * Set of all ignored URLs from the XML definition for formats.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     */
    private static final Set<String> IGNORED_URLS = new HashSet<String>();
    static  {
        // to be ignored, because identically to fileType
        Format_mxJPO.IGNORED_URLS.add("/fileCreator");
    }

    /**
     * Key used to identify the update of an attribute within
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     */
    private static final String JPO_CALLER_KEY = "updateFormat";

    /**
     * Called TCL procedure within the TCL update to parse the new policy
     * definition. The TCL procedure calls method
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)} with the new
     * policy definition. All quot's are replaced by <code>@0@0@</code> and all
     * apostroph's are replaced by <code>@1@1@</code>.
     */
    private static final String TCL_PROCEDURE
            = "proc updateFormat {_sName _lsArgs}  {\n"
                + "regsub -all {'} $_lsArgs {@0@0@} sArg\n"
                + "regsub -all {\\\"} $sArg {@1@1@} sArg\n"
                + "regsub -all {\\\\\\[} $sArg {[} sArg\n"
                + "regsub -all {\\\\\\]} $sArg {]} sArg\n"
                + "mql exec prog org.mxupdate.update.util.JPOCaller " + Format_mxJPO.JPO_CALLER_KEY + " ${_sName} \"${sArg}\"\n"
            + "}\n";

    /** Reference to the edit program. */
    private String commandEdit = null;
    /** Reference to the print program. */
    private String commandPrint = null;
    /** Reference to the view program. */
    private String commandView = null;

    /** Mime type of the format. */
    private String mimeType = null;
    /** File suffix of the format. */
    private String fileSuffix = null;

    /** Type and creator of the format (used only for MacOS). */
    private String type = null;

    /** Version of the format. */
    private String version = null;

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _typeDef  defines the related type definition enumeration
     * @param _mxName   MX name of the format object
     */
    public Format_mxJPO(final TypeDef_mxJPO _typeDef,
                        final String _mxName)
    {
        super(_typeDef, _mxName);
    }

    /**
     * Parses all format specific URLs.
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    protected boolean parse(final ParameterCache_mxJPO _paramCache,
                            final String _url,
                            final String _content)
    {
        final boolean parsed;
        if (Format_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/editCommand".equals(_url))  {
            this.commandEdit = _content;
            parsed = true;
        } else if ("/printCommand".equals(_url))  {
            this.commandPrint = _content;
            parsed = true;
        } else if ("/viewCommand".equals(_url))  {
            this.commandView = _content;
            parsed = true;

        } else if ("/fileSuffix".equals(_url))  {
            this.fileSuffix = _content;
            parsed = true;
        } else if ("/fileType".equals(_url))  {
            this.type = _content;
            parsed = true;
        } else if ("/mimeType".equals(_url))  {
            this.mimeType = _content;
            parsed = true;

        } else if ("/version".equals(_url))  {
            this.version = _content;
            parsed = true;

        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * Writes the TCL update file for this attribute. The original method is
     * overwritten because am attribute could not be only updated. A compare
     * must be done in front or otherwise some data is lost. >
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written to the
     *                     writer instance
     * @see #PARAM_SUPPORT_FLAG_RESET_ON_CLONE
     * @see #PARAM_SUPPORT_FLAG_RESET_ON_REVISION
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        // write header
        this.writeHeader(_paramCache, _out);

        // write attribute
        _out.append("updateFormat \"${NAME}\"  {\n")
            .append("  description \"").append(StringUtil_mxJPO.convertTcl(this.getDescription())).append("\"\n")
            .append("  ").append(this.isHidden() ? "" : "!").append("hidden\n")
            .append("  mime \"").append(StringUtil_mxJPO.convertTcl(this.mimeType)).append("\"\n")
            .append("  suffix \"").append(StringUtil_mxJPO.convertTcl(this.fileSuffix)).append("\"\n")
            .append("  type \"").append(StringUtil_mxJPO.convertTcl(this.type)).append("\"\n")
            .append("  version \"").append(StringUtil_mxJPO.convertTcl(this.version)).append("\"\n");
        if (_paramCache.getValueBoolean(ValueKeys.DMFormatSupportsPrograms))  {
            _out.append("  view \"").append(StringUtil_mxJPO.convertTcl(this.commandView)).append("\"\n")
                .append("  edit \"").append(StringUtil_mxJPO.convertTcl(this.commandEdit)).append("\"\n")
                .append("  print \"").append(StringUtil_mxJPO.convertTcl(this.commandPrint)).append("\"\n");
        }

        // append properties
        this.getProperties().writeUpdateFormat(_paramCache, _out, "  ");

        _out.append("}");
    }

    /**
     * Only implemented as stub because
     * {@link #write(ParameterCache_mxJPO, Appendable)} is new implemented.
     *
     * @param _paramCache   parameter cache (not used)
     * @param _out          appendable instance to the TCL update file (not
     *                      used)
     */
    @Override()
    protected void writeObject(final ParameterCache_mxJPO _paramCache,
                               final Appendable _out)
        throws IOException
    {
    }

    /**
     * The method overwrites the original method to add the TCL procedure
     * {@link #TCL_PROCEDURE} so that the format could be updated with
     * {@link #jpoCallExecute(ParameterCache_mxJPO, String...)}.
     *
     * @param _paramCache       parameter cache
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _preTCLCode       TCL code which is defined before the source
     *                          file is sourced
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     * @param _sourceFile       souce file with the TCL code to update
     * @throws Exception if the update from derived class failed
     */
    @Override()
    protected void update(final ParameterCache_mxJPO _paramCache,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _preTCLCode,
                          final Map<String,String> _tclVariables,
                          final File _sourceFile)
        throws Exception
    {
        // add TCL code for the procedure
        final StringBuilder preTCLCode = new StringBuilder()
                .append(Format_mxJPO.TCL_PROCEDURE)
                .append(_preTCLCode);

        super.update(_paramCache, _preMQLCode, _postMQLCode, preTCLCode, _tclVariables, _sourceFile);
    }

    /**
     * The method is called from the TCL update code to define the this
     * attribute. If the correct use case is defined method
     * {@link #updateDimension(ParameterCache_mxJPO, String)} is called.
     *
     * @param _paramCache   parameter cache
     * @param _args         first index defines the use case (must be
     *                      &quot;updateAttribute&quot; that the attribute
     *                      is updated); second index the name of the attribute
     *                      to update
     * @throws Exception if the update of the dimension failed or for all other
     *                   use cases from super JPO call
     */
    @Override()
    public void jpoCallExecute(final ParameterCache_mxJPO _paramCache,
                               final String... _args)
        throws Exception
    {
        // check if dimension is defined
        if ((_args.length == 3) && Format_mxJPO.JPO_CALLER_KEY.equals(_args[0])) {
// TODO: Exception Handling
            // check that attribute names are equal
            if (!this.getName().equals(_args[1]))  {
                throw new Exception("wrong format '"
                        + _args[1] + "' is set to update (currently format '" + this.getName()
                        + "' is updated!)");
            }

            final String code = _args[2].replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

            final FormatDefParser_mxJPO parser = new FormatDefParser_mxJPO(new StringReader(code));
            final Format_mxJPO format = parser.format(_paramCache, this.getTypeDef(), this.getName());

            final MqlBuilder_mxJPO mql = MqlBuilder_mxJPO.init("escape mod format $1", this.getName());

            this.calcDelta(_paramCache, mql, format);

            mql.exec(_paramCache);

        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Calculates the delta between this current format definition and the
     * {@code _target} format definition and appends the MQL append commands
     * to {@code _cmd}.
     *
     * @param _paramCache   parameter cache
     * @param _cmd          string builder to append the MQL commands
     * @param _target       target format definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MqlBuilder_mxJPO _mql,
                             final Format_mxJPO _target)
        throws UpdateException_mxJPO
    {
        DeltaUtil_mxJPO.calcValueDelta(_mql, "description",     _target.getDescription(), this.getDescription());
        DeltaUtil_mxJPO.calcFlagDelta(_mql,  "hidden",          _target.isHidden(),       this.isHidden());
        DeltaUtil_mxJPO.calcValueDelta(_mql, "mime",            _target.mimeType,         this.mimeType);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "suffix",          _target.fileSuffix,       this.fileSuffix);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "type",            _target.type,             this.type);
        DeltaUtil_mxJPO.calcValueDelta(_mql, "version",         _target.version,          this.version);

        if (_paramCache.getValueBoolean(ValueKeys.DMFormatSupportsPrograms))  {
            DeltaUtil_mxJPO.calcValueDelta(_mql, "view", _target.commandView, this.commandView);
            DeltaUtil_mxJPO.calcValueDelta(_mql, "edit", _target.commandEdit, this.commandEdit);
            DeltaUtil_mxJPO.calcValueDelta(_mql, "print", _target.commandPrint, this.commandPrint);
        } else  {
            if (_target.commandView != null)  {
                _paramCache.logInfo("    - view program " + _target.commandView + " ignored (not supported anymore!)");
            }
            if (_target.commandEdit != null)  {
                _paramCache.logInfo("    - edit program " + _target.commandEdit + " ignored (not supported anymore!)");
            }
            if (_target.commandPrint != null)  {
                _paramCache.logInfo("    - print program " + _target.commandPrint + " ignored (not supported anymore!)");
            }
        }

        _target.getProperties().calcDelta("", this.getProperties(), _mql);
    }
}
