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

package org.mxupdate.util;

import javax.xml.bind.DatatypeConverter;

/**
 * Utility for base64 stuff.
 *
 * @author The MxUpdate Team
 */
public final class Base64Utils_mxJPO
{
    /**
     * The constructor is defined to avoid external initialization.
     */
    private Base64Utils_mxJPO()
    {
    }

    /**
     * Decodes given {@code _text}.
     *
     * @param _text     text to decode
     * @return decoded bytes
     */
    public static byte[] decode(final CharSequence _text)
    {
        return DatatypeConverter.parseBase64Binary(_text.toString());
    }
}
