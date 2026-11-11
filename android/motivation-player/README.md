# Video sorpresa para FlowBox

Aplicación para Android TV 10 que abre el panel de oficina. Al pulsar 0 en el control, busca videos indexados de tipo `.mp4`, `.mkv`, `.webm`, `.mov` o `.avi` en `Movies/MOTIVATION`, elige uno al azar y lo reproduce con el reproductor de Android. Al terminar vuelve al panel. Si hay varios, evita repetir el último. No contiene credenciales de Dropbox; usa Internet para cargar el panel y el clima.

La sincronización se configura aparte en Round Sync: origen `Dropbox:/MOTIVATION`, destino local `/storage/emulated/0/Movies/MOTIVATION`, sentido Dropbox → FlowBox. Usar **Copy** para que una eliminación accidental en Dropbox no borre la copia local. Programar una ejecución diaria cuando esté conectado a la red. Los videos permanecen privados y disponibles sin conexión una vez descargados.

## Compilar

Requiere JDK, Android SDK con `platforms/android-36` y `build-tools/36.0.0`. En esta Mac:

```sh
sh android/motivation-player/build.sh
adb install android/motivation-player/.local/build/video-sorpresa.apk
```

El script genera una llave local de desarrollo bajo `.local/`; esa carpeta no se sube a Git. Para actualizar la app sin desinstalarla, conservar esa llave. El panel usa el paquete `ar.com.flowbox.office` porque el FlowBox impide desinstalar la app anterior `ar.com.flowbox.motivation` y ya no está disponible su llave de firma.

## Abrir

Desde el launcher de Android TV, seleccionar **Panel de oficina**. Pulsar **0** para reproducir un video sin salir a TV Bro. La app anterior conserva el enlace `motivacion://azar` si se usa desde otra app.
