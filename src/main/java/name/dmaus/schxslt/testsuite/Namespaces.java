/*
 * Copyright (C) 2019,2020 by David Maus <dmaus@dmaus.name>
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

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.XMLConstants;

import javax.xml.namespace.NamespaceContext;

class Namespaces implements NamespaceContext
{
    final Map<String,String> uriByPrefix = new HashMap<String,String>();
    final Map<String, ArrayList<String>> prefixByUri = new HashMap<String, ArrayList<String>>();

    Namespaces ()
    {
        addNamespaceBinding("xml", XMLConstants.XML_NS_URI);
        addNamespaceBinding("xmlns", XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }

    public String getNamespaceURI (final String prefix)
    {
        if (prefix == null) {
            throw new IllegalArgumentException("Namespace prefix must not be null");
        }
        if (uriByPrefix.containsKey(prefix)) {
            return uriByPrefix.get(prefix);
        }
        return XMLConstants.NULL_NS_URI;
    }

    public String getPrefix (final String namespaceURI)
    {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Namespace URI must not be null");
        }
        if (prefixByUri.containsKey(namespaceURI)) {
            return prefixByUri.get(namespaceURI).get(0);
        }
        return null;
    }

    public Iterator getPrefixes (final String namespaceURI)
    {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("Namespace URI must not be null");
        }
        if (prefixByUri.containsValue(namespaceURI)) {
            return prefixByUri.get(namespaceURI).iterator();
        }
        return new ArrayList<String>().iterator();
    }

    boolean isDeclaredPrefix (final String prefix)
    {
        return uriByPrefix.containsKey(prefix);
    }

    void addNamespaceBinding (final String prefix, final String namespaceURI)
    {
        if (uriByPrefix.containsKey(prefix)) {
            throw new IllegalArgumentException("Cannot redeclare prefix " + prefix);
        }
        uriByPrefix.put(prefix, namespaceURI);
        if (prefixByUri.containsKey(namespaceURI)) {
            prefixByUri.get(namespaceURI).add(prefix);
        } else {
            ArrayList<String> prefixes = new ArrayList<String>();
            prefixes.add(prefix);
            prefixByUri.put(namespaceURI, prefixes);
        }
    }
}
