package com.generator;

import com.generator.core.DocGenerator;
import test.models.Usuario;

public class Main {
    public static void main(String[] args) {
        DocGenerator generator = new DocGenerator();
        try {
            System.out.println("Iniciando análisis de clase con Reflection...");
            generator.generateMarkdown(Usuario.class);
            System.out.println("Proceso finalizado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al generar la documentación:");
            e.printStackTrace();
        }
    }
}