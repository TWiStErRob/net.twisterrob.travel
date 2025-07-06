package groovy.util;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import groovy.util.slurpersupport.GPathResult;

/**
 * Polyfill for the removed {@code groovy.util.XmlSlurper} class for Gretty Gradle Plugin.
 * 
 * @link <a href="https://github.com/gretty-gradle-plugin/gretty/issues/320">Gretty</a>
 */
public class XmlSlurper {
	private final groovy.xml.XmlSlurper delegate;

	public XmlSlurper() throws ParserConfigurationException, SAXException {
		delegate = new groovy.xml.XmlSlurper();
	}

	public GPathResult parse(File file) throws IOException, SAXException {
		return new GPathResult(delegate.parse(file));
	}
}
