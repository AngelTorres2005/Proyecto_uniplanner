package com.example.uniplanner.controller;

import com.example.uniplanner.model.preguntas;
import com.example.uniplanner.repository.preguntasRepository;
import com.example.uniplanner.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.Optional;
import com.example.uniplanner.model.usuarios;
import com.example.uniplanner.repository.usuarioRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private usuarioRepository usuarioRepository;
    @Autowired
    private preguntasRepository preguntasRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            usuarios usuario = userDetails.getUsuario();
            model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellidos());
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("usuario", usuario);
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPath(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("alertaError", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("alertaSuccess", "Sesión cerrada correctamente");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/recuperar")
    public String recuperar() {
        return "recuperar";
    }

    @GetMapping("/recuperar-2")
    public String recuperar2(){ return "recuperar-2";}

    @GetMapping("/preguntas")
    public String preguntas() {
        return "preguntas";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @PostMapping("/api/registro")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String correo,
            @RequestParam String password,
            @RequestParam String confirmarPassword,
            RedirectAttributes redirectAttributes
            ) {
        try {
            // validaciones hasta ahora
            if (!password.equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("alertaError", "Las contraseñas no coinciden");
                return "redirect:/registro";
            }

            if (password.length() < 8) {
                redirectAttributes.addFlashAttribute("alertaError", "La contraseña debe tener mínimo 8 caracteres");
                return "redirect:/registro";
            }

            if (usuarioRepository.findByCorreo(correo).isPresent()) {
                redirectAttributes.addFlashAttribute("alertaError", "El correo ya está registrado");
                return "redirect:/registro";
            }

            // encriptar la password
            String passwordEncriptada = passwordEncoder.encode(password);

            // creando el nuevo usuario
            usuarios nuevoUsuario = new usuarios();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellidos(apellidos);
            nuevoUsuario.setCorreo(correo);
            nuevoUsuario.setPassword_hash(passwordEncriptada);
            nuevoUsuario.setId_rol(1);
            nuevoUsuario.setFechaAlta(LocalDate.now());
            nuevoUsuario.setFecha_modificacion(LocalDate.now());
            nuevoUsuario.setEstado("Activo");

            // se guarda el usuario en la base de datos
            usuarios usuarioGuardado = usuarioRepository.save(nuevoUsuario);

            redirectAttributes.addFlashAttribute("alertaSuccess", "Registro exitoso. Por favor inicia sesión para continuar.");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    @PostMapping("/api/preguntas")
    public String registrarPreguntas(
            @RequestParam String respuesta1,
            @RequestParam String respuesta2,
            @RequestParam String respuesta3, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            Integer id = userDetails.getIdUsuario();

            if (id == null) {
                return "redirect:/registro";
            }
            //datos que van a la tabla preguntas
            preguntas nuevasPreguntas = new preguntas();
            nuevasPreguntas.setId_usuario(id);
            nuevasPreguntas.setPregunta1(respuesta1);
            nuevasPreguntas.setPregunta2(respuesta2);
            nuevasPreguntas.setPregunta3(respuesta3);

            //insertar datos en la tabla preguntas, tambien el id del usuario que acaba de crear su cuenta
            preguntas preguntasGuardadas = preguntasRepository.save(nuevasPreguntas);

            return "redirect:/index";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al registrar preguntas :" + e.getMessage());
            return "preguntas";
        }
    }


    @PostMapping("/api/recuperar")
    public String recuperarPass(
            @RequestParam String email,
            @RequestParam String preguntaId,
            @RequestParam String respuesta,
            HttpSession session,
            RedirectAttributes redirectAttributes){

        Optional<usuarios> usuarioOpt = usuarioRepository.findByCorreo(email);

        if(usuarioOpt.isPresent()){
            usuarios usuario = usuarioOpt.get();
            Optional<preguntas> preguntasOpt = preguntasRepository.findById_usuario(usuario.getId_usuario());

            if(preguntasOpt.isPresent()){
                preguntas p = preguntasOpt.get();
                String respuestaCorrecta = "";

                switch (preguntaId){
                    case "1": respuestaCorrecta = p.getPregunta1(); break;
                    case "2": respuestaCorrecta = p.getPregunta2(); break;
                    case "3": respuestaCorrecta = p.getPregunta3(); break;
                }
                if(respuesta.trim().equalsIgnoreCase(respuestaCorrecta)){
                    session.setAttribute("idUsuarioRecuperar", usuario.getId_usuario());
                    return "redirect:/recuperar-2";
                }
            }
        }

        redirectAttributes.addFlashAttribute("alertaError", "Los datos no coinciden con nuestros registros");
        return "redirect:/recuperar";
    }

    @PostMapping("/api/recuperar/actualizar")
    public String actualizarpass(
            @RequestParam String nuevaPassword,
            @RequestParam String confirmarPassword,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuarioRecuperar");

        if (idUsuario == null) {
            redirectAttributes.addFlashAttribute("alertaError", "La sesión ha expirado, por favor intenta de nuevo");
            return "redirect:/recuperar";
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("alertaError", "Las contraseñas no coinciden");
            return "redirect:/recuperar-2";
        }
        if (nuevaPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("alertaError", "La contraseña debe tener mínimo 8 caracteres");
            return "redirect:/recuperar-2";
        }
        if (nuevaPassword.length() > 8) {
            redirectAttributes.addFlashAttribute("alertaError", "La contraseña debe tener maximo 8 caracteres");
            return "redirect:/recuperar-2";
        }

        String passwordHash = passwordEncoder.encode(nuevaPassword);

        Optional<usuarios> usuarioOpt = usuarioRepository.findById(idUsuario);
        usuarios u = usuarioOpt.get();
        u.setPassword_hash(passwordHash);
        usuarioRepository.save(u);

        session.removeAttribute("idUsuarioRecuperar");

        redirectAttributes.addFlashAttribute("alertaSuccess", "La contraseña se actualizó correctamente");
        return "redirect:/login";
    }
}