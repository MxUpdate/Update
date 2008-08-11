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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import matrix.db.Context;
import matrix.db.MQLCommand;

public class MxUpdate_mxJPO
{
    /**
     * Map with all user interface related classes and their tag names.
     */
    final static Map<String,Class<? extends net.sourceforge.mxupdate.update.userinterface.AbstractUIObject_mxJPO>> TAGS_UI
            = new HashMap<String,Class<? extends net.sourceforge.mxupdate.update.userinterface.AbstractUIObject_mxJPO>>();
    static  {
        TAGS_UI.put("command", net.sourceforge.mxupdate.update.userinterface.Command_mxJPO.class);
        TAGS_UI.put("form", net.sourceforge.mxupdate.update.userinterface.Form_mxJPO.class);
        TAGS_UI.put("inquiry", net.sourceforge.mxupdate.update.userinterface.Inquiry_mxJPO.class);
        TAGS_UI.put("menu", net.sourceforge.mxupdate.update.userinterface.Menu_mxJPO.class);
        TAGS_UI.put("table", net.sourceforge.mxupdate.update.userinterface.Table_mxJPO.class);
    }

    /**
     * Map with all possible related classed and their tag names. The map is
     * a concatenation of {@link #TAGS_UI}.
     */
    final static Map<String,Class<? extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO>> TAGS_ALL
            = new HashMap<String,Class<? extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO>>();
    static  {
        TAGS_ALL.putAll(TAGS_UI);
    }



    public void mxMain(final Context _context,
                       final String... _args)
            throws Exception
    {
        try {

final String typeArg =             _args[0];
final Map<String,Set<String>> namesMap = getMatchingNames(_context, typeArg , _args[1]);
        final MQLCommand mql = new MQLCommand();


        String path = "/Users/tim/Daten/Bosch/temp";

//System.out.println("tables="+tables);
     // einen XML Reader erzeugen
        XMLReader reader = XMLReaderFactory.createXMLReader();

for (final Map.Entry<String,Set<String>> names : namesMap.entrySet())  {
    final String type = names.getKey();
    for (final String name : names.getValue())  {
        System.out.println(type + " " + name);
        mql.executeCommand(_context, "export " + type + " \"" + name + "\" xml");
        final String xml = mql.getResult();

// den eigenen Sax Content Handler registrieren
PadSaxHandler handler = new PadSaxHandler ();
reader.setContentHandler(handler);
reader.setDTDHandler(handler);
reader.setEntityResolver(handler);


InputSource inputSource = new InputSource(new StringReader(xml));
inputSource.setEncoding("UTF8");
reader.parse(inputSource);

for (final net.sourceforge.mxupdate.update.MatrixObject_mxJPO table : handler.tables)  {
    final File file = new File(path + File.separator + table.getPath() + File.separator + table.getFileName());
    if (!file.getParentFile().exists())  {
        file.getParentFile().mkdirs();
    }
    Writer out = new FileWriter(file);
    table.prepare(_context);
    table.write(out);
    out.flush();
    out.close();
}
    }
}
        } catch (Exception e)  {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param _context  context for this request
     * @param _type
     * @param _match
     * @return
     * @throws Exception
     */
    private Map<String,Set<String>> getMatchingNames(final Context _context,
                                 final String _type,
                                 final String _match)
        throws Exception
    {
        final Map<String,Set<String>> ret = new TreeMap<String,Set<String>>();
        final Class<? extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO> clazz = TAGS_ALL.get(_type);
        if (clazz != null)  {
            net.sourceforge.mxupdate.update.MatrixObject_mxJPO instance = clazz.newInstance();
            ret.put(_type, instance.getMatchingNames(_context, _match));
        } else if ("userinterface".equals(_type))  {
            for (final Map.Entry<String,Class<? extends net.sourceforge.mxupdate.update.userinterface.AbstractUIObject_mxJPO>> entry : TAGS_UI.entrySet())  {
                net.sourceforge.mxupdate.update.MatrixObject_mxJPO instance = entry.getValue().newInstance();
                ret.put(entry.getKey(), instance.getMatchingNames(_context, _match));
            }
        }
        return ret;
    }

    public class PadSaxHandler extends DefaultHandler
    {
        final Stack<String> stack = new Stack<String>();
        StringBuilder content = null;
        private boolean called = false;

        final Stack<Object> objects = new Stack<Object>();

        private String getUrl()
        {
            final StringBuilder ret = new StringBuilder();
            for (final String tag : stack.subList(2, stack.size()))  {
                ret.append('/').append(tag);
            }
            return ret.toString();
        }

        /**
         * An input source with a zero length strin is returned, because
         * the XML parser wants to open file &quot;ematrixml.dtd&quot;.
         */
        @Override
        public InputSource resolveEntity (final String _publicId,
                                          final String _systemId)
            throws IOException, SAXException
        {
            return new InputSource(new StringReader(""));
        }

        @Override
        public void characters(final char[] _ch,
                               final int _start,
                               final int _length)
            throws SAXException
        {

          if (_length > 0) {
            final String content = new String (_ch,_start,_length);
            if (!this.called)  {
              if (this.content == null)  {
                this.content = new StringBuilder();
              }
              this.content.append(content);
            }
          }
        }

        @Override
        public void endElement (final String uri,
                                final String localName,
                                final String qName)
                throws SAXException
        {
            if (!this.called)
            {
                evaluate();
                this.called = true;
            }
            this.stack.pop();
        }

        @Override
        public void startElement(final String _uri,
                                 final String _localName,
                                 final String _qName,
                                 final Attributes _attributes)
                throws SAXException
        {
            if (!this.called)
            {
                evaluate();
            }
            this.called = false;
            this.content = null;

            this.stack.add(_qName);
        }

Stack<net.sourceforge.mxupdate.update.MatrixObject_mxJPO> tables = new Stack<net.sourceforge.mxupdate.update.MatrixObject_mxJPO>();

        private void evaluate()
        {
            if (this.stack.size() == 2)  {
                final String tag = this.stack.get(1);
                final Class<? extends net.sourceforge.mxupdate.update.MatrixObject_mxJPO> clazz = TAGS_ALL.get(tag);
                if (clazz != null)  {
                    try
                    {
                        this.tables.add(clazz.newInstance());
                    }
                    catch (InstantiationException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else if (this.stack.size() > 2)  {
                final String tag = this.stack.get(1);
                if (TAGS_ALL.containsKey(tag))  {
                    this.tables.peek().parse(getUrl(),
                                             (this.content != null) ? this.content.toString() : null);
                }
            }
        }
    }

}
