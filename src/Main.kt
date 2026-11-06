import java.time.LocalDate

// Pizzería - Desarrollo App Móviles I
// Semana 3

fun main() {
    val pizzeria = Pizzeria()
    cargarDatosIniciales(pizzeria)

    imprimirTitulo("Pizzería - Sistema de pedidos")
    println("Periodo de promoción vigente: ${Promociones.NOMBRE} " +
            "(${Promociones.PERIODO.start} al ${Promociones.PERIODO.endInclusive}), " +
            "${Promociones.PORCENTAJE_DESCUENTO}% de descuento en pedidos con productos promocionables.")

    val pedidoInteractivo = registrarPedidoInteractivo(pizzeria)
    ejecutarDemostracion(pizzeria, pedidoInteractivo)
}

// ---------------------------------------------------------------------------------------------
// Datos iniciales: catálogo, inventario y repartidores
// ---------------------------------------------------------------------------------------------

fun cargarDatosIniciales(pizzeria: Pizzeria) {
    val pepperoni = Pizza("Pepperoni", 8990, mapOf("masa" to 1, "salsa" to 1, "queso" to 2, "pepperoni" to 3))
    val hawaiana = Pizza("Hawaiana", 9490, mapOf("masa" to 1, "salsa" to 1, "queso" to 2, "jamón" to 2, "piña" to 2))
    val cola = Bebida("Bebida Cola 1.5L", 2500)

    pizzeria.registrarProducto(pepperoni)
    pizzeria.registrarProducto(hawaiana)
    pizzeria.registrarProducto(Combo("Combo Familiar", 21990, listOf(pepperoni, hawaiana, cola)))
    pizzeria.registrarProducto(cola)
    pizzeria.registrarProducto(Producto("Queso extra", Categoria.INGREDIENTE, 1500, mapOf("queso" to 1)))

    mapOf(
        "masa" to 60, "salsa" to 60, "queso" to 150, "pepperoni" to 60,
        "jamón" to 40, "piña" to 30, "Bebida Cola 1.5L" to 20
    ).forEach { (ingrediente, cantidad) -> pizzeria.agregarStock(ingrediente, cantidad) }

    pizzeria.repartidores += Repartidor("Camila Rojas", "+56 (911) 111-1111", "Centro")
    pizzeria.repartidores += Repartidor("Diego Soto", "+56 (922) 222-2222", "Norte")
    pizzeria.repartidores += Repartidor("Felipe Mora", "+56 (933) 333-3333", "Sur")
}

// ---------------------------------------------------------------------------------------------
// Parte 1: registro interactivo con validaciones (Regex y Ranges)
// ---------------------------------------------------------------------------------------------

/**
 * Pide un dato por consola hasta que `convertir` lo acepte.
 * Si la entrada termina retorna null para no quedar en un ciclo infinito.
 */
fun <T : Any> pedirDato(mensaje: String, mensajeError: String, convertir: (String) -> T?): T? {
    while (true) {
        println(mensaje)
        val entrada = readLine() ?: return null
        val valor = convertir(entrada.trim())
        if (valor != null) return valor
        println("  [ERROR] $mensajeError")
    }
}

fun registrarPedidoInteractivo(pizzeria: Pizzeria): Pedido? {
    imprimirSubtitulo("Registro de cliente")

    val nombre = pedirDato("Nombre del cliente:", "El nombre no puede estar vacío.") {
        it.ifBlank { null }
    } ?: return entradaTerminada()

    val direccion = pedirDato("Dirección de entrega:", "La dirección no puede estar vacía.") {
        it.ifBlank { null }
    } ?: return entradaTerminada()

    val correo = pedirDato(
        "Correo electrónico (formato nombre@dominio.com):",
        "Formato de correo inválido. Ejemplo válido: nombre@dominio.com"
    ) { it.takeIf { texto -> Validaciones.esCorreoValido(texto) } } ?: return entradaTerminada()

    val telefono = pedirDato(
        "Teléfono (9 dígitos, o con código de país; ejemplo: 912345678 o +56 912345678):",
        "Teléfono inválido. Ingresa 9 dígitos (ejemplo: 912345678), con o sin el código de país +56."
    ) { Validaciones.formatearTelefono(it) } ?: return entradaTerminada()

    val nuevoCliente = Cliente(nombre, correo, telefono, direccion)
    val cliente = if (pizzeria.registrarCliente(nuevoCliente)) {
        println("Cliente registrado: ${nuevoCliente.nombre} - ${nuevoCliente.telefono}")
        nuevoCliente
    } else {
        println("El cliente ya estaba registrado, se reutiliza el registro existente.")
        pizzeria.clientes.first { it == nuevoCliente }
    }

    imprimirSubtitulo("Nuevo pedido")
    val pizzas = pizzeria.catalogo.filter { it.categoria == Categoria.PIZZA }
    pizzas.forEachIndexed { indice, producto -> println("  ${indice + 1}) ${producto.detallarProducto()}") }

    val elegido = pedirDato("Elige un producto (1-${pizzas.size}):", "Opción inválida.") {
        it.toIntOrNull()?.let { numero -> pizzas.getOrNull(numero - 1) }
    } ?: return entradaTerminada()

    val cantidad = pedirDato(
        "¿Cuántas unidades de ${elegido.nombre}? (entre ${Validaciones.CANTIDAD_PERMITIDA.first} y ${Validaciones.CANTIDAD_PERMITIDA.last}):",
        "Cantidad fuera de rango. Debe ser un número entre ${Validaciones.CANTIDAD_PERMITIDA.first} y ${Validaciones.CANTIDAD_PERMITIDA.last}."
    ) { it.toIntOrNull()?.takeIf { numero -> Validaciones.cantidadValida(numero) } } ?: return entradaTerminada()

    println("Notas del pedido (opcional, Enter para omitir):")
    val notas = readLine()?.trim()?.ifBlank { null }

    val pedido = Pedido(pizzeria.siguienteIdPedido(), cliente, LocalDate.now(), notas)
    pedido.agregarProducto(elegido, cantidad)

    if (pizzeria.aceptarPedido(pedido)) {
        println()
        imprimirComprobante(pedido)
        pedido.repartidor?.let { println("Notificación: ${it.nombre} fue asignado al pedido #${pedido.id}.") }
    }
    return pedido
}

fun entradaTerminada(): Pedido? {
    println("No hay más datos de entrada. Se omite el registro interactivo y se continúa con la demostración.")
    return null
}

// ---------------------------------------------------------------------------------------------
// Parte 2: demostración de las funcionalidades
// ---------------------------------------------------------------------------------------------

fun ejecutarDemostracion(pizzeria: Pizzeria, pedidoInteractivo: Pedido?) {
    imprimirTitulo("Demostración de funcionalidades")

    val pepperoni = pizzeria.catalogo.first { it.nombre == "Pepperoni" }
    val hawaiana = pizzeria.catalogo.first { it.nombre == "Hawaiana" }
    val cola = pizzeria.catalogo.first { it.nombre == "Bebida Cola 1.5L" }

    // --- Paso 5: evitar duplicados con equals() y hashCode() ---
    imprimirSubtitulo("Registro sin duplicados")
    val ana = Cliente("Ana Pérez", "ana.perez@mail.com", Validaciones.formatearTelefono("+56 912 345 6789") ?: "Sin teléfono", "Av. Providencia 1234")
    val bruno = Cliente("Bruno Soto", "bruno.soto@correo.cl", Validaciones.formatearTelefono("9876543210") ?: "Sin teléfono", "Los Aromos 456")
    println("Registrar a Ana Pérez:            ${if (pizzeria.registrarCliente(ana)) "registrada" else "duplicado rechazado"}")
    println("Registrar a Bruno Soto:           ${if (pizzeria.registrarCliente(bruno)) "registrado" else "duplicado rechazado"}")
    val anaDuplicada = Cliente("ANA PÉREZ", "Ana.Perez@MAIL.com", "+56 (900) 000-0000")
    println("Registrar 'ANA PÉREZ' (mismo correo): ${if (pizzeria.registrarCliente(anaDuplicada)) "registrada" else "duplicado rechazado"}")
    println("Registrar otra 'Pepperoni' (pizza):   ${if (pizzeria.registrarProducto(Pizza("pepperoni", 9990, emptyMap()))) "registrado" else "duplicado rechazado"}")
    println("Clientes registrados: ${pizzeria.clientes.size} | Productos en catálogo: ${pizzeria.catalogo.size}")

    // --- Pedidos dentro del periodo de promoción ---
    imprimirSubtitulo("Pedidos de Ana Pérez")
    val pedido1 = Pedido(pizzeria.siguienteIdPedido(), ana, LocalDate.of(2026, 11, 10), "Sin cebolla")
    pedido1.agregarProducto(pepperoni, 2)
    pedido1.agregarProducto(cola, 1)

    val pedido2 = Pedido(pizzeria.siguienteIdPedido(), ana, LocalDate.of(2026, 11, 12))
    pedido2.agregarProducto(hawaiana, 2)

    println("Pedido #${pedido1.id} aceptado: ${pizzeria.aceptarPedido(pedido1)}")
    println("Pedido #${pedido2.id} aceptado: ${pizzeria.aceptarPedido(pedido2)}")
    println()
    imprimirComprobante(pedido1)

    // --- Paso 3: operador + para combinar pedidos ---
    val combinado = pedido1 + pedido2
    println()
    imprimirComprobante(combinado, "PEDIDO COMBINADO (#${pedido1.id} + #${pedido2.id})")

    // --- Errores controlados con try-catch ---
    imprimirSubtitulo("Manejo de errores")
    val pedidoBruno = Pedido(pizzeria.siguienteIdPedido(), bruno, LocalDate.of(2026, 12, 5))
    pedidoBruno.agregarProducto(pepperoni, 1)
    try {
        val invalido = pedido1 + pedidoBruno
        println("Pedido combinado: ${invalido.id}")
    } catch (e: IllegalArgumentException) {
        println("Combinar pedidos de clientes distintos -> ${e.message}")
    }
    try {
        pedidoBruno.agregarProducto(pepperoni, 100)   // 1 + 100 = 101 unidades
    } catch (e: IllegalArgumentException) {
        println("Cantidad fuera de rango (1-100)       -> ${e.message}")
    }
    try {
        pizzeria.agregarStock("masa", -5)
    } catch (e: IllegalArgumentException) {
        println("Stock negativo                        -> ${e.message}")
    }

    // --- Pedido fuera del periodo de promoción (sin descuento) y con inventario insuficiente ---
    imprimirSubtitulo("Pedido fuera de promoción (05-12-2026)")
    println("Pedido #${pedidoBruno.id} aceptado: ${pizzeria.aceptarPedido(pedidoBruno)}")
    println("Descuento de promoción: ${formatoPesos(pedidoBruno.descuentoPromocion)} | Total: ${formatoPesos(pedidoBruno.total)}")

    imprimirSubtitulo("Pedido con inventario insuficiente")
    val pedidoGrande = Pedido(pizzeria.siguienteIdPedido(), bruno, LocalDate.of(2026, 11, 15))
    pedidoGrande.agregarProducto(pepperoni, 50)
    pizzeria.aceptarPedido(pedidoGrande)

    // --- Repartidor ---
    imprimirSubtitulo("Entrega")
    pedido1.repartidor?.let { println(it.entregarPedido(pedido1)) }

    // --- Paso 2: reflection ---
    imprimirSubtitulo("Análisis dinámico con reflection")
    listOf(Cliente::class, Producto::class, Pedido::class).forEach { clase ->
        Reflexion.describirClase(clase).forEach { println(it) }
        println()
    }

    // ---------------------------------------------------------------------------------------------
    // Paso 6: resumen final
    // ---------------------------------------------------------------------------------------------
    imprimirTitulo("Resumen final")
    imprimirPedidosCombinados(pedido1, pedido2, combinado)

    // --- Paso 4: desestructuración ---
    imprimirClienteDesestructurado(ana)
    val (clientePedido, productosPedido, totalPedido) = combinado
    println("Pedido combinado -> cliente: ${clientePedido.nombre}, " +
            "productos: ${productosPedido.entries.joinToString { "${it.value} x ${it.key.nombre}" }}, " +
            "total: ${formatoPesos(totalPedido)}")

    imprimirProductosPromocionables(pizzeria.catalogo)

    imprimirSubtitulo("4. Validación de igualdad de objetos")
    val otraAna = Cliente("ana pérez", "ANA.PEREZ@mail.com", "+56 (900) 000-0000")
    imprimirIgualdad("Cliente 'Ana Pérez' == 'ana pérez' (mismo correo)", ana == otraAna)
    imprimirIgualdad("Cliente 'Ana Pérez' == 'Bruno Soto'", ana == bruno)
    imprimirIgualdad("Cliente 'Ana Pérez' === 'ana pérez' (misma instancia)", ana === otraAna)
    imprimirIgualdad("Cliente 'Ana Pérez' hashCode igual a 'ana pérez'", ana.hashCode() == otraAna.hashCode())
    imprimirIgualdad("Producto 'Pepperoni' (pizza) == 'pepperoni' (pizza)", pepperoni == Pizza("pepperoni", 1, emptyMap()))
    imprimirIgualdad("Producto 'Pepperoni' (pizza) == 'Pepperoni' (bebida)", pepperoni == Producto("Pepperoni", Categoria.BEBIDA, 1))
    imprimirIgualdad("Producto 'Pepperoni' === otra instancia", pepperoni === Pizza("Pepperoni", 8990, emptyMap()))

    imprimirSubtitulo("Estado de la pizzería")
    println("Pedidos aceptados: ${pizzeria.pedidos.size} (pedido interactivo: ${if (pedidoInteractivo != null) "#${pedidoInteractivo.id}" else "no registrado"})")
    println("Inventario restante: ${pizzeria.inventario.entries.joinToString { "${it.key}=${it.value}" }}")
    println()
    println("Proceso finalizado correctamente.")
}
