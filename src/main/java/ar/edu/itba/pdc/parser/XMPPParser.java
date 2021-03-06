package ar.edu.itba.pdc.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import ar.edu.itba.pdc.exceptions.IncompleteElementsException;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.stanzas.Stanza;

public class XMPPParser {

	/**
	 * Parses an XML element.
	 * 
	 * First, adds a root node to it in order to make it XML valid.
	 * 
	 * Then, parses the XML returning a list of stanzas and throwing an
	 * exception in case any stanza turns out to be incomplete
	 * 
	 * @param xmlStream
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws IncompleteElementsException
	 */

	public List<Stanza> parse(ByteBuffer xmlStream)
			throws ParserConfigurationException, IOException,
			IncompleteElementsException {

		String xmlString = new String(xmlStream.array());
		xmlString = xmlString.substring(0, xmlStream.position());
		List<String> messageBodies = new ArrayList<String>();

		if (xmlString.contains("<stream:")) {
			Stanza s = new Stanza();
			s.setXMLString(xmlString);
			List<Stanza> streamList = new LinkedList<Stanza>();
			streamList.add(s);
			return streamList;
		} else {
			extractMessageBodies(xmlString, messageBodies);
			String newString = "<xmpp-proxy>" + xmlString + "</xmpp-proxy>";
			byte[] xmlBytes = newString.getBytes();
			InputStream is = new ByteArrayInputStream(xmlBytes);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			SAXParser parser;
			try {
				parser = factory.newSAXParser();
				XMPPHandler handler = new XMPPHandler();
				parser.parse(is, handler);
				if (handler.hasIncompleteElements()) {
					throw new IncompleteElementsException();
				}
				List<Stanza> lstStanzas = handler.getStanzaList();
				insertMessageBodies(lstStanzas, messageBodies);
				return lstStanzas;
			} catch (SAXException e) {
				throw new IncompleteElementsException();
			}
		}

	}

	/**
	 * Extracts the bodies from all messages in the stream, storing them in a
	 * list to set them later, once the whole stream was parsed and the
	 * structures made.
	 * 
	 * @param xmlString
	 * @param messageBodies
	 * @return
	 */

	private String extractMessageBodies(String xmlString,
			List<String> messageBodies) {
		int position = 0;
		while (xmlString.indexOf("<message", position) > -1) {
			int bodyPosition = xmlString.indexOf("<body", position);
			position = xmlString.indexOf(">", bodyPosition);
			int bodyEndingPosition = xmlString.indexOf("</body>", bodyPosition);
			if (bodyPosition > -1 && bodyEndingPosition > -1) {
				String bodyMessage = xmlString.substring(position + 1,
						bodyEndingPosition);
				xmlString = xmlString.substring(0, position + 1)
						+ xmlString.substring(bodyEndingPosition,
								xmlString.length());
				messageBodies.add(bodyMessage);
			}
		}
		return xmlString;
	}

	/**
	 * Inserts back the message bodies into the marshaled objects.
	 * 
	 * @param lstStanzas
	 * @param messageBodies
	 */

	private void insertMessageBodies(List<Stanza> lstStanzas,
			List<String> messageBodies) {
		Iterator<String> iter = messageBodies.iterator();
		for (Stanza s : lstStanzas) {
			if (s.isMessage()) {
				if (iter.hasNext())
					((Message) s.getElement()).setMessage(iter.next());
			}
		}
	}

}
