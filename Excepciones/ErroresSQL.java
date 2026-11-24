package Excepciones;

import java.sql.SQLException;

/*Traduce los errores técnicos de JDBC/H2 (SQLException) a excepciones propia, con un mensaje que el usuario sí puede entender.*/
public final class ErroresSQL {

    private ErroresSQL() {
    }

    public static FileyException traducir(SQLException e, String contexto) {
        switch (e.getErrorCode()) {
            case 23505: // llave primaria duplicada
                return new RegistroDuplicadoException("Ya existe un registro con ese ID.", e);
            case 23503: // se borra un padre que tiene hijos
                return new ReglaNegocioException(
                        "No se puede eliminar: hay reservaciones que dependen de este registro.", e);
            case 23506: // se inserta un hijo sin padre
                return new ReglaNegocioException("El stand o la editorial indicados no existen.", e);
            case 22001: // texto demasiado largo
                return new ReglaNegocioException("Alguno de los textos excede la longitud permitida.", e);
            default:
                return new FileyException("Error de base de datos al " + contexto + ": " + e.getMessage(), e);
        }
    }
}
