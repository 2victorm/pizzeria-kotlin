// Usuarios del sistema (Semana 2 - Herencia, Semana 3 - Pasos 4 y 5)

open class Usuario(
    val nombre: String,
    val contacto: String
)

/**
 * Cliente de la pizzería.
 *
 * - Desestructuración: `val (nombre, correo, telefono) = cliente` gracias a los operator component1..3.
 * - Igualdad: dos clientes son iguales si coinciden en nombre y correo.
 */
class Cliente(
    nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String = "Sin dirección"
) : Usuario(nombre, "$telefono / $correo") {

    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
                correo.trim().equals(other.correo.trim(), ignoreCase = true)
    }

    override fun hashCode(): Int = 31 * nombre.trim().lowercase().hashCode() + correo.trim().lowercase().hashCode()

    override fun toString(): String = "Cliente(nombre=$nombre, correo=$correo)"
}

class Repartidor(
    nombre: String,
    telefono: String,
    val zona: String
) : Usuario(nombre, telefono) {

    var pedidosAsignados: Int = 0
        private set

    fun asignar() {
        pedidosAsignados++
    }

    /** Marca el pedido como "En camino" y retorna el mensaje de la entrega. */
    fun entregarPedido(pedido: Pedido): String {
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        return "$nombre sale a entregar el pedido #${pedido.id} a ${pedido.cliente.direccion}."
    }
}
