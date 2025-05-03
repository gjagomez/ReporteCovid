# 🦠 COVID-19 Report

## 📌 1. Introducción

Este proyecto consume datos de la API pública de estadísticas de COVID-19 para Estados Unidos y almacena los resultados en una base de datos MySQL.  
Implementa lógica para evitar ejecuciones duplicadas por país y fecha, y proporciona consultas ordenadas por provincia.

---

## 🗂️ 2. Estructura del Proyecto

### 🌳 2.1 Árbol de Directorios

```text
covidreport/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org.covidreport/
│   │   │       ├── config/          # ⚙️ Configuración global
│   │   │       ├── controller/      # 🎮 Controlador principal
│   │   │       ├── model/           # 🧩 Modelos de datos
│   │   │       ├── repository/      # 💾 Repositorios de base de datos
│   │   │       ├── service/         # 🧠 Lógica de negocio
│   │   │       ├── thread/          # 🧵 Hilos de procesamiento
│   │   │       └── util/            # 🛠️ Utilidades
│   │   └── resources/               # 📦 Configuraciones
└── pom.xml                          # 📄 Configuración de Maven
```

---

## 🧱 3. Componentes Principales

### 📦 3.1 Paquetes Clave

| 📁 Paquete     | Descripción                                                                 |
|----------------|------------------------------------------------------------------------------|
| `config`       | ⚙️ Configuración de la aplicación.                                           |
| `controller`   | 🎮 Punto de entrada principal.                                               |
| `model`        | 🧩 Entidades de dominio como `Report` y `Region`.                           |
| `repository`   | 💾 Interacción con la base de datos.                                         |
| `service`      | 🧠 Lógica principal de negocio.                                              |
| `thread`       | 🧵 Gestión de hilos.                                                         |
| `util`         | 🛠️ Utilidades como manejo de conexión JDBC.                                |

### 📌 3.2 Clases Destacadas

#### 🔧 `AppConfig.java`

```java
// Carga configuraciones desde application.properties
```

#### 🚀 `MainController.java`

```java
// Inicia el hilo de procesamiento
```

#### 📊 `Report.java`

```java
// Contiene fecha, casos confirmados y región
```

#### 💾 `ReportRepository.java`

```java
// Guarda y consulta reportes por país y fecha
```

#### 🧠 `ReportService.java`

```java
// Procesa reportes y evita duplicados
```

---

## 🔁 4. Flujo de Ejecución

1. 🔄 Inicio: `MainController` lanza el hilo.
2. ✅ Verificación: evita ejecución si ya se procesó.
3. 🌐 Llamada a API: obtiene datos con reintentos.
4. 🗃️ Guardado: inserta datos en MySQL.
5. 📝 Registro: marca la ejecución como completada.
6. 📈 Consulta: devuelve datos agrupados y ordenados.

---

## 🗄️ 5. Base de Datos

### 🧾 5.1 Tablas

```sql
-- 🦠 Tabla de reportes
CREATE TABLE reports (...);

-- 🏙️ Tabla de ciudades
CREATE TABLE cities (...);

-- 📅 Tabla de logs de ejecución
CREATE TABLE executed_reports (...);
```

---

## 🌐 6. Integración con la API

- **URL**: `https://covid-19-statistics.p.rapidapi.com/reports`
- **Parámetros**:
  - 🏳️ `iso`: País (ej: `USA`)
  - 📍 `region_name`: Región (ej: `US`)
  - 📆 `date`: Fecha (`yyyy-MM-dd`)

---

## ⚠️ 7. Manejo de Errores

- ⏳ **Timeouts**: 10 segundos.
- 🔁 **Reintentos**: 3 intentos con backoff.
- 🪵 **Logs**: salida de errores e info.

---

## 🧑‍🏫 8. Buenas Prácticas Aplicadas

- 🧠 **Principios SOLID**:
  - SRP: Una responsabilidad por clase.
  - OCP: Clases abiertas a extensión.
- 🧩 **Inyección de dependencias**.
- 🚀 **HikariCP** para pooling de conexiones.
- 🧹 **`UNIQUE` constraints** para evitar duplicados.

---

## ▶️ 9. Ejemplo de Uso

```java
String iso = "USA";
LocalDate date = LocalDate.parse("2020-04-16");

ReportProcessingThread thread = new ReportProcessingThread(iso, date);
thread.start();
```

---

## ✅ 10. Pruebas Sugeridas

- ❌ Ejecutar dos veces el mismo país/fecha → solo una ejecución.
- ✅ Consultar reportes agrupados por provincia.

---

## 🚧 11. Limitaciones

- ⚠️ La API tiene límites de uso.
- 🔒 No hay autenticación avanzada.

---

## 🛠️ 12. Herramientas Utilizadas

- ☕ Java SE 17  
- 🧰 Maven  
- 🐬 MySQL  
- 🔄 HikariCP  
- 📦 `org.json`  
