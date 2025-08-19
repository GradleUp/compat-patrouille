interface Foo

/**
 * This fails compilation with the error below if not using the correct kotlin-stdlib
 *
 * java.lang.IllegalStateException: Class "kotlin.wasm.internal.KClassInterfaceImpl" not found! Please make sure that your stdlib version is the same as the compiler.
 * 	at org.jetbrains.kotlin.backend.wasm.WasmSymbols.getIrClass(WasmSymbols.kt:446)
 * 	at org.jetbrains.kotlin.backend.wasm.WasmSymbols.getInternalClass(WasmSymbols.kt:450)
 * 	at org.jetbrains.kotlin.backend.wasm.WasmSymbols.access$getInternalClass(WasmSymbols.kt:33)
 */
fun main() {
  println(Foo::class.simpleName)
}
