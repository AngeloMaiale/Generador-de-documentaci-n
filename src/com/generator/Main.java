package com.generator;

import com.generator.core.DocGenerator;
import test.models.Usuario;

public class Main {
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("▶️ Iniciando Generador de Documentación Rúbrica 100%");
        System.out.println("==============================================\n");

        DocGenerator engine = new DocGenerator();
        try {
            engine.generateMarkdown(Usuario.class);

        } catch (Exception e) {
            System.err.println("❌ Ocurrió un error crítico durante la generación:");
            e.printStackTrace();
        }
    }
}