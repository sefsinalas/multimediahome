# Video sorpresa para FlowBox

Aplicación pequeña para Android TV 10. Al abrirla busca videos indexados de tipo `.mp4`, `.mkv`, `.webm`, `.mov` o `.avi` en `Movies/MOTIVATION`, elige uno al azar y lo reproduce con el reproductor de Android. Al terminar vuelve al panel. Si hay varios, evita repetir el último. No contiene credenciales de Dropbox ni usa la red.

La sincronización se configura aparte en Round Sync: origen `Dropbox:/MOTIVATION`, destino local `/storage/emulated/0/Movies/MOTIVATION`, sentido Dropbox → FlowBox. Usar **Copy** para que una eliminación accidental en Dropbox no borre la copia local. Programar una ejecución diaria cuando esté conectado a la red. Los videos permanecen privados y disponibles sin conexión una vez descargados.

## Compilar

Requiere JDK, Android SDK con `platforms/android-36` y `build-tools/36.0.0`. En esta Mac:

```sh
sh android/motivation-player/build.sh
adb install android/motivation-player/.local/build/video-sorpresa.apk
```

El script genera una llave local de desarrollo bajo `.local/`; esa carpeta no se sube a Git. Para actualizar la app sin desinstalarla, conservar esa llave.

## Abrir

Desde el launcher de Android TV, seleccionar **Video sorpresa**. También responde al enlace `motivacion://azar`, destinado al botón del panel de oficina. TV Bro pide confirmar la apertura de la app externa; seleccionar **YES**.
