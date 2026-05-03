package com.example.uniplanner.controller;

import com.example.uniplanner.model.usuarios;
import com.example.uniplanner.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("nombreUsuario")
    public String nombreUsuario(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            usuarios usuario = userDetails.getUsuario();
            return usuario.getNombre() + " " + usuario.getApellidos();
        }
        return "";
    }

    @ModelAttribute("nombre")
    public String nombre(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            usuarios usuario = userDetails.getUsuario();
            return usuario.getNombre();
        }
        return "";
    }

    @ModelAttribute("usuario")
    public usuarios usuario(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getUsuario();
        }
        return null;
    }
}
