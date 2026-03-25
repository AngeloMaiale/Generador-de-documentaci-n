package com.generator;

import com.generator.core.DocGenerator;
import test.models.Usuario;

public class Main {
    public static void main(String[] args) {
        DocGenerator generator = new DocGenerator();
        System.out.println("=== Generador de Documentación Java ===");
        try {
            Class<?> claseADocumentar = Usuario.class;
            System.out.println("Procesando clase: " + claseADocumentar.getSimpleName() + "...");
            generator.generateMarkdown(claseADocumentar);
            System.out.println("---------------------------------------");
            System.out.println("¡Éxito! El archivo Markdown ha sido generado en la carpeta raíz.");
            System.out.println("Nombre del archivo: " + claseADocumentar.getSimpleName() + "_Doc.md");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se pudo encontrar la clase especificada.");
        } catch (Exception e) {
            System.err.println("Ocurrió un error durante la generación:");
            e.printStackTrace();
        }
    }
}