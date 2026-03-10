# 📚 LiterAlura - Challenge Alura Latam

Este proyecto es un catálogo de libros interactivo desarrollado en Java con Spring Boot. Utiliza la API de Gutendex para obtener información literaria y la almacena en una base de datos MySQL.

## 🚀 Funcionalidades
1. **Buscar libro por título:** Obtiene datos directamente de la API y los guarda en la BD.
2. **Listar libros registrados:** Muestra todos los libros almacenados.
3. **Listar autores registrados:** Muestra los autores de los libros guardados.
4. **Filtro de autores vivos:** Permite consultar autores que estaban vivos en un año específico.
5. **Listado por idioma:** Filtra libros según su código de idioma (es, en, fr, pt).

## 🛠️ Tecnologías
* Java 17
* Spring Boot 3.2.3
* Spring Data JPA
* MySQL (XAMPP)
* Jackson (Manejo de JSON)

## 📋 Requisitos
* JDK 17 o superior.
* MySQL corriendo (vía XAMPP o local).
* Configurar usuario/password en `src/main/resources/application.properties`.
