package net.twisterrob.blt.io.feeds;

import java.lang.annotation.*;

import javax.annotation.RegEx;

import net.twisterrob.java.annotations.SimpleDateFormatString;

/**
 * Tagging interface for XML descriptor interfaces.
 * Implementing this interface and adhering to the convention makes the code more readable and provides uniform look to the code.
 * Also it's a good place for documentation for the individual elements.
 *
 * <p>
 * The format of these interfaces is as follows:
 * <ul>
 *     <li>The root interface, which is the compilation unit describes the whole feed with all the elements as inner classes.<ul>
 *     <li>The Root element's interface has special members:<ul>
 *         <li><code>NS</code>: the namespace declaration (<code>xmlns</code>) of the root node</li>
 *         <li><code>NS$blah</code>: other namespace declarations (<code>xmlns:blah</code>) on the node</li>
 *         <li>All other element descriptors only define {@code NS} if they have one different from Root</li>
 *     </ul></li>
 *     <li>Inner interfaces of the compilation unit are the elements possible in the XML, named <code>"CamelCase"</code>.</li>
 *     <li>Each of them have special members:<ul>
 *         <li><code>ELEMENT</code>: The name of the xml <code>&lt;element&gt;</code>
 *         This is used only as documentation.</li>
 *         <li>Annotation {@link Children @Children}: The possible children classes of the XML element.<br>
 *             If it has no children, the annotation can be omitted.<br>
 *             For content use {@link CONTENT CONTENT.class}.</li>
 *     </ul></li>
 *     <li>A shorthand for contained elements is possible by denoting elements as "{@code CamelCase}".<br>
 *         These can have a {@link Child @Child} annotation meaning it's not an attribute
 *         and {@link Children @Children} to denote what is contained in them.<br>
 *         Most useful when creating a separate descriptor a list is unnecessary.
 *     </li>
 *     <li>The attributes of each inner interface are named <code>"lowerCase"</code>
 *         and tagged with {@link Attribute @Attribute}.</li>
 *     <li>These attributes can have special values or format strings, for example:
 *         {@code attrName$emptyValue} or {@code attrName$format} for which the following annotations are suggested:<ul>
 *         <li>{@link ValueConstraint @ValueConstraint} to sign it's constraintness.</li>
 *         <li>{@link SimpleDateFormatString @SimpleDateFormatString} to signify the constraint has date format string inside.</li>
 *         <li>{@link Value @Value(regex = "\\d+")} to denote a number or anything describable by regex</li>
 *         <li>{@link Value @Value(enumeration = ValueEnum.class)} if there's an {@link Enum} named {@code ValueEnum}.</li>
 *     </ul></li>
 * </ul>
 * An example of the above:
 * <pre><code> &#64;SuppressWarnings("unchecked")
 * public interface InterfaceName {
 *     {@literal @}Children(Child.class)
 *     interface Root {
 *         String NS = "";
 *         String ELEMENT = "RootElementName";
 *     }
 *     interface ChildElement {
 *         String ELEMENT = "ChildElementName";
 *         {@literal @}Attribute String created = "created";
 *         {@literal @}AttributeConstraint {@literal @}SimpleDateFormat String created$format = "yyyy-MM-dd";
 *         {@literal @}Child(CONTENT.class) Name = "NameElement";
 *         {@literal @}Child {@literal @}Children(ChildElement.class) Children = "Children";
 *     }
 * }</code></pre>
 * which denotes the following XML:
 * <pre><code> &lt;RootElementName&gt;
 *     &lt;ChildElementName created="2013-10-20"&gt;
 *         &lt;NameElement&gt;some string&lt;/NameElement&gt;
 *         ... recursive ChildElementNames
 *     &lt;/ChildElementName&gt;
 *     ...
 * &lt;/RootElementName&gt;</code></pre>
 * </p>
 *
 * <p>
 * It is recommended to import the XML descriptor's all inner interfaces, which then can be used like this:
 * <pre><code> RootElement root = new RootElement(Root.NS, Root.ELEMENT);
 * Element child = root.getChild(Root.NS, Child.ELEMENT);
 * child.setStartElementListener(new StartElementListener() {
 *     private final DateFormat CREATED_FORMAT = new SimpleDateFormatString(Child.created$format, Locale.UK);
 *     {@literal @}Override
 *     public void start(Attributes attributes) {
 *         String attrCreated = attributes.getValue(Child.created);
 *         Date created = CREATED_FORMAT.parse(attrCreated);
 *         // ... use created in some way
 *     }
 * });
 * Element name = child.getChild(Root.NS, Child.Name);
 * name.setEndTextElementListener(new EndTextElementListener() {
 *     {@literal @}Override
 *     public void end(String body) {
 *         // ... use body in some way which is the name of the Child
 *     }
 *     ...</code></pre>
 * </p>
 */
@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface FeedXmlDescriptor {
	/**
	 * This is to be used in the <code>CHILDREN</code> array when an element has content.
	 * It can be ignored at use site.
	 */
	interface CONTENT {
		// tag interface
	}

	/**
	 * This is to be used as the <code>CHILDREN</code> array when an element has no children at all.
	 * It can be ignored at use site.
	 */
	@SuppressWarnings("rawtypes") Class[] NO_CHILDREN = {};

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Value {
		@RegEx
		String regex() default ".*";
		@SuppressWarnings("rawtypes")
		Class<? extends Enum> enumeration() default Enum.class;
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface ValueConstraint {
		// no parameters
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.FIELD})
	public @interface Children {
		Class<?>[] value() default {};
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Child {
		Class<?>[] value() default {};
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	public @interface Attribute {
		// no parameters
	}
}
