package com.plataforma.Vapor.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private int codigo;
    private String mensaje;
    private String detalle;
}