package Datos;

import java.time.LocalDate;

public class Reservacion {

    private String id;
    private String standId;
    private String editorialId;
    private LocalDate fechaReserva;
    private LocalDate fechaVigencia;
    private boolean activa;

    public Reservacion(
            String id,
            String standId,
            String editorialId,
            LocalDate fechaReserva,
            LocalDate fechaVigencia,
            boolean activa) {

        this.id = id;
        this.standId = standId;
        this.editorialId = editorialId;
        this.fechaReserva = fechaReserva;
        this.fechaVigencia = fechaVigencia;
        this.activa = activa;
    }

    public String getId() {
        return id;
    }

    public String getStandId() {
        return standId;
    }

    public String getEditorialId() {
        return editorialId;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public LocalDate getFechaVigencia() {
        return fechaVigencia;
    }

    public boolean isActiva() {
        return activa;
    }
}
