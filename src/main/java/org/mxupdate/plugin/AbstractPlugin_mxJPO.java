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

package org.mxupdate.plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import matrix.util.Mime64;

/**
 * Abstract class which defines common methods to extract called parameters
 * from the Eclipse plug-in.
 *
 * @author The MxUpdate Team
 */
abstract class AbstractPlugin_mxJPO
{
    /**
     * Returns the argument for given key if defined.
     *
     * @param <T>           searched class of the argument
     * @param _arguments    arguments map
     * @param _key          key within the arguments map
     * @param _default      default value (if key is not defined in the
     *                      arguments map)
     * @return found value in the arguments map or <code>_default</code> if not
     *         found
     */
    @SuppressWarnings("unchecked")
    protected final <T> T getArgument(final Map<String,Object> _arguments,
                                      final String _key,
                                      final T _default)
    {
        return (_arguments.get(_key) != null) ? (T) _arguments.get(_key) : _default;
    }

    /**
     * Decodes given string value to an object of given type
     * <code>&lt;T&gt;</code>. First the string is <b>base64</b> decoded, then
     * the object instance is extracted from the decoded bytes via the Java
     * &quot;standard&quot; feature of the {@link ObjectInputStream}.
     *
     * @param <T>       type of the object which must be decoded
     * @param _args     string array with all values (base64 encoded)
     * @param _index    index within the string array
     * @return decoded object instance of given type <code>&lt;T&gt;</code>
     * @throws IOException              if the value could not be decoded,
     *                                  the decoder stream could not be
     *                                  opened or the argument at given
     *                                  <code>_index</code> is not defined
     * @throws ClassNotFoundException   if the object itself could not be read
     *                                  from decoder stream
     */
    @SuppressWarnings("unchecked")
    protected final <T> T decode(final String[] _args,
                                 final int _index)
        throws IOException, ClassNotFoundException
    {
        if (_index >= _args.length)  {
            throw new IOException("Argument " + _index + " is not defined!");
        }

        final byte[] bytes = Mime64.decode(_args[_index]);

        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        final ObjectInputStream ois = new ObjectInputStream(in);
        final T fileNames = (T) ois.readObject();
        ois.close();
        return fileNames;
    }

    /**
     * Works like {@link #decode(String[], int)}, only if at
     * <code>_index</code> of the string array <code>_args</code> no value is
     * defined, the default value <code>_default</code> is returned instead.
     *
     * @param <T>       type of the object which must be decoded
     * @param _args     string array with all values (base64 encoded)
     * @param _index    index within the string array
     * @param _default  default value if the <code>_index</code> is not within
     *                  the string array <code>_args</code>
     * @return decoded object instance of given type <code>&lt;T&gt;</code>
     * @throws IOException              if the value could not be decoded,
     *                                  the decoder stream could not be
     *                                  opened or the argument at given
     *                                  <code>_index</code> is not defined
     * @throws ClassNotFoundException   if the object itself could not be read
     *                                  from decoder stream
     * @see #decode(String[], int)
     */
    protected final <T> T decode(final String[] _args,
                                 final int _index,
                                 final T _default)
        throws IOException, ClassNotFoundException
    {
        final T ret = ((_index >= _args.length) || (_args[_index] == null))
                      ? _default
                      : this.<T>decode(_args, _index);
        return (ret == null) ? _default : ret;
    }
}
