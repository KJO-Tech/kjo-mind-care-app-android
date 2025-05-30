# KJO Mind Care - App Android 📱🧘‍♀️

KJO Mind Care App ofrece a los usuarios una experiencia personalizada para registrar su estado emocional diario, leer blogs sobre salud mental, acceder a centros de ayuda cercanos y contactar líneas de emergencia. En esta nueva versión, Firebase se encarga de manejar la base de datos, autenticación y almacenamiento de imágenes, facilitando así la sincronización de datos entre dispositivos.

## 🛠 Tecnologías Usadas

- Kotlin (Jetpack Compose)

- Firebase (Auth, Firestore, Storage, Notifications)

- Google Maps (API de ubicación)

- Retrofit + Hilt

## 📦 Casos de Uso

| Caso de Uso                           | Descripción                                                                                                                        | Rol     |
|:--------------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------|:--------|
| Iniciar sesión                        | Crear nuevo usuario mediante correo o Google                                                                                       | Usuario |
| Registrarse                           | Acceso mediante correo o Google                                                                                                    | Usuario |
| Visualizar blogs publicados           | Ver los blogs que tienen estado "Publicado"                                                                                        | Usuario |
| Filtrar blogs publicados              | Filtrar los blogs publicados por categoría                                                                                         | Usuario |
| Visualizar detalle de un blog         | Ver el detalle de un blog, incluyendo sus comentarios, reacciones, etc                                                             | Usuario |
| Visualizar mis blogs                  | Obtener una lista de los blogs que ha creado un usuario                                                                            | Usuario |
| Crear blog                            | Publicar entrada nueva en el blog                                                                                                  | Usuario |
| Editar mis blogs                      | Editar los blogs que ha creado un usuario                                                                                          | Usuario |
| Eliminar mis blogs                    | Cambiar de estado a los blogs creados por un usuario                                                                               | Usuario |
| Crear comentario                      | Publicar un comentario en un blog                                                                                                  | Usuario |
| Responder comentario                  | Responder un comentario ya publicado en algun blog                                                                                 | Usuario |
| Editar mi comentario                  | Editar mi comentario                                                                                                               | Usuario |
| Eliminar mi comentario                | Eliminar mi comentario                                                                                                             | Usuario |
| Visualizar centros de salud cercanos  | Obtener los centros de salud más cercanos a mi en un radio de 5km y poder observarlos en un mapa                                   | Usuario |
| Obtener detalle de un centro de salud | Visualizar la información de un centro de salud seleccionado                                                                       | Usuario |
| Visualizar recursos de emergencia     | Obtener todos los recursos de emergencia creados por el administrador, poder abrir su enlace web y guardar el contacto relacionado | Usuario |
| Registrar estado de ánimo             | Guardar cómo se siente el usuario día a día                                                                                        | Usuario |
| Ver historial emocional               | Mostrar gráficas interactivas de emociones pasadas                                                                                 | Usuario |
| Ver mi perfil                         | Visualizar la información de un usuario, cantidad de blogs, de registros                                                           | Usuario |

## 📲 Características de la App

- Interfaz amigable para adolescentes.

- Registro diario de emociones con selección de íconos/colores.

- Lectura de contenido verificado sobre salud mental.

- Geolocalización de centros y recursos.

- Integración con Firebase para sincronización y autenticación.

- Notificaciones (en desarrollo).

## ⚙️ Configuración del proyecto

- Primero clona el repositorio

```bash
git clone https://github.com/KJO-Tech/kjo-care-front
```

- Dirígete al directorio

```bash
cd kjo-care-front
```

- Conéctate a un proyecto de firebase

- Ejecuta el proyecto y visualízalo en un emulador o en tu propio dispositivo Android