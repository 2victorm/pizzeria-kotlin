import java.time.LocalDate

enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    CANCELADO("Cancelado")
}

/**
 * Pedido de un cliente.
 *
 * - Los productos se guardan en un Map<Producto, Int> (producto -> cantidad).
 *  - Como Producto sobrescribe equals()/hashCode(), agregar dos veces el mismo producto suma la cantidad.
 * - operator plus: combina dos pedidos y recalcula descuentos e impuestos.
 * - Desestructuración: `val (cliente, productos, total) = pedido`.
 */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    val fecha: LocalDate,
    val notas: String? = null
) {
    companion object {
        const val PORCENTAJE_DESCUENTO_VOLUMEN = 10
        const val PIZZAS_PARA_DESCUENTO = 3      // más de 3 pizzas -> descuento
        const val PORCENTAJE_IVA = 19
    }

    private val items = LinkedHashMap<Producto, Int>()

    val productos: Map<Producto, Int> get() = items

    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    var repartidor: Repartidor? = null

    fun agregarProducto(producto: Producto, cantidad: Int) {
        val nuevaCantidad = (items[producto] ?: 0) + cantidad
        require(Validaciones.cantidadValida(nuevaCantidad)) {
            "La cantidad de '${producto.nombre}' debe estar entre ${Validaciones.CANTIDAD_PERMITIDA.first} y " +
                    "${Validaciones.CANTIDAD_PERMITIDA.last} (se intentó registrar $nuevaCantidad)."
        }
        items[producto] = nuevaCantidad
    }

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    // ----- Cálculo de totales -----

    val subtotal: Int
        get() = items.entries.sumOf { (producto, cantidad) -> producto.precio * cantidad }

    val cantidadPizzas: Int
        get() = items.entries.filter { it.key.categoria == Categoria.PIZZA }.sumOf { it.value }

    /** Productos del pedido marcados con @Promocionable (se detectan con reflection). */
    val productosPromocionables: List<Producto>
        get() = items.keys.filter { Reflexion.esPromocionable(it) }

    val descuentoVolumen: Int
        get() = if (cantidadPizzas > PIZZAS_PARA_DESCUENTO) subtotal * PORCENTAJE_DESCUENTO_VOLUMEN / 100 else 0

    /** Descuento de promoción: la fecha debe estar en el rango y haber al menos un producto promocionable. */
    val descuentoPromocion: Int
        get() = if (Promociones.estaEnPromocion(fecha) && productosPromocionables.isNotEmpty()) {
            subtotal * Promociones.PORCENTAJE_DESCUENTO / 100
        } else {
            0
        }

    val descuentos: Int get() = descuentoVolumen + descuentoPromocion

    val neto: Int get() = subtotal - descuentos

    val impuestos: Int get() = (neto * PORCENTAJE_IVA + 50) / 100

    val total: Int get() = neto + impuestos

    // ----- Operadores y desestructuración -----

    /** Combina dos pedidos del mismo cliente en uno nuevo: suma los productos y recalcula el total. */
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "No se pueden combinar pedidos de clientes distintos (${cliente.nombre} y ${otro.cliente.nombre})."
        }
        val fechaCombinada = if (fecha >= otro.fecha) fecha else otro.fecha
        val notasCombinadas = listOfNotNull(notas, otro.notas).joinToString(" | ").ifBlank { null }
        val combinado = Pedido(id, cliente, fechaCombinada, notasCombinadas)
        items.forEach { (producto, cantidad) -> combinado.agregarProducto(producto, cantidad) }
        otro.items.forEach { (producto, cantidad) -> combinado.agregarProducto(producto, cantidad) }
        return combinado
    }

    operator fun component1(): Cliente = cliente
    operator fun component2(): Map<Producto, Int> = productos
    operator fun component3(): Int = total
}
