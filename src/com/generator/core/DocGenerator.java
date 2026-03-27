package com.generator.core;

import com.generator.annotations.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class DocGenerator {

    private Set<String> fieldNames;

    public void generateMarkdown(Class<?> clazz) throws Exception {
        StringBuilder sb = new StringBuilder();
        this.fieldNames = getFieldNames(clazz);
        sb.append("# Documentación de la Clase: `").append(clazz.getSimpleName()).append("`\n\n");
        sb.append("## Información General\n\n");
        sb.append("| Concepto | Detalle |\n");
        sb.append("| :--- | :--- |\n");
        sb.append("| **Nombre Completo** | `").append(clazz.getName()).append("` |\n");

        if (clazz.isAnnotationPresent(DocumentedClass.class)) {
            DocumentedClass ann = clazz.getAnnotation(DocumentedClass.class);
            sb.append("| **Autor** | ").append(ann.author()).append(" |\n");
            sb.append("| **Descripción** | ").append(ann.description()).append(" |\n");
            sb.append("| **Versión** | ").append(ann.version()).append(" |\n");
        }

        boolean isSubclass = !clazz.getSuperclass().equals(Object.class);
        sb.append("| **Es Subclase** | ").append(isSubclass ? "Sí (Hereda de `" + clazz.getSuperclass().getSimpleName() + "`)" : "No").append(" |\n\n");

        sb.append("## Atributos\n\n");
        sb.append("| Nombre | Tipo | Modificadores | Descripción |\n");
        sb.append("| :--- | :--- | :--- | :--- |\n");

        for (Field field : clazz.getDeclaredFields()) {
            String desc = field.isAnnotationPresent(DocumentedField.class)
                    ? field.getAnnotation(DocumentedField.class).description()
                    : "N/A";

            sb.append(String.format("| **%s** | `%s` | `%s` | %s |\n",
                    field.getName(),
                    field.getType().getSimpleName(),
                    Modifier.toString(field.getModifiers()),
                    desc));
        }
        sb.append("\n");

        sb.append("## Operaciones (Constructores y Métodos)\n\n");
        sb.append("| Firma (Nombre) | Descripción | Parámetros | Retorno | Mods | Cons. | Over. | G/S |\n");
        sb.append("| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |\n");

        for (Constructor<?> cons : clazz.getDeclaredConstructors()) {
            sb.append(String.format("| **%s()** | Constructor por defecto. | %s | N/A | `%s` | Sí | No | No |\n",
                    clazz.getSimpleName(),
                    formatParameters(cons.getParameters()),
                    Modifier.toString(cons.getModifiers())));
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isSynthetic()) continue; // Ignorar métodos generados por el compilador

            String desc = method.isAnnotationPresent(DocumentedMethod.class)
                    ? method.getAnnotation(DocumentedMethod.class).description() : "N/A";

            sb.append(String.format("| **%s()** | %s | %s | `%s` | `%s` | No | %s | %s |\n",
                    method.getName(),
                    desc,
                    formatParameters(method.getParameters()),
                    method.getReturnType().getSimpleName(),
                    Modifier.toString(method.getModifiers()),
                    isOverridden(method, clazz) ? "Sí" : "No",
                    checkAccessorForField(method)
            ));
        }

        Path outputPath = Paths.get(clazz.getSimpleName() + "_Doc.md");
        Files.writeString(outputPath, sb.toString());
        System.out.println("------------------------------------------------");
        System.out.println("✅ Documentación 100% Rúbrica generada en:\n👉 " + outputPath.toAbsolutePath());
        System.out.println("------------------------------------------------");
    }

    private Set<String> getFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    private String formatParameters(Parameter[] parameters) {
        if (parameters.length == 0) return "Ninguno";
        return Arrays.stream(parameters)
                .map(p -> "`" + p.getType().getSimpleName() + " " + p.getName() + "`")
                .collect(Collectors.joining(", <br>")); // <br> para salto de línea en celda
    }

    private String checkAccessorForField(Method m) {
        String name = m.getName();
        String possibleField = "";
        String type = "";

        if ((name.startsWith("get") || name.startsWith("is")) && m.getParameterCount() == 0) {
            type = "Getter";
            possibleField = name.startsWith("get") ? name.substring(3) : name.substring(2);
        } else if (name.startsWith("set") && m.getParameterCount() == 1) {
            type = "Setter";
            possibleField = name.substring(3);
        } else {
            return "No";
        }

        if (possibleField.isEmpty()) return "No";
        String fieldRealName = possibleField.substring(0, 1).toLowerCase() + possibleField.substring(1);

        if (this.fieldNames.contains(fieldRealName)) {
            return String.format("Sí (%s de `%s`) ", type, fieldRealName);
        }

        return "No";
    }

    private boolean isOverridden(Method method, Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && !superClass.equals(Object.class)) {
            try {
                superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return true;
            } catch (NoSuchMethodException e) {
                superClass = superClass.getSuperclass();
            }
        }
        return false;
    }
}