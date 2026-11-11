# Panel de oficina

Panel de pantalla completa para el FlowBox: clima actual y pronóstico, más relojes de Cancún, EST y Ciudad de México. Los relojes que coinciden se agrupan en una sola hora. En la app Android del panel, la tecla 0 del control reproduce un video sorpresa local y vuelve al panel cuando termina.

## Publicar en Netlify

Importá este repositorio desde Netlify. Es un sitio HTML estático: no necesita comando de compilación. `netlify.toml` ya indica que se publique la raíz del repositorio.

El clima usa la API pública de [Open-Meteo](https://open-meteo.com/); el navegador del dispositivo necesita conexión a Internet para actualizarlo.
