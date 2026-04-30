# 🍣 SUSHI TEGRIDAD — Sistema de Gestión

Sistema de administración para restaurante de sushi desarrollado en **Java OOP**.
Migrado desde C++ con estructura completamente orientada a objetos.

---

## Estructura del proyecto

```
SushiTegridad/
├── src/
│   ├── Main.java              ← Punto de entrada: login + menú principal
│   ├── Util.java              ← Helpers de consola (limpiar, pausa, leer)
│   ├── Ingrediente.java       ← Entidad: ingrediente del almacén
│   ├── Platillo.java          ← Entidad: platillo con receta (Map)
│   ├── Venta.java             ← Entidad: registro de venta
│   ├── Empleado.java          ← Entidad: empleado con cálculo de sueldo
│   ├── Almacen.java           ← Módulo: CRUD de ingredientes
│   ├── ModuloVentas.java      ← Módulo: ventas + gestión de carta
│   ├── RecursosHumanos.java   ← Módulo: CRUD de empleados
│   └── GestorArchivos.java    ← Persistencia: guardar/cargar datos_sushi.txt
└── out/                       ← Clases compiladas (.class)
```

---

## Cómo compilar y ejecutar

```bash
# Compilar (desde la raíz del proyecto)
javac -d out src/*.java

# Ejecutar
java -cp out Main
```

---

## Credenciales por defecto

| Campo     | Valor  |
|-----------|--------|
| Usuario   | admin  |
| Contraseña| 1234   |

---

## Módulos del sistema

### 🥡 Almacén
- Ver lista de ingredientes
- Agregar ingredientes
- Modificar cantidad
- Buscar por nombre
- Eliminar ingrediente

### 🍱 Ventas
- Generar venta (verifica stock y descuenta ingredientes)
- Eliminar venta
- Informe de ventas con total de ingresos
- Ver carta completa con recetas
- Registrar nuevo platillo con receta personalizada

### 👥 Recursos Humanos
- Ver plantilla de empleados con sueldo semanal calculado
- Agregar empleado
- Dar de baja
- Modificar datos
- Buscar por nombre

### ⚙️ Configuración
- Cambiar contraseña del administrador

---

## Persistencia

Todos los datos se guardan en **`datos_sushi.txt`** al salir por la opción 5.
Al iniciar, el sistema carga automáticamente ese archivo si existe.

---

## Cambios respecto al código C++ original

| Aspecto             | C++ original                  | Java — Sushi Tegridad               |
|---------------------|-------------------------------|--------------------------------------|
| Paradigma           | Procedural                    | Orientado a Objetos (OOP)            |
| Datos               | Arrays estáticos              | `ArrayList` dinámico                 |
| Recetas             | Arreglo 2D por índice         | `Map<String, Float>` por nombre      |
| Módulos             | `switch` en `main()`          | Clases especializadas por dominio    |
| Persistencia        | Funciones globales            | Clase estática `GestorArchivos`      |
| Tema                | Pastelería                    | Restaurante de Sushi 🍣              |
