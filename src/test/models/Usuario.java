package test.models;

import com.generator.annotations.*;

@DocumentedClass(
        author = "Desarrollador Backend",
        description = "Entidad principal para la gestión de acceso en el sistema MVC. (Compatible con Java 25)",
        version = "1.0.0"
)
public class Usuario extends Persona {

    @DocumentedField(description = "Identificador único asignado al estudiante/usuario en la base de datos")
    private int student_id = 31544978;

    @DocumentedField(description = "Correo electrónico para inicio de sesión")
    public String email;

    // Constructor
    public Usuario(int student_id, String email) {
        this.student_id = student_id;
        this.email = email;
    }

    @DocumentedMethod(description = "Obtiene el identificador del usuario")
    public int getStudent_id() {
        return student_id;
    }

    @DocumentedMethod(description = "Actualiza el correo electrónico validando el formato")
    public void setEmail(String email, boolean forzarActualizacion) {
        this.email = email;
    }

    @Override
    public void mostrarDetalles() {
        System.out.println("Usuario ID: " + student_id);
    }
}