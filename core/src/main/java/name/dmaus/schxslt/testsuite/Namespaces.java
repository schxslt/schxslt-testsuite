/*
 * Copyright (C) 2021 by David Maus <dmaus@dmaus.name>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package name.dmaus.schxslt.testsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * NamespaceContext implementation.
 */
final class Namespaces implements NamespaceContext
{
    private final Map<String, String> decls = new HashMap<String, String>();

    Namespaces (final Element element)
    {
        decls.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        decls.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);

        Node parent = element;
        while (parent != null) {
            NamedNodeMap attrs = parent.getAttributes();
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    Node node = attrs.item(i);
                    if (XMLConstants.XMLNS_ATTRIBUTE.equals(node.getNodeName())) {
                        decls.put(XMLConstants.NULL_NS_URI, node.getNodeValue());
                    }
                    if (XMLConstants.XMLNS_ATTRIBUTE.equals(node.getPrefix())) {
                        decls.put(node.getLocalName(), node.getNodeValue());
                    }
                }
            }
            parent = parent.getParentNode();
        }
    }

    public String getNamespaceURI (final String prefix)
    {
        if (decls.containsKey(prefix)) {
            return decls.get(prefix);
        }
        return XMLConstants.NULL_NS_URI;
    }

    public String getPrefix (final String namespaceURI)
    {
        if (decls.containsValue(namespaceURI)) {
            for (Map.Entry<String, String> entry : decls.entrySet()) {
                if (entry.getValue().equals(namespaceURI)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public Iterator<String> getPrefixes (final String namespaceURI)
    {
        List<String> prefixes = new ArrayList<String>();
        for (Map.Entry<String, String> entry : decls.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                prefixes.add(entry.getKey());
            }
        }
        return prefixes.listIterator();
    }

    void declare (final String prefix, final String namespaceURI)
    {
        decls.put(prefix, namespaceURI);
    }
}
