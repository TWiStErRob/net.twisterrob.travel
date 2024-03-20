package com.google.errorprone.annotations;

/**
 * Workaround for <code>class file for com.google.errorprone.annotations.RestrictedInheritance not found</code>.
 *
 * <pre><code>
 * warning: Cannot find annotation method 'explanation()' in type 'RestrictedInheritance'
 * warning: Cannot find annotation method 'link()' in type 'RestrictedInheritance'
 * warning: Cannot find annotation method 'allowedOnPath()' in type 'RestrictedInheritance'
 * warning: Cannot find annotation method 'allowlistAnnotations()' in type 'RestrictedInheritance'
 * </code></pre>
 *
 * @see <a href="https://github.com/google/error-prone/issues/4335">Report</a>
 */
public @interface RestrictedInheritance {
	String explanation();
	String link();
	String allowedOnPath();
	Class<?>[] allowlistAnnotations();
}
