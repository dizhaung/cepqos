/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

/**
 * A parameterless function interface.
 * When <code>Func1</code> is used in a method parameter declaration, you
 * may consider using wildcards: 
 * <p><code>&lt;T> T someMethod(Func0&lt;? extends T> f);</code></p>
 * @author epaln
 * @param <Return> the return type
 */
public interface Func0<Return> {
	/**
	 * The function body to invoke.
	 * @return the return type
	 */
	Return invoke();
}