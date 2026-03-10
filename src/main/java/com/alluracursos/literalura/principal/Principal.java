package com.alluracursos.literalura.principal;

import com.alluracursos.literalura.model.*;
import com.alluracursos.literalura.repository.AutorRepository;
import com.alluracursos.literalura.repository.LibroRepository;
import com.alluracursos.literalura.service.ConsumoAPI;
import com.alluracursos.literalura.service.ConvertirDatos;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner lectura = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvertirDatos conversor = new ConvertirDatos();
    private final String URL_BASE = "https://gutendex.com/books/";

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ***************************************************
                    BIENVENIDO A LITERALURA - TU CATÁLOGO DE LIBROS
                    ***************************************************
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    0 - Salir
                    ***************************************************
                    Elija una opción:
                    """;
            System.out.println(menu);
            try {
                opcion = lectura.nextInt();
                lectura.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número.");
                lectura.nextLine();
                continue;
            }

            switch (opcion) {
                case 1 -> buscarLibroPorTitulo();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivosEnAnio();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Cerrando la aplicación...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el nombre del libro que desea buscar:");
        var nombreLibro = lectura.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, RespuestaAPI.class);

        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibro datos = libroBuscado.get();

            // Verificamos si el libro ya está registrado
            if (libroRepository.findByTituloIgnoreCase(datos.titulo()).isPresent()) {
                System.out.println("El libro ya se encuentra registrado en la base de datos.");
                return;
            }

            // Manejo del Autor (Tarjeta 9 - Persistencia)
            DatosAutor datosAutor = datos.autor().get(0);
            Autor autor = autorRepository.findByNombreIgnoreCase(datosAutor.nombre())
                    .orElseGet(() -> autorRepository.save(new Autor(datosAutor)));

            // Registro del Libro
            Libro libro = new Libro(datos);
            libro.setAutor(autor);
            libroRepository.save(libro);
            System.out.println(libro);
        } else {
            System.out.println("Libro no encontrado.");
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar:");
        var nombreLibro = lectura.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, RespuestaAPI.class);

        return datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst()
                .orElse(null);
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingrese el año que desea consultar:");
        var anio = lectura.nextInt();
        lectura.nextLine();
        List<Autor> autoresVivos = autorRepository.autoresVivosEnDeterminadoAnio(anio);
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en ese año." + anio);
        } else {
            autoresVivos.forEach(System.out::println);
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
            Ingrese el idioma para buscar los libros:
            es - español
            en - inglés
            fr - francés
            pt - portugués
            """);
        var idioma = lectura.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma seleccionado.");
        } else {
            librosPorIdioma.forEach(System.out::println);
        }
    }
}
