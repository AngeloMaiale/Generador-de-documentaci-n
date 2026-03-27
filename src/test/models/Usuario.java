package test.models;

import com.generator.annotations.*;

@DocumentedClass(
        author = "Estudiante de Ingeniería",
        description = "Clase que representa un Usuario final con permisos de acceso y herencia de Persona.",
        version = "1.2.0"
)
public class Usuario extends Persona {

    @DocumentedField(description = "Identificador numérico único de la base de datos.")
    private int userId;

    @DocumentedField(description = "Nombre completo de visualización.")
    public String nombre;

    @DocumentedField(description = "Campo estático de prueba.")
    public static final String ROL_DEFECTO = "GUEST";

    public Usuario() { }

    public Usuario(int userId, String nombre) {
        this.userId = userId;
        this.nombre = nombre;
    }

    @DocumentedMethod(description = "Devuelve el ID único.")
    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    @Override
    @DocumentedMethod(description = "Imprime los datos específicos del Usuario.")
    public void mostrarDetalles() {
        System.out.println("Usuario: " + nombre + " (ID: " + userId + ")");
    }
    @Override
    protected String obtenerTipoEntidad() {
        return "Usuario";
    }
}