package com.example.uniplanner.controller;

import com.example.uniplanner.model.*;
import com.example.uniplanner.repository.*;
import com.example.uniplanner.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private materiasRepository materiasRepository;
    @Autowired
    private horariosRepository horariosRepository;

    @Autowired
    private tareasRepository tareasRepository;

    @Autowired
    private prioridadRepository prioridadRepository;

    @Autowired
    private usuarioRepository usuarioRepository;


    @PostMapping("/agregarMateria")
    public String agregarMateria(
            @RequestParam String nombreMateria,
            @RequestParam String colorMateria,
            @RequestParam List<String> dia,
            @RequestParam List<String> horaInicio,
            @RequestParam List<String> horaFinal,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ){
        try {
            usuarios usuarioActual = userDetails.getUsuario();
            Integer idUsuario = userDetails.getIdUsuario();

            // --- VALIDACIÓN DE CHOQUES ---
            for (int i = 0; i < dia.size(); i++) {
                LocalTime inicio = LocalTime.parse(horaInicio.get(i));
                LocalTime fin = LocalTime.parse(horaFinal.get(i));
                String diaActual = dia.get(i);

                long choques = horariosRepository.countChoques(idUsuario, diaActual, inicio, fin);

                if (choques > 0) {
                    redirectAttributes.addFlashAttribute("alertaError",
                            "¡Choque de horario! Ya tienes una clase el " + diaActual + " a esa hora.");
                    return "redirect:/index";
                }
            }


            materias nuevaMateria = new materias();
            nuevaMateria.setUsuario(usuarioActual);
            nuevaMateria.setNombre(nombreMateria);
            nuevaMateria.setColor(colorMateria);

            materias materiaGuardada = materiasRepository.save(nuevaMateria);

            for (int i = 0; i < dia.size(); i++) {
                horarios nuevoHorario = new horarios();
                nuevoHorario.setMateria(materiaGuardada);
                nuevoHorario.setDia(dia.get(i));
                nuevoHorario.setHora_inicio(LocalTime.parse(horaInicio.get(i)));
                nuevoHorario.setHora_final(LocalTime.parse(horaFinal.get(i)));
                horariosRepository.save(nuevoHorario);
            }

            redirectAttributes.addFlashAttribute("alertaSuccess", "Materia guardada correctamente");
            return "redirect:/index";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("alertaError", "Error al procesar la solicitud");
            return "redirect:/index";
        }
    }

    @GetMapping("/selectMaterias")
    @ResponseBody
    public List<materias> selectMaterias(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer idUsuario = userDetails.getIdUsuario();
        return materiasRepository.findByUsuario(idUsuario);
    }

    @PostMapping("/crearTarea")
    public String crearTarea(
            @RequestParam String titulo,
            @RequestParam Integer prioridad,
            @RequestParam Integer materia,
            @RequestParam String descripcion,
            @RequestParam LocalDate fechaEntrega,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ){
        try{
            usuarios usuarioActual = userDetails.getUsuario();

            // PROXY PARA QUE SEA INT
            materias materiaProxy = materiasRepository.getReferenceById(materia);
            prioridad prioridadProxy = prioridadRepository.getReferenceById(prioridad);

            tareas nuevaTarea = new tareas();
            nuevaTarea.setMateria(materiaProxy);
            nuevaTarea.setPrioridad(prioridadProxy);

            nuevaTarea.setUsuario(usuarioActual);
            nuevaTarea.setTitulo(titulo);
            nuevaTarea.setDescripcion(descripcion);
            nuevaTarea.setFecha_creacion(LocalDate.now());
            nuevaTarea.setFecha_entrega(fechaEntrega);

            tareasRepository.save(nuevaTarea);
            redirectAttributes.addFlashAttribute("alertaSuccess", "Tarea guardada con éxito");
            return "redirect:/index";

        } catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("alertaError","Error al guardar la tarea, favor de reintentar");
            return "redirect:/index";
        }
    }

    @GetMapping("/tareasContainer")
    @ResponseBody
    public List<tareas> tareasContainer(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Integer idUsuario = userDetails.getIdUsuario();
        List<tareas> lista = tareasRepository.buscarTareasPendientes(idUsuario);
        return lista;
    }

    @GetMapping("/datos/{id}")
    @ResponseBody
    public tareas obtenerDetalleTarea(@PathVariable("id") Integer id) {
        return tareasRepository.findById(id).orElse(null);
    }

    @GetMapping("/datosMateria/{id}")
    @ResponseBody
    public materias obtenerDetalleMateria(@PathVariable("id") Integer id){
        return materiasRepository.findById(id).orElse(null);
    }
    
    @PostMapping("/actualizarTarea")
    public String actualizarTarea(
            @RequestParam Integer id_tarea,
            @RequestParam String titulo,
            @RequestParam Integer prioridad,
            @RequestParam LocalDate fechaEntrega,
            @RequestParam Integer materia,
            @RequestParam String descripcion,
            @AuthenticationPrincipal CustomUserDetails userDetails, 
            RedirectAttributes redirectAttributes
            ){
        prioridad prioridadProxy = prioridadRepository.getReferenceById(prioridad);
        materias materiaProxy = materiasRepository.getReferenceById(materia);

        Optional<tareas> tareasOpt = tareasRepository.findById(id_tarea);
        tareas t = tareasOpt.get();
        t.setTitulo(titulo);
        t.setPrioridad(prioridadProxy);
        t.setMateria(materiaProxy);
        t.setFecha_entrega(fechaEntrega);
        t.setDescripcion(descripcion);
        tareasRepository.save(t);

        redirectAttributes.addFlashAttribute("alertaSuccess", "Tarea actualizada correctamente");
        return "redirect:/index";
    }

    @GetMapping("/materiasContainer")
    @ResponseBody
    public List<materias> materiasContainer(@AuthenticationPrincipal CustomUserDetails userDetails){
        return materiasRepository.findByUsuario(userDetails.getIdUsuario());
    }

    @PostMapping("/entregarTarea/{id}")
    @ResponseBody
    public String entregarTarea(@PathVariable("id") Integer id) {
        try{
            Optional<tareas> tareasOpt = tareasRepository.findById(id);
            tareas tarea = tareasOpt.get();

            String estatus = "Entregada";
            tarea.setFecha_entregada(LocalDate.now());
            tarea.setEstatus(estatus);
            tareasRepository.save(tarea);
            return "success";
        }catch(Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/ultimas2Tareas")
    @ResponseBody
    public List<tareas> ultimas2Tareas(@AuthenticationPrincipal CustomUserDetails userDetails){
        return tareasRepository.buscarUltimas2Tareas(userDetails.getIdUsuario());
    }

    @GetMapping("/tareasProximas")
    @ResponseBody
    public List<tareas> tareasProximas(@AuthenticationPrincipal CustomUserDetails userDetails){
        return tareasRepository.buscar3TareasAVencer(userDetails.getIdUsuario());
    }

    @PostMapping("/eliminarMateria/{id}")
    @ResponseBody
    public String eliminarMateria(@PathVariable("id") Integer id, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {

            Optional<materias> materiaOpt = materiasRepository.findById(id);

            if (materiaOpt.isPresent() && materiaOpt.get().getUsuario().getId_usuario() == userDetails.getIdUsuario()) {
                materiasRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("alertaSuccess" ,"Materia eliminada correctamente");
                return "success";
            }
            redirectAttributes.addFlashAttribute("alertaError", "Error al eliminar");
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("alertaError", "Error al eliminar en el servidor");
            return "redirect:/index";
        }
    }

    @GetMapping("/materiasHorario")
    @ResponseBody
    public List<horarios> materiasHorario(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer id_usuario = customUserDetails.getIdUsuario();
        return horariosRepository.findByUsuario(id_usuario);
    }

    @PostMapping("/editarMateria")
    public String editarMateria(
            @RequestParam Integer id_materia,
            @RequestParam String nombre,
            @RequestParam String color,
            RedirectAttributes redirectAttributes){
        try{
            materias materia = materiasRepository.findById(id_materia).orElse(null);
            materia.setNombre(nombre);
            materia.setColor(color);
            materiasRepository.save(materia);
            redirectAttributes.addFlashAttribute("alertaSuccess","Materia editada correctamente");
            return "redirect:/index";
        }catch(Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("alertaError","Error al editar la materia");
            return "redirect:/index";
        }
    }

    @GetMapping("/datosPerfil")
    @ResponseBody
    public usuarios datosPerfil(@AuthenticationPrincipal CustomUserDetails userDetails){
        return userDetails.getUsuario();
    }

    @PostMapping("/editarPerfil")
    public String editarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes){
        try{
            usuarios usuario = userDetails.getUsuario();
            usuario.setNombre(nombre);
            usuario.setApellidos(apellidos);
            usuario.setFecha_modificacion(LocalDate.now());
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("alertaSuccess","Perfil actualizado correctamente");
            return "redirect:/perfil";
        }catch(Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("alertaError","Error al actualizar el perfil");
            return "redirect:/perfil";
        }
    }

    @GetMapping("/tareasRecordatorio")
    @ResponseBody
    public List<tareas> tareasRecordatorio(@AuthenticationPrincipal CustomUserDetails userDetails){
        LocalDate manana = LocalDate.now().plusDays(1);
        Integer id = userDetails.getIdUsuario();
        return tareasRepository.buscarTareasParaRecordatorioID(manana, id);
    }

}//fin de la clase indexController