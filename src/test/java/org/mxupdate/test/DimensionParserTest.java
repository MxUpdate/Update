/*
 * Copyright 2008-2009 The MxUpdate Team
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

package org.mxupdate.test;

import java.io.StringReader;
import java.lang.reflect.Method;

import org.mxupdate.test.util.TestParameterCache;
import org.mxupdate.update.datamodel.Dimension_mxJPO;
import org.mxupdate.update.datamodel.dimension.DimensionDefParser_mxJPO;
import org.mxupdate.update.util.ParameterCache_mxJPO;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Class is used to test the dimension parser {@link DimensionDefParser_mxJPO} with
 * some examples.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class DimensionParserTest
{
    /**
     * Start of the command to update the dimension to extract the code.
     */
    private static final String START_INDEX = "updateDimension \"${NAME}\"  {";

    /**
     * Length of the string of the command to update the dimension.
     *
     * @see #START_INDEX
     */
    private static final int START_INDEX_LENGTH = DimensionParserTest.START_INDEX.length();

    /**
     * Parsed the <code>_definition</code> code and compares the result with
     * <code>_toTest</code>.
     *
     * @param _definition   text of the definition to test
     * @param _toTest       expected result (if empty string
     *                      <code>_definition</code> is the expected result)
     * @throws Exception if <code>_definition</code> could not parsed
     */
    @Test
    @Parameters({"code", "test"})
    public void testDimension(final String _definition,
                              final String _toTest)
            throws Exception
    {
        final ParameterCache_mxJPO paramCache = new TestParameterCache();

        final DimensionDefParser_mxJPO parser = new DimensionDefParser_mxJPO(new StringReader(_definition));
        final Dimension_mxJPO dimension = parser.dimension(paramCache,
                                                           paramCache.getMapping().getTypeDef("Dimension"),
                                                           "Test");

        final StringBuilder bck = new StringBuilder();
        final Method write = dimension.getClass()
                .getDeclaredMethod("write", ParameterCache_mxJPO.class, Appendable.class);
        write.setAccessible(true);
        write.invoke(dimension, paramCache, bck);

        final StringBuilder oldDefBuilder = new StringBuilder();
        for (final String line : ("".equals(_toTest)) ? _definition.split("\n") : _toTest.split("\n"))  {
            oldDefBuilder.append(line.trim()).append(' ');
        }
        int length = 0;
        String oldDef = oldDefBuilder.toString();
        while (length != oldDef.length())  {
            length = oldDef.length();
            oldDef = oldDef.replaceAll("  ", " ");
        }

        final String temp = bck.substring(bck.indexOf(DimensionParserTest.START_INDEX)
                                                      + DimensionParserTest.START_INDEX_LENGTH + 1,
                                          bck.length() - 2)
                               .toString();
        final StringBuilder newDef = new StringBuilder();
        for (final String line : temp.split("\n"))  {
            newDef.append(line.trim()).append(' ');
        }

        Assert.assertEquals(oldDef.trim(), newDef.toString().trim());
    }
}
