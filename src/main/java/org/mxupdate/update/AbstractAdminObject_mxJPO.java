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

package org.mxupdate.update;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.mxupdate.mapping.PropertyDef_mxJPO;
import org.mxupdate.typedef.EMxAdmin_mxJPO;
import org.mxupdate.update.util.AbstractParser_mxJPO.ParseException;
import org.mxupdate.update.util.AdminPropertyList_mxJPO;
import org.mxupdate.update.util.MqlUtil_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO.ValueKeys;
import org.mxupdate.update.util.StringUtil_mxJPO;
import org.mxupdate.update.zparser.AdminXMLExportObject_mxJPO;
import org.mxupdate.update.zparser.AdminXMLExportParser_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO;
import org.mxupdate.util.MqlBuilderUtil_mxJPO.MqlBuilder;
import org.mxupdate.util.StringUtils_mxJPO;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import matrix.util.MatrixException;

/**
 * The class is used to export, create, delete and update administration
 * objects within MX.
 *
 * @author The MxUpdate Team
 * @param <CLASS> derived from this class
 */
public abstract class AbstractAdminObject_mxJPO<CLASS extends AbstractAdminObject_mxJPO<CLASS>>
    extends AbstractObject_mxJPO<CLASS>
    implements AdminXMLExportObject_mxJPO
{
    /** Name of the parameter to suppress warnings for not parsed URLs. */
    private static final String PARAM_SUPPRESS_URL_WARNINGS = "SuppressUrlWarnings";

    /** Set of all ignored URLs from the XML definition for all admin objects. */
    private static final Set<String> IGNORED_URLS = new HashSet<>();
    static  {
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/release");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/datetime");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/event");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/dtdInfo");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/creationInfo");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/creationInfo/datetime");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/modificationInfo");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/modificationInfo/datetime");
        // because name is defined within constructor
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/name");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/propertyList");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/agent");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/datetime");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/event");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/order");
        AbstractAdminObject_mxJPO.IGNORED_URLS.add("/adminProperties/historyList/history/string");
    }

    /** MX class definition. */
    private final EMxAdmin_mxJPO mxClassDef;

    /** Referenced package name. */
    private String packageRef;

    /** Is the MX object hidden? */
    private boolean hidden = false;

    /** All current defined symbolic names for MX administration objects are stored. */
    private final SortedSet<String> symbolicNames = new TreeSet<>();

    /** List of all properties. */
    private final AdminPropertyList_mxJPO properties = new AdminPropertyList_mxJPO();

    /**
     * Constructor used to initialize the type definition enumeration.
     *
     * @param _mxClass  defines the related MX class enumeration
     * @param _mxName   MX name of the administration object
     */
    protected AbstractAdminObject_mxJPO(final EMxAdmin_mxJPO _mxClassDef,
                                        final String _mxName)
    {
        super(_mxName);
        this.mxClassDef = _mxClassDef;
    }

    /**
     * Returns depending {@link #mxClassDef MX class} enumeration.
     *
     * @return Mx class
     */
    public EMxAdmin_mxJPO mxClassDef()
    {
        return this.mxClassDef;
    }

    /**
     * Returns the {@link #packageRef name of referenced package}.
     *
     * @return name of referenced package
     */
    public String getPackageRef()
    {
        return this.packageRef;
    }

    /**
     * {@inheritDoc}
     * <p>A print on the property with a key is internally done.</p>
     */
    @Override
    public String getPropValue(final ParameterCache_mxJPO _paramCache,
                               final PropertyDef_mxJPO _prop)
        throws MatrixException
    {
        final String tmp = MqlUtil_mxJPO.execMql(_paramCache, new StringBuilder()
                .append("escape print ").append(this.mxClassDef().mxClass())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                .append(this.mxClassDef().mxClassSuffix())
                .append(" select property[").append(_prop.getPropName(_paramCache)).append("] dump"));
        final int length = 7 + _prop.getPropName(_paramCache).length();
        return (tmp.length() >= length)
                     ? tmp.substring(length)
                     : "";
    }

    /**
     * Creates a XML representation of the Object to export, parses them and
     * executes the post preparation {@link #prepare(ParameterCache_mxJPO)}.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException  if the export of the admin object failed
     * @throws ParseException   if the exported XML document could not be
     *                          parsed
     * @see PadSaxHandler                   SAX handler to parse the XML file
     */
    @Override
    public void parse(final ParameterCache_mxJPO _paramCache)
        throws MatrixException, ParseException
    {
        new AdminXMLExportParser_mxJPO(new StringReader(this.execXMLExport(_paramCache))).parse(_paramCache, this);

        // prepare post preparation
        this.prepare();

        // reads symbolic names of the administration objects
        this.parseDBFinish(_paramCache);
    }

    /**
     * Finishes the DB parsing by reading the symbolic names and package names.
     *
     * @param _paramCache   parameter cache
     * @throws MatrixException  if the export of the admin object failed
     */
    protected void parseDBFinish(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        // reads symbolic names of the administration objects
        final String symbProg = _paramCache.getValueString(ValueKeys.RegisterSymbolicNames);
        final String symbProgIdxOf = new StringBuilder().append(" on program ").append(symbProg).append(' ').toString();
        final String symbNames = MqlBuilderUtil_mxJPO.mql()
                .cmd("escape list property on program ").arg(symbProg).cmd(" ")
                .cmd("to ").cmd(this.mxClassDef().mxClass()).cmd(" ").arg(this.getName()).cmd(" ").cmd(this.mxClassDef().mxClassSuffix())
                .exec(_paramCache.getContext());
        if (!symbNames.isEmpty())  {
            for (final String symbName : symbNames.split("\n"))  {
                this.symbolicNames.add(symbName.substring(0, symbName.indexOf(symbProgIdxOf)));
            }
        }

        // evaluate package (with workaround for associations)
        final String pckTmp;
        if (this.mxClassDef() == EMxAdmin_mxJPO.Association)  {
            pckTmp = MqlBuilderUtil_mxJPO.mql()
                    .cmd("escape list package ").arg("*").cmd(" where ").arg("member[association " + this.getName() + "]")
                    .exec(_paramCache.getContext());
        } else  {
            pckTmp = MqlBuilderUtil_mxJPO.mql()
                    .cmd("escape print ").cmd(this.mxClassDef().mxClass()).cmd(" ").arg(this.getName()).cmd(" ").cmd(this.mxClassDef().mxClassSuffix()).cmd(" ")
                    .cmd("select ").arg("package").cmd(" dump")
                    .exec(_paramCache.getContext());
        }
        this.packageRef = StringUtils_mxJPO.isEmpty(pckTmp) ? (String) null : pckTmp;
    }

    /**
     * The XML export is executed and the result returned. The method could be
     * used to overwrite the returned XML export; e.g. if the XML export from MX
     * has some 'problems'.
     *
     * @param _paramCache   parameter cache
     * @return string from the XML export
     * @throws MatrixException if export failed
     */
    protected String execXMLExport(final ParameterCache_mxJPO _paramCache)
        throws MatrixException
    {
        return MqlBuilderUtil_mxJPO.mql().cmd("escape export ").cmd(this.mxClassDef().mxClass()).cmd(" ").arg(this.getName()).cmd(" xml").exec(_paramCache.getContext());
    }

    /**
     * Parsed administration object related XML tags. This includes:
     * <ul>
     * <li>description</li>
     * <li>is the object {@link #hidden}</li>
     * <li>properties</li>
     * </ul>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL within the XML
     * @param _content      value of the URL
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     * @see #IGNORED_URLS
     */
    @Override
    public boolean parseAdminXMLExportEvent(final ParameterCache_mxJPO _paramCache,
                                            final String _url,
                                            final String _content)
    {
        final boolean parsed;
        if (AbstractAdminObject_mxJPO.IGNORED_URLS.contains(_url))  {
            parsed = true;
        } else if ("/adminProperties/description".equals(_url))  {
            this.setDescription(_content);
            parsed = true;
        } else if ("/adminProperties/hidden".equals(_url))  {
            this.hidden = true;
            parsed = true;

        } else if (_url.startsWith("/adminProperties/propertyList"))  {
            parsed = this.properties.parse(_paramCache, _url.substring(29), _content);
        } else  {
            parsed = false;
        }
        return parsed;
    }

    /**
     * Sorted the properties, sets the author and version depending on the
     * properties and reads the symbolic names.
     *
     * @throws MatrixException if the symbolic names could not be extracted
     */
    protected void prepare()
    {
        // sort the properties
        this.properties.prepare();
    }

    /**
     * Deletes administration object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if delete failed
     */
    @Override
    public void delete(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final StringBuilder cmd = new StringBuilder()
                .append("escape delete ").append(this.mxClassDef().mxClass())
                .append(" \"").append(StringUtil_mxJPO.convertMql(this.getName())).append("\" ")
                .append(this.mxClassDef().mxClassSuffix());
        MqlUtil_mxJPO.execMql(_paramCache, cmd);
    }

    /**
     * Creates given administration object from given type with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if the new administration object could not be created
     */
    @Override
    public void createOld(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
        final MqlBuilder mql = MqlBuilderUtil_mxJPO.mql().cmd("escape add ").cmd(this.mxClassDef().mxClass()).cmd(" ").arg(this.getName());
        if (this.mxClassDef().hasMxClassSuffix())  {
            mql.cmd(" ").cmd(this.mxClassDef().mxClassSuffix());
        }
        mql.exec(_paramCache.getContext());
    }

    /**
     * Creates a new administration object with given name.
     *
     * @param _paramCache   parameter cache
     * @throws Exception if create failed
     */
    public void create(final ParameterCache_mxJPO _paramCache)
        throws Exception
    {
// TODO
    }

    /**
     * Getter method for instance variable {@link #hidden}.
     *
     * @return value of instance variable {@link #hidden}.
     */
    protected boolean isHidden()
    {
        return this.hidden;
    }

    /**
     * Returns the set of all defined symbolic names of this administration
     * (not business!) object. The method is the getter method for
     * {@link #symbolicNames}.
     *
     * @return all defined symbolic names
     */
    public SortedSet<String> getSymbolicNames()
    {
        return this.symbolicNames;
    }

    /**
     * Getter method for instance variable {@link #properties}.
     *
     * @return value of instance variable {@link #properties}.
     */
    public AdminPropertyList_mxJPO getProperties()
    {
        return this.properties;
    }

    /**
     * The string representation of this administration object is returned.
     * The string representation is the MQL update script and so method
     * {@link #write(ParameterCache_mxJPO, Appendable)} is called.
     *
     * @return string representation of this administration object
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
/*    @Override
    public String toString()
    {
        final StringWriter writer = new StringWriter();
        try
        {
            this.write(null, writer);
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
        return writer.toString();
    }*/

    ///////////////////////////////////////////////////////////////////////////

    /**
     * SAX handler used to parse the XML exports from XML.
     */
    public class PadSaxHandler
        extends DefaultHandler
    {
        /**
         * Parameter cache for logging purposed.
         */
        private final ParameterCache_mxJPO paramCache;

        /**
         * Holds the current stack (deep) of the tags. If a start element from
         * {@link #startElement(String, String, String, Attributes)} is called
         * a new string is added. If an evaluation of an element is ended from
         * {@link #endElement(String, String, String)}, this element is removed
         * from the stack.
         *
         * @see #getUrl()
         * @see #startElement(String, String, String, Attributes)
         * @see #endElement(String, String, String)
         */
        private final Stack<String> stack = new Stack<>();

        /**
         * Holds the string content within a XML tag. The content is build by
         * {@link #characters(char[], int, int)} and reset by
         * {@link #startElement(String, String, String, Attributes)}.
         *
         * @see #characters(char[], int, int)
         */
        private StringBuilder content = null;

        /**
         * Holds a flag if {@link #evaluate()} already called for an element.
         * If not, the value is <i>false</i>. The value is always reset if
         * an element is started in
         * {@link #startElement(String, String, String, Attributes)}.
         *
         * @see #startElement(String, String, String, Attributes)
         * @see #endElement(String, String, String)
         */
        private boolean evaluated = false;

        /**
         * Initializes the {@link #paramCache parameter cache}.
         *
         * @param _paramCache   parameter cache
         */
        PadSaxHandler(final ParameterCache_mxJPO _paramCache)
        {
            this.paramCache = _paramCache;
        }

        /**
         * An input source defining the entity &quot;ematrixProductDtd&quot; to
         * replace the original DTD file &quot;ematrixml.dtd&quot; which some
         * XML parser wants to open.
         *
         * @param _publicId     not used
         * @param _systemId     not used
         * @return input source where only the &quot;ematrixProductDtd&quot;
         *         entity is defined
         */
        @Override
        public InputSource resolveEntity(final String _publicId,
                                         final String _systemId)
        {
            return new InputSource(new StringReader("<!ENTITY ematrixProductDtd \"\">"));
        }

        /**
         * Adds given characters to {@link #content} if the XML element was not
         * already evaluated (checked with flag {@link #evaluated}).
         *
         * @param _ch   array of characters
         * @param _start    start position within the array of characters
         * @param _length   length of characters within the array of characters
         * @see #content
         */
        @Override
        public void characters(final char[] _ch,
                               final int _start,
                               final int _length)
        {
            if ((_length > 0) && (!this.evaluated))  {
                if (this.content == null)  {
                    this.content = new StringBuilder();
                }
                this.content.append(new String(_ch,_start,_length));
            }
        }

        /**
         * A definition of a XML element is started. If previous XML element
         * was not evaluated, previous XML element is evaluated by calling
         * {@link #evaluate()}. Current XML element is then added to the
         * {@link #stack}.
         *
         * @param _uri          URI of the XML element (not used)
         * @param _localName    local name of the XML element (not used)
         * @param _qName        current name of the XML element
         * @param _attributes   attributes of the XML element (not used)
         * @see #stack
         * @see #evaluated
         * @see #evaluate()
         */
        @Override
        public void startElement(final String _uri,
                                 final String _localName,
                                 final String _qName,
                                 final Attributes _attributes)
        {
            if (!this.evaluated)  {
                this.evaluate();
            }
            this.evaluated = false;
            this.content = null;

            this.stack.add(_qName);
        }

        /**
         * A definition of a XML element is ended. If the XML element is not
         * already evaluated (checked with flag {@link #evaluated}), current
         * XML element is evaluated by {@link #evaluate()}. The XML element is
         * removed from {@link #stack}.
         *
         * @param _uri          URI of the XML element (not used)
         * @param _localName    local name of the XML element (not used)
         * @param _qName        current name of the XML element (not used)
         * @see #stack
         * @see #evaluated
         * @see #evaluate()
         */
        @Override
        public void endElement(final String _uri,
                               final String _localName,
                               final String _qName)
        {
            if (!this.evaluated)  {
                this.evaluate();
                this.evaluated = true;
            }
            this.stack.pop();
        }

        /**
         * Prepares an URL from current {@link #stack}. The URL is build
         * starting with element at <code>_startIndex</code> of the
         * {@link #stack}.
         *
         * @param _startIndex       index which is used as first element in the
         *                          URL
         * @return URL made from {@link #stack}
         * @see #stack
         */
        private String getUrl(final int _startIndex)
        {
            final StringBuilder ret = new StringBuilder();
            for (final String tag : this.stack.subList(_startIndex, this.stack.size()))  {
                ret.append('/').append(tag);
            }
            return ret.toString();
        }

        /**
         * <p>Current XML element is evaluated. The path of the element defined
         * by the {@link #stack} fetched from {@link #getUrl()} is used to
         * identify the XML element. Evaluation of a XML element means to call
         * {@link AbstractAdminObject_mxJPO#parseAdminXMLExportEvent(ParameterCache_mxJPO, String, String)}.</p>
         * <p>The parser is only called if the deep of a XML element is higher
         * than two, because this XML tags defines the administration element.
         * Deep 0 till 2 is used from MX for administration.</p>
         *
         * @see #stack
         */
        private void evaluate()
        {
            if ((this.stack.size() > 2) && !"creationProperties".equals(this.stack.get(1)))  {
                final boolean parsed = AbstractAdminObject_mxJPO.this.parseAdminXMLExportEvent(
                        this.paramCache,
                        this.getUrl(2),
                        (this.content != null) ? this.content.toString() : null);
                if (!parsed && !this.paramCache.getValueBoolean(AbstractAdminObject_mxJPO.PARAM_SUPPRESS_URL_WARNINGS))  {
                    this.paramCache.logWarning("Url '" + this.getUrl(1) + "'"
                            + ((this.content != null) ? (" with value '" + this.content.toString().trim() + "'") : "")
                            + " unknown and not parsed!");
                }
            }
        }
    }
}
