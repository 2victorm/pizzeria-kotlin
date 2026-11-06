// Modelo de productos

enum class Categoria(val etiqueta: String) {
    PIZZA("Pizza"),
    BEBIDA("Bebida"),
    INGREDIENTE("Ingrediente")
}

/**
 * Producto base. Dos productos son iguales si tienen el mismo nombre y la misma categoría.
 *
 * En Kotlin, sobrescribir equals() es sobrecargar el operador ==, porque `a == b` se traduce
 * a `a?.equals(b)`. hashCode() se mantiene consistente con equals() para que HashSet y
 * HashMap (usados en el catálogo y en los pedidos) detecten los duplicados.
 */
open class Producto(
    val nombre: String,
    val categoria: Categoria,
    val precio: Int,
    val ingredientes: Map<String, Int> = emptyMap()   // ingrediente -> cantidad por unidad
) {
    init {
        require(nombre.isNotBlank()) { "El producto debe tener nombre." }
        require(precio >= 0) { "El precio de '$nombre' no puede ser negativo (recibido: $precio)." }
    }

    open fun detallarProducto(): String {
        val detalle = if (ingredientes.isEmpty()) {
            "sin ingredientes registrados"
        } else {
            ingredientes.entries.joinToString(", ") { "${it.key} x${it.value}" }
        }
        return "${categoria.etiqueta}: $nombre - ${formatoPesos(precio)} ($detalle)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) && categoria == other.categoria
    }

    override fun hashCode(): Int = 31 * nombre.trim().lowercase().hashCode() + categoria.hashCode()

    override fun toString(): String = "$nombre (${categoria.etiqueta})"
}

@Promocionable("Pizza elegible para promociones")
class Pizza(
    nombre: String,
    precio: Int,
    ingredientes: Map<String, Int>
) : Producto(nombre, Categoria.PIZZA, precio, ingredientes)

@Promocionable("Combo elegible para promociones")
class Combo(
    nombre: String,
    precio: Int,
    val incluye: List<Producto>
) : Producto(nombre, Categoria.PIZZA, precio, unirIngredientes(incluye)) {

    override fun detallarProducto(): String =
        super.detallarProducto() + " | Incluye: " + incluye.joinToString { it.nombre }
}

/** Las bebidas no están marcadas como @Promocionable. Su ingrediente es la propia unidad en stock. */
class Bebida(
    nombre: String,
    precio: Int
) : Producto(nombre, Categoria.BEBIDA, precio, mapOf(nombre to 1))

private fun unirIngredientes(productos: List<Producto>): Map<String, Int> {
    val resultado = mutableMapOf<String, Int>()
    productos.forEach { producto ->
        producto.ingredientes.forEach { (ingrediente, cantidad) ->
            resultado.merge(ingrediente, cantidad) { a, b -> a + b }
        }
    }
    return resultado
}
