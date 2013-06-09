package ar.edu.itba.pdc.stanzas;

import ar.edu.itba.pdc.jabber.JIDConfiguration;
import ar.edu.itba.pdc.jabber.JabberElement;
import ar.edu.itba.pdc.jabber.Message;
import ar.edu.itba.pdc.jabber.Presence;

public class Stanza {

	private StanzaType type;
	private boolean completed;
	private JabberElement element;
	
	public Stanza() {
		this.completed = false;
	}
	
	public Stanza(StanzaType type) {
		this();
		this.type = type;
	}
	
	public Stanza(StanzaType type, JabberElement element) {
		this(type);
		this.element = element;
	}
	
	public String getType() {
		return type.toString();
	}
	
	public void complete() {
		this.completed = true;
	}
	
	public void setType(StanzaType type) {
		this.type = type;
	}
	
	public boolean isComplete() {
		return completed;
	}
	
	public boolean isMessage() {
		return element != null && element.getClass() == Message.class;
	}
	
	public boolean isPresence() {
		return element != null && element.getClass() == Presence.class;
	}
	
	public boolean isJIDConfiguration() {
		return element != null && element.getClass() == JIDConfiguration.class;
	}
	
	public JabberElement getElement() {
		return element;
	}
	
	@Override
	public String toString() {
		if (element != null)
			return element.toString();
		return "Default Stanza";
	}
	
	public void setElement(JabberElement element) {
		this.element = element;
	}
	
}
