# Entrega final Computación en Java
+
## Acerca de
Cliente de Facebook en línea de comando para ver o publicar en tu cuenta de facebook.

## Guia de uso
**Clonar el repositorio**  

git clone https://github.com/AlanValdez/fbcmd4j.git

**Importar a Eclipse**  
1. Estando en eclipse dar click en FILE -> IMPORT -> SELECCIONA LA CARPETA DEL PROYECTO.

**Exportar .jar**  
1. Dar clic derecho al proyecto en Eclipse.
2. Dar click en Export -> Runnable JAR file.

**Ejecutar .jar**  
1. Abrir la carpeta bin situada ya en el proyecto.
![](https://cdn.discordapp.com/attachments/239132105609576458/441730150334464000/unknown.png)
2. Abrir linea de comando en la carpeta presionado `shift + click derecho` -> Open powershell o bien abrir la linea de comando y desplazarte a la carpeta con el jar en el.
![](https://cdn.discordapp.com/attachments/239132105609576458/441731289632997378/unknown.png)
3. Ejecutar el comando `java -jar fbcmd4j.jar`.
![](https://cdn.discordapp.com/attachments/239132105609576458/441737461530492930/unknown.png)

## Uso
**Configurar la cuenta de Facebook**   
1. Seleccionar la opción 0 Obtener token.
2. Escribir el codigo que se proporciona en el navegador.
![](https://cdn.discordapp.com/attachments/239132105609576458/441737727055101954/unknown.png)
3. Dar permiso a la plicacion.
![](https://cdn.discordapp.com/attachments/239132105609576458/441738837237039105/unknown.png)
![](https://cdn.discordapp.com/attachments/239132105609576458/441737865144041503/unknown.png)


**Menu**   
1. Una vez obtenido tu token podras usar el cliente.


**Clases y metodos**
-Main:
Contiene el menu del programa

-ControladorFacebook
Contiene los metodos:
* cargaArchivo: carga los archivos de configuracion a el programa
* configTokens: crea un token para poder usar el programa
* guardaPropiedades: guarda el token creado del metodo configTokens
* sb: Simple stringbuilder para el metodo un metodo httpost
* metodoPostJson: regresa la conexion de un metodo httpost que contiene el token para autentificar
* guardarPublicacionArchivo: guarda las publicaciones vistas
* configFacebook: configura el controlador
* printPost: imprime las publicaciones
* postEstado: publica estados
* postLink: publica links

## Créditos
Desarrollado por:
- 2711306 Alan Bledimir Valdez Chavez
- Jose Manuel por 

## Licencia 
GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
of this license document, but changing it is not allowed.


