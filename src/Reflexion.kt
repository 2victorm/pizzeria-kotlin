import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

/**
 * Funciones basadas en reflection.
 * Requiere la librería kotlin-reflect.
 */
object Reflexion {

    /** Lee las anotaciones de la clase del producto, en tiempo de ejecución, y busca @Promocionable. */
    fun esPromocionable(producto: Producto): Boolean =
        producto::class.annotations.any { it is Promocionable }

    fun descripcionPromocion(producto: Producto): String? =
        producto::class.annotations.filterIsInstance<Promocionable>().firstOrNull()?.descripcion

    /** Retorna las líneas con las propiedades y las funciones que tiene la clase. */
    fun describirClase(clase: KClass<*>): List<String> {
        val lineas = mutableListOf<String>()
        lineas += "Clase ${clase.simpleName}"

        val propiedades = clase.members.filterIsInstance<KProperty<*>>().sortedBy { it.name }
        lineas += "  Propiedades (${propiedades.size}):"
        propiedades.forEach { lineas += "    - ${it.name}" }

        val funciones = clase.members.filterIsInstance<KFunction<*>>().sortedBy { it.name }
        lineas += "  Métodos (${funciones.size}):"
        funciones.forEach { lineas += "    - ${it.name}" }

        return lineas
    }
}
