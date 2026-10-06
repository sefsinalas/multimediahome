# Panel de oficina

Panel de pantalla completa para el FlowBox: clima actual y pronóstico, más relojes de Cancún, EST y Ciudad de México. Los relojes que coinciden se agrupan en una sola hora. La ciudad del clima se puede cambiar desde el panel y queda guardada en ese navegador.

## Publicar en Netlify

Importá este repositorio desde Netlify. Es un sitio HTML estático: no necesita comando de compilación. `netlify.toml` ya indica que se publique la raíz del repositorio.

El clima usa la API pública de [Open-Meteo](https://open-meteo.com/); el navegador del dispositivo necesita conexión a Internet para actualizarlo.
