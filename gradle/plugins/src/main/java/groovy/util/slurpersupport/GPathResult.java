package groovy.util.slurpersupport;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;

/**
 * Polyfill for the removed {@code groovy.util.slurpersupport.GPathResult} class for Gretty Gradle Plugin.
 *
 * @link <a href="https://github.com/gretty-gradle-plugin/gretty/issues/320">Gretty</a>
 */
public class GPathResult extends GroovyObjectSupport {
	private final groovy.xml.slurpersupport.GPathResult delegate;

	public GPathResult(groovy.xml.slurpersupport.GPathResult delegate) {
		this.delegate = delegate;
	}

	@Override
	public MetaClass getMetaClass() {
		return delegate.getMetaClass();
	}

	@Override
	public void setMetaClass(MetaClass metaClass) {
		delegate.setMetaClass(metaClass);
	}

	@Override
	public Object invokeMethod(String name, Object args) {
		return delegate.invokeMethod(name, args);
	}

	@Override
	public Object getProperty(String propertyName) {
		return delegate.getProperty(propertyName);
	}

	@Override
	public void setProperty(String propertyName, Object newValue) {
		delegate.setProperty(propertyName, newValue);
	}
}
