package com.generator.core;
import com.generator.annotations.DocumentedClass;
import com.generator.annotations.DocumentedMethod;
import java.lang.reflect.*;
import java.nio.file.*;

public class DocGenerator {

    public void generateMarkdown(Class<?> clazz) throws Exception {
        StringBuilder md = new StringBuilder();
        md.append("# Documentación de Clase: ").append(clazz.getSimpleName()).append("\n\n");
        if (clazz.isAnnotationPresent(DocumentedClass.class)) {
            DocumentedClass ann = clazz.getAnnotation(DocumentedClass.class);
            md.append("- **Autor:** ").append(ann.author()).append("\n");
            md.append("- **Versión:** ").append(ann.version()).append("\n");
            md.append("- **Descripción:** ").append(ann.description()).append("\n");
        }
        md.append("- **Es subclase:** ").append(!clazz.getSuperclass().equals(Object.class)).append("\n\n");
        md.append("## Atributos\n");
        for (Field field : clazz.getDeclaredFields()) {
            md.append(String.format("- **%s** (%s) | Modificadores: %s\n",
                    field.getName(),
                    field.getType().getSimpleName(),
                    Modifier.toString(field.getModifiers())));
        }
        md.append("\n## Métodos\n");
        for (Method method : clazz.getDeclaredMethods()) {
            md.append("### ").append(method.getName()).append("\n");
            md.append("- **Retorno:** ").append(method.getReturnType().getSimpleName()).append("\n");
            md.append("- **Modificadores:** ").append(Modifier.toString(method.getModifiers())).append("\n");

            if (method.isAnnotationPresent(DocumentedMethod.class)) {
                md.append("- **Descripción:** ").append(method.getAnnotation(DocumentedMethod.class).description()).append("\n");
            }
            if (method.getName().startsWith("get") || method.getName().startsWith("set")) {
                md.append("- **Tipo:** Accesor (Getter/Setter)\n");
            }
            md.append("\n");
        }
        Files.writeString(Paths.get(clazz.getSimpleName() + "_Doc.md"), md.toString());
        System.out.println("Documentación generada exitosamente.");
    }
}