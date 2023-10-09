# Try&Catch

## Propuesta actualizada
La idea de la página web es similar a Stack Overflow, pero se enfoca en proporcionar ayuda a estudiantes universitarios. Los usuarios pueden registrarse y hacer preguntas relacionadas con cualquier tema universitario, y otros usuarios pueden responder a estas preguntas.

El método para determinar la precisión de las respuestas es mediante votaciones de los usuarios, lo que significa que las preguntas con más votos serán consideradas más fiables. Sin embargo, también se tiene en cuenta la verificación de los usuarios, como por ejemplo, si son profesores.

La plataforma también permite a los usuarios buscar información sobre otras universidades y carreras, como una fuente informativa adicional. Además, la plataforma cuenta con un rol de administrador que se encarga de gestionar los usuarios y universidades, y de atender las solicitudes de los usuarios.

En resumen, la página web es una plataforma donde los estudiantes universitarios pueden formular preguntas y obtener respuestas de otros usuarios, lo que les permite ampliar su conocimiento y resolver dudas académicas. La plataforma también ofrece información sobre universidades y carreras, y cuenta con un equipo de administradores que gestionan la plataforma y atienden las solicitudes de los usuarios.

## Diagrama actualizado de tu BD
![Db](https://github.com/Alejandrodlrio/IW/blob/main/doc/iwDb.png)

## Una descripción de usuarios existentes, con sus roles y contraseñas
Existen 3 usuarios:
  - User: Puede crear, ver, votar preguntas y respuestas
  - Profesor: Puede crear, ver, votar preguntas y respuestas pero al votar una pregunta/respuesta aporta más veracidad a dicha pregunta/respuesta
  - Admin: Gestionar los usuarios y universidades, y de atender las solicitudes de los usuarios.

  - usuario: juan@ucm.es contraseña: aa (Usuario, profesor)
  - usuario:pepe@ucm.es contraseña: aa (Usuario, admin)

## Una descripción de tus pruebas
Hay 3 pruebas:
  - Flujo principal: login usuario a , crea pregunta, logout usuario a, login usuario b crea respuesta en la pregunta realizada por usuario a, logout de usuario b, login de usuario a, vota respuesta creada por usuario b.
  - Flujo administrador: Acceder a la pagina de administrado, buscar un usuario y le hacer profesor.
  - Registro: crear nuevo usuario.


* **Atribuciones** - En la pagina web existen varias imagenes extraidas del sitio Freepik:
 * <a href="https://www.freepik.es/vector-gratis/ilustracion-concepto-personas-curiosidad_30576696.htm#query=question&position=3&from_view=search&track=sph">Imagen de storyset</a> en Freepik
 * <a href="https://www.freepik.es/vector-gratis/ilustracion-concepto-busqueda-curiosidad_30576701.htm#query=question&position=18&from_view=search&track=sph">Imagen de storyset</a> en Freepik
 * <a href="https://www.freepik.es/vector-gratis/preguntas-respuestas-o-preguntas-frecuentes-conceptos-preguntas-frecuentes-sitios-web-paginas-negocios-redes-sociales-ilustracion-vector-dibujos-animados-plana_24070638.htm#query=Q%20amp%20A&position=1&from_view=search&track=ais">Imagen de jcomp</a> en Freepik
 * <a href="https://www.freepik.es/vector-gratis/ilustracion-concepto-opinion_12219840.htm#page=2&query=survey&position=14&from_view=search&track=sph">Imagen de storyset</a> en Freepik
 * <a href="https://www.freepik.es/vector-gratis/ilustracion-concepto-archivos-texto_11641796.htm#query=documents&position=32&from_view=search&track=sph">Imagen de storyset</a> en Freepik
 * <a href="https://www.freepik.es/vector-gratis/ilustracion-concepto-archivos-texto_11641796.htm#query=documents&position=32&from_view=search&track=sph">Imagen de storyset</a> en Freepik

 * Imagen de <a href="https://www.freepik.es/vector-gratis/coleccion-plana-gente-yendo-universidad_4672246.htm">Freepik</a>
