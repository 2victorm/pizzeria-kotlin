/**
 * Gestión de la pizzería: catálogo, clientes, inventario, repartidores y pedidos.
 *
 * Los clientes y productos se guardan en conjuntos (Set).
 */
class Pizzeria {

    val clientes = mutableSetOf<Cliente>()
    val catalogo = mutableSetOf<Producto>()
    val inventario = mutableMapOf<String, Int>()          // ingrediente -> cantidad disponible
    val repartidores = mutableListOf<Repartidor>()
    val pedidos = mutableListOf<Pedido>()

    private var proximoIdPedido = 1001

    fun siguienteIdPedido(): Int = proximoIdPedido++

    /** Retorna true si se registró o false si ya existía un cliente igual. */
    fun registrarCliente(cliente: Cliente): Boolean = clientes.add(cliente)

    /** Retorna true si se registró o false si ya existía un producto igual. */
    fun registrarProducto(producto: Producto): Boolean = catalogo.add(producto)

    fun agregarStock(ingrediente: String, cantidad: Int) {
        require(cantidad >= 0) { "No se puede registrar una cantidad negativa en el inventario ($ingrediente: $cantidad)." }
        inventario.merge(ingrediente, cantidad) { actual, nuevo -> actual + nuevo }
    }

    /** Total de cada ingrediente que necesita el pedido completo. */
    fun ingredientesRequeridos(pedido: Pedido): Map<String, Int> {
        val requeridos = mutableMapOf<String, Int>()
        pedido.productos.forEach { (producto, cantidad) ->
            producto.ingredientes.forEach { (ingrediente, porUnidad) ->
                requeridos.merge(ingrediente, porUnidad * cantidad) { a, b -> a + b }
            }
        }
        return requeridos
    }

    /** Ingredientes que faltan para preparar el pedido: ingrediente -> unidades que faltan. */
    fun ingredientesFaltantes(pedido: Pedido): Map<String, Int> =
        ingredientesRequeridos(pedido)
            .mapValues { (ingrediente, necesario) -> necesario - (inventario[ingrediente] ?: 0) }
            .filterValues { it > 0 }

    /**
     * Acepta el pedido si hay inventario: descuenta los ingredientes, asigna repartidor y lo registra.
     * Si faltan ingredientes, informa cuáles y cómo reabastecerlos, y el pedido queda cancelado.
     */
    fun aceptarPedido(pedido: Pedido): Boolean {
        val faltantes = ingredientesFaltantes(pedido)
        if (faltantes.isNotEmpty()) {
            pedido.cambiarEstado(EstadoPedido.CANCELADO)
            println("El pedido #${pedido.id} no puede ser aceptado: ingredientes insuficientes.")
            faltantes.forEach { (ingrediente, falta) ->
                println("  - $ingrediente: disponibles ${inventario[ingrediente] ?: 0}, faltan $falta")
            }
            println("  Para reabastecer: 1) solicitar los ingredientes al proveedor, " +
                    "2) registrar el ingreso con agregarStock(), 3) reintentar el pedido.")
            return false
        }

        ingredientesRequeridos(pedido).forEach { (ingrediente, necesario) ->
            inventario[ingrediente] = (inventario[ingrediente] ?: 0) - necesario
        }
        val repartidor = repartidores.minByOrNull { it.pedidosAsignados }
        repartidor?.asignar()
        pedido.repartidor = repartidor
        pedidos.add(pedido)
        return true
    }
}
