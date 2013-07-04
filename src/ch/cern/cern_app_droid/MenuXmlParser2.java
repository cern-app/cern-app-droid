package ch.cern.cern_app_droid;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class MenuXmlParser2 {
	
	private static final String TAG = "DomParser";

	public void parse(InputStream is) {
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc = dbf.newDocumentBuilder().parse(new InputSource(is));
			Node n = doc.getDocumentElement();
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList menuEntries = ((Node) xpath.evaluate("/plist/dict/array", n, XPathConstants.NODE)).getChildNodes();
			int count = menuEntries.getLength();
			
			Log.d(TAG, "menuEntries.Length() = " + count);
			String k;
			String v;
			for (int i = 0 ; i < count ; i++) {
				Node node = menuEntries.item(i);
				Log.d(TAG, "name: " + node.getNodeName() + "; value:" + node.getNodeValue());
				Log.d(TAG, "text: " + node.getTextContent());
				node = node.getFirstChild();
				if (node == null) {
					Log.d(TAG, "child is null");
					continue;
				}
				k = node.getNodeName();
				node = node.getNextSibling();
				if (node == null)
					continue;
				v = node.getNodeValue();
				Log.d(TAG, "key: " + k + ";  value: " + v);
			}
			
			
		} catch (Exception e) {
			Log.d(TAG, "", e);
		}
	}

}
