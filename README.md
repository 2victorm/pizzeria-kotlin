# Pizzería - Creando soluciones con Kotlin (Semana 3)

Desarrollo App Móviles I (PMY2201) - Actividad sumativa individual.
Alumno: Victor Marambio

## Cómo ejecutar
1. Abrir la carpeta `Semana3` en IntelliJ IDEA (File > Open).
2. Ejecutar `src/Main.kt` (botón Run junto a `fun main()`).
3. El programa pide por consola los datos de un cliente y de un pedido (con validaciones y reintentos)
   y luego ejecuta una demostración automática con todas las funcionalidades.

La reflection de Kotlin (`::class`, `members`, `KProperty`) necesita la librería `kotlin-reflect`, que ya viene
en la carpeta `lib/` y está configurada en el proyecto. Si IntelliJ no la reconoce (error `KotlinReflectionNotSupportedError`
o `Unresolved reference: reflect`), agrégala manualmente:
File > Project Structure > Modules > Dependencies > `+` > Library > From Maven >
`org.jetbrains.kotlin:kotlin-reflect` (misma versión que tu Kotlin), o JARs or Directories > `lib/kotlin-reflect.jar`.

## Archivos
| Archivo | Contenido |
|---|---|
| `Main.kt` | Registro interactivo, demostración y resumen final |
| `Validaciones.kt` | Regex (correo, teléfono), Ranges (cantidad 1..100, periodo de promoción) |
| `Anotaciones.kt` | Anotación `@Promocionable` |
| `Producto.kt` | `Producto`, `Pizza`, `Combo`, `Bebida`; `equals()`/`hashCode()` y `==` |
| `Usuario.kt` | `Usuario`, `Cliente` (desestructuración, `equals`/`hashCode`), `Repartidor` |
| `Pedido.kt` | `Pedido` con operador `+`, desestructuración, descuentos e IVA |
| `Reflexion.kt` | Lectura de `@Promocionable` (`::class.annotations`) y listado de propiedades/métodos (`::class.members`) |
| `Pizzeria.kt` | Catálogo, clientes (sin duplicados), inventario, repartidores y pedidos |
| `Resumen.kt`, `Formato.kt` | Salida por consola |

## Requerimientos de la actividad
| Paso | Dónde está |
|---|---|
| 1. Regex: correo con reintento | `Validaciones.esCorreoValido`, `pedirDato` en `Main.kt` |
| 1. Regex: teléfono `+XX (XXX) XXX-XXXX` | `Validaciones.formatearTelefono` |
| 1. Ranges: periodo de promoción | `Promociones.PERIODO` (`LocalDate..LocalDate`), `Pedido.descuentoPromocion` |
| 1. Ranges: cantidad 1..100 | `Validaciones.CANTIDAD_PERMITIDA`, `Validaciones.cantidadValida` |
| 2. Anotación `@Promocionable` | `Anotaciones.kt`, aplicada a `Pizza` y `Combo` |
| 2. Detección dinámica de promocionables | `Reflexion.esPromocionable`, `Pedido.productosPromocionables` |
| 2. Reflection de métodos y propiedades | `Reflexion.describirClase`: `members.filterIsInstance<KProperty<*>>()` y `<KFunction<*>>` (Cliente, Producto, Pedido) |
| 3. Operador `+` en `Pedido` | `Pedido.plus` |
| 3. Operador `==` en `Producto` | `Producto.equals` (nombre y categoría) |
| 4. Desestructuración de `Cliente` y `Pedido` | `component1..3` en ambas clases |
| 5. `equals()`/`hashCode()` | `Cliente` (nombre y correo), `Producto` (nombre y categoría) |
| 5. Evitar duplicados | `Pizzeria.registrarCliente` / `registrarProducto` (usan `Set`) |
| 6-7. Resumen en consola | `ejecutarDemostracion` en `Main.kt`, `Resumen.kt` |

## Decisiones de diseño
- **Teléfono:** el formato pedido tiene 2 + 10 dígitos. Se aceptan 12 dígitos (con código de país) o 10 dígitos
  (se asume el código `56`). Se permiten espacios, guiones, paréntesis y `+` como separadores.
- **Promoción:** aplica el 15% sobre el subtotal si la fecha del pedido está dentro del periodo
  (01-11-2026 al 30-11-2026) y el pedido incluye al menos un producto marcado `@Promocionable`.
  El periodo se cambia en `Promociones.PERIODO`.
- **Descuento por volumen (semana 2):** 10% si el pedido tiene más de 3 pizzas. IVA: 19% sobre el neto.
- **Combinar pedidos (`+`):** solo se permite entre pedidos del mismo cliente; de lo contrario lanza un
  `IllegalArgumentException` con un mensaje claro.
