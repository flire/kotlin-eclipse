// KT-1968 Double closing parentheses entered when completing unit function
package some

fun test() = 12
fun test1()

val a = <caret>

// ELEMENT: test1