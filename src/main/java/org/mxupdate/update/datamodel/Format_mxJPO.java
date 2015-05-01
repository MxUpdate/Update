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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.mxupdate.mapping.TypeDef_mxJPO;
import org.mxupdate.update.AbstractAdminObject_mxJPO;
import org.mxupdate.update.datamodel.format.FormatDefParser_mxJPO;
import org.mxupdate.update.util.DeltaUtil_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO;
import org.mxupdate.update.util.MqlBuilder_mxJPO.MultiLineMqlBuilder;
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
     * Writes the TCL update file for this format. The original method is
     * overwritten because a format could not be only updated. A compare
     * must be done in front or otherwise some data is lost. >
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not be written to the
     *                     writer instance
     */
    @Override()
    protected void write(final ParameterCache_mxJPO _paramCache,
                         final Appendable _out)
        throws IOException
    {
        this.writeHeader(_paramCache, _out);

        _out.append("mxUpdate format \"${NAME}\"  {\n")
            .append("    description \"").append(StringUtil_mxJPO.convertUpdate(this.getDescription())).append("\"\n")
            .append("    ").append(this.isHidden() ? "" : "!").append("hidden\n")
            .append("    mime \"").append(StringUtil_mxJPO.convertUpdate(this.mimeType)).append("\"\n")
            .append("    suffix \"").append(StringUtil_mxJPO.convertUpdate(this.fileSuffix)).append("\"\n")
            .append("    type \"").append(StringUtil_mxJPO.convertUpdate(this.type)).append("\"\n")
            .append("    version \"").append(StringUtil_mxJPO.convertUpdate(this.version)).append("\"\n");
        if (_paramCache.getValueBoolean(ValueKeys.DMFormatSupportsPrograms))  {
            _out.append("    view \"").append(StringUtil_mxJPO.convertUpdate(this.commandView)).append("\"\n")
                .append("    edit \"").append(StringUtil_mxJPO.convertUpdate(this.commandEdit)).append("\"\n")
                .append("    print \"").append(StringUtil_mxJPO.convertUpdate(this.commandPrint)).append("\"\n");
        }

        this.getProperties().writeProperties(_paramCache, _out, "    ");

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
     * The method is called from the TCL update code to define the this
     * format. If the correct use case is defined method
     * {@link #updateDimension(ParameterCache_mxJPO, String)} is called.
     *
     * @param _paramCache   parameter cache
     * @param _args         first index defines the use case (must be
     *                      &quot;updateAttribute&quot; that the format
     *                      is updated); second index the name of the format
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
        if ((_args.length == 4) && "mxUpdate".equals(_args[0]) && "format".equals(_args[1])) {
// TODO: Exception Handling
            // check that format names are equal
            if (!this.getName().equals(_args[2]))  {
                throw new Exception("wrong format '" + _args[2] + "' is set to update (currently format '" + this.getName() + "' is updated!)");
            }

            final String code = _args[3].replaceAll("@0@0@", "'").replaceAll("@1@1@", "\\\"");

            final FormatDefParser_mxJPO parser = new FormatDefParser_mxJPO(new StringReader(code));
            final Format_mxJPO format = parser.parse(_paramCache, this.getTypeDef(), this.getName());

            final MultiLineMqlBuilder mql = MqlBuilder_mxJPO.multiLine("escape mod format $1", this.getName());

            this.calcDelta(_paramCache, mql, format);

            mql.exec(_paramCache);

        } else  {
            super.jpoCallExecute(_paramCache, _args);
        }
    }

    /**
     * Calculates the delta between this current format definition and the
     * {@code _target} format definition and appends the MQL append commands
     * to {@code _mql}.
     *
     * @param _paramCache   parameter cache
     * @param _cmd          string builder to append the MQL commands
     * @param _target       target format definition
     * @throws UpdateException_mxJPO if update is not allowed (because data can
     *                      be lost)
     */
    protected void calcDelta(final ParameterCache_mxJPO _paramCache,
                             final MultiLineMqlBuilder _mql,
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

        _target.getProperties().calcDelta(_mql, "", this.getProperties());
    }
}
