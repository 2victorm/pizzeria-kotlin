import java.time.LocalDate

// Validaciones con Regex y Ranges (Semana 3 - Paso 1)

/** Periodo de promoción y su porcentaje de descuento (se usa un Range de fechas). */
object Promociones {
    val PERIODO = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)
    const val PORCENTAJE_DESCUENTO = 15
    const val NOMBRE = "Mes de la Pizza"

    fun estaEnPromocion(fecha: LocalDate): Boolean = fecha in PERIODO
}

object Validaciones {

    /** Rango permitido de unidades por producto (1 a 100). */
    val CANTIDAD_PERMITIDA = 1..100

    /** Código de país que se asume cuando el teléfono se ingresa sin él. */
    const val CODIGO_PAIS_POR_DEFECTO = "56"

    // nombre@dominio.com: parte local, arroba, dominio con al menos un punto y extensión de 2+ letras
    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    // Solo se aceptan dígitos y separadores comunes de un teléfono
    private val REGEX_CARACTERES_TELEFONO = Regex("^\\+?[0-9\\s()-]+$")

    fun esCorreoValido(correo: String): Boolean = REGEX_CORREO.matches(correo)

    /**
     * Normaliza un teléfono al formato uniforme +XX (XXX) XXX-XXXX.
     * Retorna null si el texto no es un teléfono válido.
     */
    fun formatearTelefono(texto: String): String? {
        if (!REGEX_CARACTERES_TELEFONO.matches(texto)) return null
        val digitos = texto.filter { it.isDigit() }
        val completo = when (digitos.length) {
            9, 10 -> CODIGO_PAIS_POR_DEFECTO + digitos
            11, 12 -> digitos
            else -> return null
        }
        val pais = completo.substring(0, 2)
        val area = completo.substring(2, 5)
        val parte1 = completo.substring(5, 8)
        val parte2 = completo.substring(8)      // 3 o 4 dígitos según el largo del número
        return "+$pais ($area) $parte1-$parte2"
    }

    fun cantidadValida(cantidad: Int): Boolean = cantidad in CANTIDAD_PERMITIDA
}
