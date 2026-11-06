import java.util.Locale

// Utilidades de formato para la salida por consola

/** Formatea un monto en pesos con separador de miles */
fun formatoPesos(monto: Int): String = "$" + String.format(Locale.forLanguageTag("es-CL"), "%,d", monto)

fun imprimirTitulo(titulo: String) {
    println()
    println("=".repeat(60))
    println(titulo.uppercase())
    println("=".repeat(60))
}

fun imprimirSubtitulo(subtitulo: String) {
    println()
    println("--- $subtitulo ---")
}
