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
        sb.append("# Clase: `").append(clazz.getSimpleName()).append("`\n\n");

        if (clazz.isAnnotationPresent(DocumentedClass.class)) {
            DocumentedClass ann = clazz.getAnnotation(DocumentedClass.class);
            sb.append("> ").append(ann.description()).append("\n\n");
            sb.append("- **Autor:** ").append(ann.author()).append("\n");
            sb.append("- **Versión:** ").append(ann.version()).append("\n\n");
        }

        boolean isSubclass = !clazz.getSuperclass().equals(Object.class);
        if (isSubclass) {
            sb.append("**Extiende:** `").append(clazz.getSuperclass().getSimpleName()).append("`\n\n");
        }

        sb.append("## Índice\n\n");

        sb.append("### Atributos (Properties)\n");
        for (Field field : clazz.getDeclaredFields()) {
            sb.append("- [`").append(field.getName()).append("`](#").append(field.getName().toLowerCase()).append(")\n");
        }

        sb.append("\n### Métodos (Methods)\n");
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;
            sb.append("- [`").append(method.getName()).append("`](#").append(method.getName().toLowerCase()).append(")\n");
        }
        sb.append("\n---\n\n");

        sb.append("## Atributos\n\n");
        for (Field field : clazz.getDeclaredFields()) {
            sb.append("### `").append(field.getName()).append("`\n\n");

            sb.append("```java\n");
            sb.append(Modifier.toString(field.getModifiers())).append(" ")
                    .append(field.getType().getSimpleName()).append(" ")
                    .append(field.getName()).append(";\n");
            sb.append("```\n\n");

            String desc = field.isAnnotationPresent(DocumentedField.class)
                    ? field.getAnnotation(DocumentedField.class).description()
                    : "Sin descripción proporcionada.";
            sb.append("**Descripción:** ").append(desc).append("\n\n");
            sb.append("---\n\n");
        }

        sb.append("## Métodos\n\n");

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isSynthetic()) continue;

            sb.append("### `").append(method.getName()).append("`\n\n");

            sb.append("```java\n");
            sb.append(Modifier.toString(method.getModifiers())).append(" ")
                    .append(method.getReturnType().getSimpleName()).append(" ")
                    .append(method.getName()).append("(").append(formatParametersCode(method.getParameters())).append(");\n");
            sb.append("```\n\n");

            String desc = method.isAnnotationPresent(DocumentedMethod.class)
                    ? method.getAnnotation(DocumentedMethod.class).description() : "Sin descripción proporcionada.";
            sb.append("**Descripción:** ").append(desc).append("\n\n");

            if (method.getParameterCount() > 0) {
                sb.append("**Parámetros:**\n\n");
                sb.append("| Nombre | Tipo | Descripción |\n");
                sb.append("| :--- | :--- | :--- |\n");
                for (Parameter p : method.getParameters()) {
                    sb.append(String.format("| `%s` | `%s` | - |\n", p.getName(), p.getType().getSimpleName()));
                }
                sb.append("\n");
            }
            sb.append("**Retorno:** `").append(method.getReturnType().getSimpleName()).append("`\n\n");
            String accessorInfo = checkAccessorForField(method);
            if (!accessorInfo.equals("No")) {
                sb.append("- 💡 *Este método es un ").append(accessorInfo).append(".*\n");
            }
            if (isOverridden(method, clazz)) {
                sb.append("- 🔄 *Sobreescribe un método de la clase padre.*\n");
            }

            sb.append("\n---\n\n");
        }
        Path outputPath = Paths.get(clazz.getSimpleName() + "_Doc.md");
        Files.writeString(outputPath, sb.toString());
        System.out.println("✅ Documentación estilo Compodoc generada en: " + outputPath.toAbsolutePath());
    }

    private Set<String> getFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    private String formatParametersCode(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(p -> p.getType().getSimpleName() + " " + p.getName())
                .collect(Collectors.joining(", "));
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
            return String.format("%s de `%s`", type, fieldRealName);
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