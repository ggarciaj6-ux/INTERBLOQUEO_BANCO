class AlgoritmoBanquero(// Número de procesos y tipos de recursos
    private val numProcesos: Int, private val numRecursos: Int, // Matriz de demanda máxima
    private val maximo: Array<IntArray?>, // Matriz de recursos asignados (En uso)
    private val asignacion: Array<IntArray?>, disp: IntArray
) {
    private val necesidad: Array<IntArray?> // Matriz de necesidad (Maximo - Asignacion)
    private val disponibles: IntArray // Vector de recursos disponibles

    init {
        this.disponibles = disp.copyOf(disp.size)
        this.necesidad = Array<IntArray?>(numProcesos) { IntArray(numRecursos) }

        calcularNecesidad()
    }

    // Calcula: Necesidad = Máximo - Asignación
    private fun calcularNecesidad() {
        for (i in 0..<numProcesos) {
            for (j in 0..<numRecursos) {
                necesidad[i]!![j] = maximo[i]!![j] - asignacion[i]!![j]
            }
        }
    }

    // Algoritmo de seguridad del Banquero
    fun verificarEstadoSeguro(): Boolean {
        val finalizado = BooleanArray(numProcesos)
        val trabajo = disponibles.copyOf(numRecursos)
        val secuenciaSegura = IntArray(numProcesos)
        var contador = 0

        println("\n--- INICIO DE VERIFICACIÓN DE SEGURIDAD ---")
        println("Disponibles iniciales: " + trabajo.contentToString())

        while (contador < numProcesos) {
            var encontrado = false

            for (p in 0..<numProcesos) {
                if (!finalizado[p]) {
                    // Verificar si Necesidad <= Trabajo
                    var puedeEjecutar = true
                    for (r in 0..<numRecursos) {
                        if (necesidad[p]!![r] > trabajo[r]) {
                            puedeEjecutar = false
                            break
                        }
                    }

                    // Si los recursos son suficientes, el proceso termina y libera los asignados
                    if (puedeEjecutar) {
                        for (r in 0..<numRecursos) {
                            trabajo[r] += asignacion[p]!![r]
                        }
                        secuenciaSegura[contador++] = p
                        finalizado[p] = true
                        encontrado = true
                        println("-> Proceso P" + (p + 1) + " puede terminar. Nuevos disponibles: " + trabajo.contentToString())
                    }
                }
            }

            // Si dimos una vuelta completa y ningún proceso pudo ejecutarse
            if (!encontrado) {
                println("\n[ALERTA] Ningún proceso puede continuar con los recursos disponibles.")
                println("ESTADO: INSEGURO (Existe riesgo de Deadlock).")
                return false
            }
        }

        println("\nESTADO: SEGURO")
        print("Secuencia segura encontrada: ")
        for (i in 0..<numProcesos) {
            print("P" + (secuenciaSegura[i] + 1) + (if (i < numProcesos - 1) " -> " else ""))
        }
        println()
        return true
    }

    fun mostrarMatrices() {
        println("==================================================")
        println("Proceso |   Máximo   |   En Uso   |  Necesidad ")
        println("==================================================")
        for (i in 0..<numProcesos) {
            System.out.printf(
                "  P%-5d| %s | %s | %s\n",
                (i + 1),
                maximo[i].contentToString(),
                asignacion[i].contentToString(),
                necesidad[i].contentToString()
            )
        }
        println("==================================================")
        println("Recursos Disponibles: " + disponibles.contentToString())
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Datos del ejercicio de la imagen:
            // 4 Procesos (P1 a P4) y 3 tipos de Recursos (R1, R2, R3)
            val procesos = 4
            val recursos = 3

            val maximo = arrayOf<IntArray?>(
                intArrayOf(3, 2, 2),  // P1
                intArrayOf(6, 1, 3),  // P2
                intArrayOf(3, 1, 4),  // P3
                intArrayOf(4, 2, 2) // P4
            )

            val asignacion = arrayOf<IntArray?>(
                intArrayOf(2, 0, 1),  // P1
                intArrayOf(5, 1, 1),  // P2
                intArrayOf(2, 1, 1),  // P3
                intArrayOf(0, 0, 2) // P4
            )

            // Recursos disponibles calculados: [0, 1, 1]
            val disponibles = intArrayOf(0, 1, 1)

            val banquero = AlgoritmoBanquero(procesos, recursos, maximo, asignacion, disponibles)
            banquero.mostrarMatrices()
            banquero.verificarEstadoSeguro()
        }
    }
}