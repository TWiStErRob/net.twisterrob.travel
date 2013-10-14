package net.twisterrob.blt.io.feeds;

/**
 * Tagging interface for XML descriptor interfaces.
 * Implementing this interface and adhering to the convention makes the code more readable and provides uniform look to the code.
 * Also it's a good place for documentation for the individual elements.
 * 
 * <p>
 * The format of these interfaces is as follows:
 * <ul>
 *     <li>The root interface, which is the compilation unit describes the whole feed, it has special members:<ul>
 *         <li><code>NS</code>: the namespace declaration (<code>xmlns</code>) of the root node</li>
 *         <li><code>NS$blah</code>: other namespace declarations (<code>xmlns:blah</code>) on the node</li>
 *     </ul></li>
 *     <li>Inner interfaces of the compilation unit are the elements possible in the XML, named <code>"CamelCase"</code>.<br>
 *         The extend the root interface and can override the namespaces.</li>
 *     <li>They each have special members:<ul>
 *         <li><code>CHILDREN</code>: The possible children classes of the XML element, including {@link #CONTENT} for XML content.<br>
 *             (or use {@link #NO_CHILDREN} if it doesn't have children).</li>
 *         <li><code>ELEMENT</code>: The name of the xml <code>&lt;element&gt;</code>
 *         This is used only as documentation.</li>
 *     </ul></li>
 *     <li>The attributes of each inner interface are named <code>"lowerCase"</code>.</li>
 *     <li>These attributes can have special values or format strings, for example:
 *         <code>attrName$emptyValue</code> or <code>attrName$format</code></li>
 * </ul>
 * An example of the above:
 * <pre><code> &#64;SuppressWarnings("unchecked")
 * public interface InterfaceName {
 *     String NS = null;
 *     interface Root extends InterfaceName {
 *         Class<? extends InterfaceName>[] CHILDREN = new Class[]{Child.class};
 *         String ELEMENT = "RootElementName";
 *     }
 *     interface Child extends InterfaceName {
 *         Class<? extends InterfaceName>[] CHILDREN = NO_CHILDREN;
 *         String ELEMENT = "ChildElementName";
 *         String created = "created";
 *         String created$format = "yyyy-MM-dd";
 *     }
 * }
</code></pre>
 * </p>
 * 
 * <p>
 * It is recommended to import the XML descriptor in the class where it is used with:
 * <pre><code>private interface X extends InterfaceName {} // Shorthand for the XML interface</code></pre>
 * and then it can be used like this:
 * <pre><code> RootElement root = new RootElement(X.Root.ELEMENT);
 * Element timeElement = root.getChild(X.Child.ELEMENT);
 * timeElement.setStartElementListener(new StartElementListener() {
 *     private final DateFormat CREATED_FORMAT = new SimpleDateFormat(X.Child.created$format, Locale.UK);
 *     &#64;Override
 *     public void start(Attributes attributes) {
 *         String attrCreated = attributes.getValue(X.Child.created);
 *         Date created = CREATED_FORMAT.parse(attrCreated);
 *         ...</code></pre>
 *	
 * </p>
 * @author TWiStEr
 */
public interface FeedXmlDescriptor {
	/**
	 * This is to be used in the <code>CHILDREN</code> array when an element has content.
	 * It can be ignored at use site.
	 * @author TWiStEr
	 */
	interface CONTENT {
		// tag interface
	}

	/**
	 * This is to be used as the <code>CHILDREN</code> array when an element has no children at all.
	 * It can be ignored at use site.
	 * @author TWiStEr
	 */
	@SuppressWarnings("rawtypes")
	Class[] NO_CHILDREN = {};
}
