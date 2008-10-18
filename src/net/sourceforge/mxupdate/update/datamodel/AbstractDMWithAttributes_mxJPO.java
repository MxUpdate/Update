/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import matrix.db.Context;
import matrix.db.JPO;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convert;

/**
 * The class is used to handle the export / import of administration objects
 * depending on attributes.
 *
 * @author tmoxter
 * @version $Id$
 */
public abstract class AbstractDMWithAttributes_mxJPO
        extends net.sourceforge.mxupdate.update.datamodel.AbstractDMWithTriggers_mxJPO
        implements net.sourceforge.mxupdate.update.util.JPOCaller_mxJPO.JPOCallerInterface
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2194896309854537398L;

    /**
     * List of all attributes for this data model administration object.
     *
     * @see #parse(String, String)              method used to parse attributes
     * @see #writeEnd(Writer)                   method used to write attributes
     *                                          into the TCL update script
     * @see #jpoCallExecute(Context, String...) method used to update
     *                                          attributes of administration
     *                                          object
     */
    private final Set<String> attributes = new TreeSet<String>();

    /**
     * Called TCL procedure within the TCL update to assign attributes to this
     * administration object.
     *
     * @see #update(Context, CharSequence, CharSequence, Map)
     */
    private final static String TCL_PROCEDURE
            = "proc testAttributes {args}  {\n"
                + "global JPO_CALLER_INSTANCE\n"
                + "set iIdx 0\n"
                + "set lsCmd [list mql exec prog net.sourceforge.mxupdate.update.util.JPOCaller $JPO_CALLER_INSTANCE]\n"
                + "while {$iIdx < [llength $args]}  {\n"
                +   "lappend lsCmd [lindex $args $iIdx]\n"
                +   "incr iIdx\n"
                + "}\n"
                + "eval $lsCmd\n"
            + "}\n";

    /**
     * Checks if the URL to parse defined an attribute and appends this
     * name of attribute to the list of attributes {@link #attributes}.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     * @see #attributes
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if ("/attributeDefRefList/attributeDefRef".equals(_url))  {
            this.attributes.add(_content);
        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * Appends at the end of the TCL update file the call to the
     * {@link #TCL_PROCEDURE} to append missing attributes to the
     * administration object.
     *
     * @param _out      writer instance to the TCL update file
     * @see #attributes
     */
    @Override
    protected void writeEnd(final Writer _out)
            throws IOException
    {
        _out.append("\n\ntestAttributes -").append(getInfoAnno().adminType())
            .append(" \"${NAME}\" -attributes [list \\\n");
        for (final String attr : this.attributes)  {
            _out.append("    \"").append(convert(attr)).append("\" \\\n");
        }
        _out.append("]");
    }

    /**
     * Adds the TCL procedure {@link #TCL_PROCEDURE} so that attributes could
     * be assigned to this administration object. The instance itself
     * is stored as encoded string in the TCL variable
     * <code>JPO_CALLER_INSTANCE</code>.
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL command which must be called before the TCL
     *                          code is executed
     * @param _tclCode          TCL code from the file used to update which
     *                          gets the TCL procedure in the front
     * @param _tclVariables     map with TCL variables
     * @see #TCL_PROCEDURE
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        final String[] instance = JPO.packArgs(this);
        final Map<String,String> tclVariables = new HashMap<String,String>();
        tclVariables.putAll(_tclVariables);
        tclVariables.put("JPO_CALLER_INSTANCE", instance[1]);
        super.update(_context, _preMQLCode, TCL_PROCEDURE + _tclCode, tclVariables);
    }

    /**
     * The method is called within the update of an administration object. The
     * method is called directly within the update and checks which attributes
     * are missed in the new definition and adds missing attributes to the
     * administration object. If an attribute is not defined anymore but
     * assigned in Matrix, an exception is thrown.
     *
     * @param _contex   context for this request
     * @param _args     arguments from the TCL procedure
     * @throws Exception if an unknown parameter is defined, the given name of
     *                   the administration object is not the same or an
     *                   attribute is assigned to an administration object
     *                   within Matrix but not defined anymore
     */
    public void jpoCallExecute(final Context _context,
                               final String... _args)
            throws Exception
    {
        final String nameParam = new StringBuilder()
                .append('-').append(getInfoAnno().adminType()).toString();
        int idx = 0;
        String name = null;
        String attrStr = null;
// TODO: parameters -ignoreattr and -removeattr
        while (idx < _args.length)  {
            final String arg = _args[idx];
            if (nameParam.equals(arg))  {
                name = _args[++idx];
            } else if ("-attributes".equals(arg))  {
                attrStr = _args[++idx];
            } else  {
                throw new Exception("unknown parameter \"" + arg + "\"");
            }
            idx++;
        }

        // check for equal administration name
        if (!this.getName().equals(name))  {
            throw new Exception(getInfoAnno().description()
                    + " '" + this.getName() + "' was called to"
                    + " update via update script, but "
                    + getInfoAnno().description() + " '" + name + "' was"
                    + " called in the procedure...");
        }

        // get all attributes
// TODO: if a { or } is in an attribute name, the list is not created correctly
        final Pattern pattern = Pattern.compile("(\\{[^\\{\\}]*\\} )|([^ \\{\\}]* )");
        final Matcher matcher = pattern.matcher(attrStr + " ");
        final Set<String> newAttrs = new TreeSet<String>();
        while (matcher.find())  {
            final String attrName = matcher.group().trim().replaceAll("(^\\{)|(\\}$)", "");
            if (!"".equals(attrName))  {
                newAttrs.add(attrName);
            }
        }

        // now check for not defined but existing attributes
        for (final String attr : this.attributes)  {
            if (!newAttrs.contains(attr))  {
                throw new Exception("Attribute '" + attr + "' is defined to be deleted"
                        + " in " + getInfoAnno().description() + " '"
                        + this.getName() + "', but not allowed!");
            }
        }
        // and check for not existing attributes but needed
        for (final String attr : newAttrs)  {
            if (!this.attributes.contains(attr))  {
System.out.println("    - add attribute '" + attr + "'");
                this.execMql(_context,
                             new StringBuilder()
                                     .append("mod ").append(getInfoAnno().adminType())
                                     .append(" '").append(this.getName()).append('\'')
                                     .append("add attribute '").append(attr).append('\''));
            }
        }
    }

}
