/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

/**
 * The function interface which takes one parameter and returns something. When
 * <code>Func1</code> is used in a method parameter declaration, you may
 * consider using wildcards:
 * <p><code>&lt;T, U> U someMethod(Func1&lt;? super T, ? extends U> f);</code></p>
 *
 * @author epaln
 * @param <Param1> the first parameter
 * @param <Return> the return type
 */
public interface Func1<Param1, Return> {

    /**
     * The method that gets invoked with a parameter.
     *
     * @param param1 the parameter value
     * @return the return object
     */
    Return invoke(Param1 param1);
}