# programacion2-segundaparcial

## ClienteApp - Sistema de Gestión de Clientes

### 📱 Descripción General
Aplicación Android desarrollada en Java que permite la gestión integral de clientes con captura de fotografías, carga de archivos y sincronización automática de logs de auditoría.

---

## ⚡ Funcionalidades Principales

### 1. **Registro de Clientes con Fotografías** 📸
- Formulario completo con datos del cliente (CI, nombre, dirección, teléfono)
- Captura de 3 fotografías de la casa del cliente usando la cámara
- Compresión automática de imágenes para optimizar el envío
- Envío multipart al servidor combinando datos JSON + imágenes

### 2. **Carga Múltiple de Archivos** 📦
- Selección múltiple de archivos desde el dispositivo
- Compresión automática en formato ZIP
- Envío al servidor asociado al CI del cliente
- Soporte para cualquier tipo de archivo

### 3. **Sistema de Auditoría Local** 📊
- Base de datos Room para persistencia de logs
- Registro automático de errores con try-catch
- Visualización de historial completo de eventos
- Almacenamiento con timestamp, descripción y clase de origen

### 4. **Sincronización Automática** 🔄
- WorkManager para tareas en segundo plano
- Sincronización cada 15 minutos
- Envío de logs al servidor en formato JSON
- Limpieza automática después de sincronización exitosa

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java 8 + Kotlin (UI)
- **IDE:** Android Studio
- **Base de Datos:** Room Database 2.5.2
- **Red:** Retrofit 2.9.0 + OkHttp
- **Tareas en Background:** WorkManager 2.8.1
- **Imágenes:** Glide 4.15.1
- **UI:** Jetpack Compose + Material Design 3

---

## 📋 Requisitos

- Android Studio Hedgehog o superior
- Min SDK: API 24 (Android 7.0)
- Target SDK: API 34 (Android 14)
- Conexión a Internet para envío de datos

---

## 🚀 Instalación

1. Clona el repositorio:
```bash
git clone https://github.com/TU-USUARIO/programacion2-segundaparcial.git
```

2. Abre el proyecto en Android Studio

3. Configura tu Webhook:
    - Ve a https://webhook.site
    - Copia tu UUID único
    - Reemplaza en `app/src/main/java/.../api/ApiService.java`

4. Sincroniza Gradle:
```
File → Sync Project with Gradle Files
```

5. Ejecuta la aplicación:
```
Run → Run 'app'
```

---

## 📁 Estructura del Proyecto

```
com.example.clienteapp/
├── api/
│   ├── ApiService.java
│   └── RetrofitClient.java
├── database/
│   ├── LogApp.java
│   ├── LogAppDao.java
│   ├── AppDatabase.java
│   └── LogHelper.java
├── worker/
│   └── SyncLogsWorker.java
├── ui/theme/
│   └── Theme.kt
├── ClienteFormActivity.java
├── UploadFilesActivity.java
├── MainActivity.kt
└── MyApplication.java
```

---

## 🎯 Uso de la Aplicación

### Registrar un Cliente:
1. Desde el menú principal, selecciona "📋 Registrar Cliente"
2. Completa todos los campos del formulario
3. Captura las 3 fotografías (se comprimen automáticamente)
4. Presiona "✅ ENVIAR CLIENTE"
5. Verifica en webhook.site la recepción de datos

### Cargar Archivos:
1. Selecciona "📦 Cargar Archivos ZIP"
2. Ingresa el CI del cliente
3. Selecciona múltiples archivos
4. Presiona "🚀 Comprimir y Enviar"
5. El sistema comprimirá y enviará automáticamente

---

## 🔐 Permisos Requeridos

- `INTERNET` - Envío de datos
- `CAMERA` - Captura de fotografías
- `READ_EXTERNAL_STORAGE` - Lectura de archivos
- `READ_MEDIA_IMAGES` - Acceso a imágenes (Android 13+)

---

## 👨‍💻 Autor

**Tobias Franco**
- Universidad: Uninorte
- Materia: Programación 2 - Segunda Parcial
- Fecha: Noviembre 2025

---

## 📄 Licencia

Proyecto desarrollado con fines académicos.

---

