package com.generator.core;

import com.generator.annotations.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DocGenerator {

    public void generateMarkdown(Class<?> clazz) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("# Documentación de la Clase: `").append(clazz.getSimpleName()).append("`\n\n");

        if (clazz.isAnnotationPresent(DocumentedClass.class)) {
            DocumentedClass ann = clazz.getAnnotation(DocumentedClass.class);
            sb.append("**Metadatos de la Clase**\n");
            sb.append("- **Nombre:** ").append(clazz.getName()).append("\n");
            sb.append("- **Autor:** ").append(ann.author()).append("\n");
            sb.append("- **Descripción:** ").append(ann.description()).append("\n");
            sb.append("- **Versión:** ").append(ann.version()).append("\n");
        }

        boolean isSubclass = !clazz.getSuperclass().equals(Object.class);
        sb.append("- **Es Subclase:** ").append(isSubclass ? "Sí (Hereda de " + clazz.getSuperclass().getSimpleName() + ")" : "No").append("\n\n");
        sb.append("## Atributos\n\n");
        sb.append("| Tipo | Nombre | Modificadores | Descripción |\n");
        sb.append("| :--- | :--- | :--- | :--- |\n");
        for (Field field : clazz.getDeclaredFields()) {
            String desc = field.isAnnotationPresent(DocumentedField.class)
                    ? field.getAnnotation(DocumentedField.class).description()
                    : "N/A";

            sb.append(String.format("| `%s` | **%s** | `%s` | %s |\n",
                    field.getType().getSimpleName(),
                    field.getName(),
                    Modifier.toString(field.getModifiers()),
                    desc));
        }
        sb.append("\n");
        sb.append("## Métodos y Constructores\n\n");

        for (Constructor<?> cons : clazz.getDeclaredConstructors()) {
            sb.append("### `").append(clazz.getSimpleName()).append("()`\n");
            sb.append("- **Descripción:** Constructor de la clase.\n");
            sb.append("- **Parámetros:** ").append(formatParameters(cons.getParameters())).append("\n");
            sb.append("- **Tipo de retorno:** N/A\n");
            sb.append("- **Modificadores:** `").append(Modifier.toString(cons.getModifiers())).append("`\n");
            sb.append("- **¿Es Getter/Setter?:** No\n");
            sb.append("- **¿Es Constructor?:** Sí\n");
            sb.append("- **¿Es Sobreescrito?:** No\n\n");
        }

        for (Method method : clazz.getDeclaredMethods()) {
            sb.append("### `").append(method.getName()).append("()`\n");

            String desc = method.isAnnotationPresent(DocumentedMethod.class)
                    ? method.getAnnotation(DocumentedMethod.class).description() : "N/A";

            sb.append("- **Descripción:** ").append(desc).append("\n");
            sb.append("- **Parámetros:** ").append(formatParameters(method.getParameters())).append("\n");
            sb.append("- **Tipo de retorno:** `").append(method.getReturnType().getSimpleName()).append("`\n");
            sb.append("- **Modificadores:** `").append(Modifier.toString(method.getModifiers())).append("`\n");
            sb.append("- **¿Es Getter/Setter?:** ").append(isAccessor(method) ? "Sí" : "No").append("\n");
            sb.append("- **¿Es Constructor?:** No\n");
            sb.append("- **¿Es Sobreescrito?:** ").append(isOverridden(method, clazz) ? "Sí" : "No").append("\n\n");
        }

        Path outputPath = Paths.get(clazz.getSimpleName() + "_Doc.md");
        Files.writeString(outputPath, sb.toString());
        System.out.println("Documentación generada en: " + outputPath.toAbsolutePath());
    }

    private String formatParameters(Parameter[] parameters) {
        if (parameters.length == 0) return "Ninguno";
        return Arrays.stream(parameters)
                .map(p -> "`" + p.getType().getSimpleName() + " " + p.getName() + "`")
                .collect(Collectors.joining(", "));
    }

    private boolean isAccessor(Method m) {
        String name = m.getName();
        return (name.startsWith("get") || name.startsWith("set") || name.startsWith("is")) && !name.equals("getClass");
    }

    private boolean isOverridden(Method method, Class<?> clazz) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass == null || superClass.equals(Object.class)) return false;
        try {
            superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}