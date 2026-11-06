// Anotaciones personalizadas del sistema (Semana 3 - Paso 2)


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(val descripcion: String)
