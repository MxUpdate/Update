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

package org.mxupdate.update.user.workspace;

import java.io.IOException;

import org.mxupdate.update.user.AbstractUser_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.mxupdate.update.util.StringUtil_mxJPO;

/**
 * User specific class to store the workspace object for one cue.
 *
 * @author The MxUpdate Team
 */
public class Cue_mxJPO
    extends AbstractVisualQueryWorkspaceObject_mxJPO
{
    /**
     * Order in which the cue is applied in relation to other cues: before,
     * with or after other cues (-1 / 0 / 1).
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String order;

    /**
     * Foreground color.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String foregroundColor;

    /**
     * Highlight color.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String highlightColor;

    /**
     * Name of the font.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String fontName;

    /**
     * Line style.
     *
     * @see #parse(ParameterCache_mxJPO, String, String)
     * @see #write(ParameterCache_mxJPO, Appendable)
     */
    private String lineStyle;

    /**
     * Default constructor.
     *
     * @param _user         related user for this workspace object
     */
    public Cue_mxJPO(final AbstractUser_mxJPO _user)
    {
        super(_user, "cue");
    }

    /**
     * <p>Parses all cue specific URL values. This includes:
     * <ul>
     * <li>{@link #order}</li>
     * <li>{@link #foregroundColor foreground color}</li>
     * <li>{@link #highlightColor highlight color}</li>
     * <li>{@link #fontName name of the font}</li>
     * <li>{@link #lineStyle line style}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache with MX context
     * @param _url          URL to parse
     * @param _content      content of the URL to parse
     * @return <i>true</i> if <code>_url</code> could be parsed; otherwise
     *         <i>false</i>
     */
    @Override()
    public boolean parse(final ParameterCache_mxJPO _paramCache,
                         final String _url,
                         final String _content)
    {
        final boolean parsed;
        if ("/zlevel".equals(_url))  {
            this.order = _content;
            parsed = true;
        } else if ("/foregroundColor".equals(_url))  {
            this.foregroundColor = _content;
            parsed = true;
        } else if ("/highlightColor".equals(_url))  {
            this.highlightColor = _content;
            parsed = true;
        } else if ("/fontName".equals(_url))  {
            this.fontName = _content;
            parsed = true;
        } else if ("/lineStyle".equals(_url))  {
            this.lineStyle = _content;
            parsed = true;
        } else  {
            parsed = super.parse(_paramCache, _url, _content);
        }
        return parsed;
    }

    /**
     * <p>Writes all cue specific values to the TCL update file
     * <code>_out</code>. This includes:
     * <ul>
     * <li>{@link #order}</li>
     * <li>{@link #foregroundColor foreground color}</li>
     * <li>{@link #highlightColor highlight color}</li>
     * <li>{@link #fontName name of the font}</li>
     * <li>{@link #lineStyle line style}</li>
     * </ul></p>
     *
     * @param _paramCache   parameter cache
     * @param _out          appendable instance to the TCL update file
     * @throws IOException if the TCL update code could not written
     */
    @Override()
    public void write(final ParameterCache_mxJPO _paramCache,
                      final Appendable _out)
        throws IOException
    {
        super.write(_paramCache, _out);
        if (this.order != null)  {
            _out.append(" \\\n    order \"").append(StringUtil_mxJPO.convertTcl(this.order)).append("\"");
        }
        if (this.foregroundColor != null)  {
            _out.append(" \\\n    color \"").append(StringUtil_mxJPO.convertTcl(this.foregroundColor)).append("\"");
        }
        if (this.highlightColor != null)  {
            _out.append(" \\\n    highlight \"").append(StringUtil_mxJPO.convertTcl(this.highlightColor)).append("\"");
        }
        if (this.fontName != null)  {
            _out.append(" \\\n    font \"").append(StringUtil_mxJPO.convertTcl(this.fontName)).append("\"");
        }
        if (this.lineStyle != null)  {
            _out.append(" \\\n    linestyle \"").append(StringUtil_mxJPO.convertTcl(this.lineStyle)).append("\"");
        }
    }
}
