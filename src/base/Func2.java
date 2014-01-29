/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

/**
 * The function interface which takes two parameter and returns something. When
 * <code>Func1</code> is used in a method parameter declaration, you may
 * consider using wildcards:
 * <p><code>&lt;T, U, V> V someMethod(Func2&lt;? super T, ? super U, ? extends V> f);</code></p>
 *
 * @author epaln
 * @param <Return> the return type
 * @param <Param1> the first parameter
 * @param <Param2> the second parameter
 */
public interface Func2<Param1, Param2, Return> {

    /**
     * The method that gets invoked with two parameters.
     *
     * @param param1 the first parameter value
     * @param param2 the second parameter value
     * @return the return object
     */
    Return invoke(Param1 param1, Param2 param2);
}