package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.model.Datos;
import com.aluracursos.desafio.model.DatosLibro;
import com.aluracursos.desafio.services.ConsumoAPI;
import com.aluracursos.desafio.services.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class principal {
    private final ConsumoAPI consumoAPI = new ConsumoAPI();

    private final ConvierteDatos conversor = new ConvierteDatos();

    private final Scanner teclado = new Scanner(System.in);
    public void muestraElMenu(){
        String URL_BASE = "https://gutendex.com/books/";
        var json = consumoAPI.obtenerDatos(URL_BASE);
//        System.out.println(json);
        var datos = conversor.obtenerDatos(json,Datos.class);
//        System.out.println(datos);

        //Top 15 de los libros más descargados
        System.out.println("------------------------------------------------");
        System.out.println("** \uD83D\uDCDA Top 15 de los libros más descargados \uD83D\uDCDA **");
        System.out.println("------------------------------------------------");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibro::numeroDeDescargas).reversed())
                .limit(15)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);

        //Busqueda de libros por su nombre
        System.out.println("--------------------------------------------");
        System.out.println("Ingresa el nombre del libro que desea buscar:");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE +"?search=" + tituloLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibro> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println("Libro Encontrado con éxito \uD83E\uDD29");
            System.out.println("------------------------------------------");
            System.out.println(libroBuscado.get());


            //Trabajando con estadisticas
            DoubleSummaryStatistics est = datos.resultados().stream()
                    .filter(d -> d.numeroDeDescargas() >0 )
                    .collect(Collectors.summarizingDouble(DatosLibro::numeroDeDescargas));
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("✅ Media de descargas: " + est.getAverage());
            System.out.println("✅ Máxima de descargas: "+ est.getMax());
            System.out.println("✅ Mínima de descargas: " + est.getMin());
            System.out.println("\uD83C\uDD97 Total de registros evaluados para calcular las estadísticas: " + est.getCount());
            System.out.println("-------------------------------------------------------------------------------");

        }else {
            System.out.println("Libro no encontrado. Intentelo de nuevo \uD83D\uDE14");
        }

    }

}