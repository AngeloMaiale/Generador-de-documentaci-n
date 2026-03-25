package test.models;
import com.generator.annotations.DocumentedClass;
import com.generator.annotations.DocumentedMethod;
import com.generator.annotations.*;
@DocumentedClass(author = "Estudiante CS", description = "Representa un usuario en el sistema CRUD", version = "2.5")
public class Usuario extends Persona {
    private String username;
    public int nivel;
    @DocumentedMethod(description = "Obtiene el nombre de usuario")
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    @Override
    public void saludar() {
        System.out.println("Hola, soy un usuario");
    }
}