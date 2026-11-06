// Resúmenes por consola (Semana 2 - Paso 5, Semana 3 - Pasos 6 y 7)

private const val ANCHO = 60

private fun linea(caracter: Char = '-') = caracter.toString().repeat(ANCHO)

private fun fila(izquierda: String, derecha: String): String =
    izquierda.padEnd(ANCHO - derecha.length) + derecha

/** Comprobante de un pedido: cliente, productos, totales y estado. */
fun imprimirComprobante(pedido: Pedido, titulo: String = "RESUMEN DEL PEDIDO #${pedido.id}") {
    val (cliente, productos, _) = pedido    // desestructuración del pedido

    println(linea('='))
    println(titulo.padStart((ANCHO + titulo.length) / 2))
    println(linea('='))
    println("DATOS DEL CLIENTE")
    println("  Nombre:    ${cliente.nombre}")
    println("  Dirección: ${cliente.direccion}")
    println("  Teléfono:  ${cliente.telefono}")
    println("  Correo:    ${cliente.correo}")
    println(linea())
    println("PRODUCTOS")
    productos.forEach { (producto, cantidad) ->
        val marca = if (Reflexion.esPromocionable(producto)) " [promocionable]" else ""
        val detalle = "  $cantidad x ${producto.nombre} (${formatoPesos(producto.precio)} c/u)$marca"
        println(fila(detalle, formatoPesos(producto.precio * cantidad)))
    }
    println(linea())
    println(fila("  Subtotal", formatoPesos(pedido.subtotal)))
    if (pedido.descuentoVolumen > 0) {
        println(fila("  Descuento por volumen (${Pedido.PORCENTAJE_DESCUENTO_VOLUMEN}%)", "-" + formatoPesos(pedido.descuentoVolumen)))
    }
    if (pedido.descuentoPromocion > 0) {
        println(fila("  Descuento ${Promociones.NOMBRE} (${Promociones.PORCENTAJE_DESCUENTO}%)", "-" + formatoPesos(pedido.descuentoPromocion)))
    }
    if (pedido.descuentos == 0) {
        println(fila("  Descuentos aplicados", formatoPesos(0)))
    }
    println(fila("  Neto", formatoPesos(pedido.neto)))
    println(fila("  IVA (${Pedido.PORCENTAJE_IVA}%)", formatoPesos(pedido.impuestos)))
    println(fila("  TOTAL", formatoPesos(pedido.total)))
    println(linea())
    println("  Fecha:      ${pedido.fecha}")
    println("  Estado:     ${pedido.estado.etiqueta}")
    println("  Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
    println("  Notas:      ${pedido.notas ?: "Sin notas"}")
    println(linea('='))
}

/** pedidos combinados con el operador +. */
fun imprimirPedidosCombinados(primero: Pedido, segundo: Pedido, combinado: Pedido) {
    imprimirSubtitulo("1. Pedidos combinados con el operador +")
    println("Pedido #${primero.id}: ${primero.cantidadPizzas} pizza(s) - total ${formatoPesos(primero.total)}")
    println("Pedido #${segundo.id}: ${segundo.cantidadPizzas} pizza(s) - total ${formatoPesos(segundo.total)}")
    println("Suma de totales por separado: ${formatoPesos(primero.total + segundo.total)}")
    println("Pedido combinado (pedido1 + pedido2): ${combinado.cantidadPizzas} pizza(s) - total ${formatoPesos(combinado.total)}")
    println("Ahorro por recalcular descuentos: ${formatoPesos(primero.total + segundo.total - combinado.total)}")
}

/**  cliente desestructurado. */
fun imprimirClienteDesestructurado(cliente: Cliente) {
    imprimirSubtitulo("2. Cliente desestructurado")
    val (nombre, correo, telefono) = cliente
    println("Nombre:   $nombre")
    println("Correo:   $correo")
    println("Teléfono: $telefono")
}

/** productos promocionables, identificados con reflection. */
fun imprimirProductosPromocionables(productos: Collection<Producto>) {
    imprimirSubtitulo("3. Productos promocionables (anotación @Promocionable)")
    productos.forEach { producto ->
        val descripcion = Reflexion.descripcionPromocion(producto)
        if (descripcion != null) {
            println("  [SI] ${producto.nombre} - $descripcion")
        } else {
            println("  [NO] ${producto.nombre} - no califica")
        }
    }
}

/** validación de igualdad. */
fun imprimirIgualdad(descripcion: String, resultado: Boolean) {
    println("  ${descripcion.padEnd(52)} -> $resultado")
}
