import * as projectController from "./controllers/project.controller.mjs";

import * as usuarioController from "./controllers/usuario.controller.mjs";
import * as empresaController from "./controllers/empresa.controller.mjs";
import * as categoriaController from "./controllers/categoria.controller.mjs";
import * as habilidadController from "./controllers/habilidad.controller.mjs";
import * as postulacionController from "./controllers/postulacion.controller.mjs";
import * as conversacionController from "./controllers/conversacion.controller.mjs";
import * as mensajeController from "./controllers/mensaje.controller.mjs";
import * as notificacionController from "./controllers/notificacion.controller.mjs";
import * as valoracionController from "./controllers/valoracion.controller.mjs";

const recursosGenericos = {
  usuarios: usuarioController,
  empresas: empresaController,
  categorias: categoriaController,
  habilidades: habilidadController,
  postulaciones: postulacionController,
  conversaciones: conversacionController,
  notificaciones: notificacionController,
  valoraciones: valoracionController,
};

export const handler = async (event) => {
  try {
    const method = event.requestContext.http.method;
    const partes = (event.rawPath || "/").split("/").filter(Boolean);
    const [recurso, id, sub] = partes;

    // ───────────────────────────────────────────────────────────────────────
    // RUTAS ESPECIALES: PROJECTS
    // ───────────────────────────────────────────────────────────────────────
    if (recurso === "projects") {
      if (method === "POST") return projectController.crearProject(event);
      if (method === "GET" && !id)
        return projectController.getProjects(event.queryStringParameters);
      if (method === "GET" && id) return projectController.getProject(id);
      if (method === "PUT" && id)
        return projectController.actualizarProject(id, event);
      if (method === "DELETE" && id)
        return projectController.eliminarProject(id);
      return {
        statusCode: 405,
        body: JSON.stringify({ message: "Método no permitido" }),
      };
    }

    // ───────────────────────────────────────────────────────────────────────
    // RUTAS ESPECIALES: POSTULACIONES (con estadísticas)
    // ───────────────────────────────────────────────────────────────────────
    if (recurso === "postulaciones") {
      // GET /postulaciones/estadisticas?candidatoId=xxx
      if (method === "GET" && id === "estadisticas") {
        return postulacionController.obtenerEstadisticas(
          event.queryStringParameters?.candidatoId
        );
      }
      
      // Rutas estándar de postulaciones
      if (method === "POST") return postulacionController.crear(event);
      if (method === "GET" && !id)
        return postulacionController.listar(event.queryStringParameters);
      if (method === "GET" && id) return postulacionController.getPorId(id);
      if (method === "PUT" && id) return postulacionController.actualizar(id, event);
      if (method === "DELETE" && id) return postulacionController.eliminar(id);
      
      return {
        statusCode: 405,
        body: JSON.stringify({ message: "Método no permitido" }),
      };
    }

    // ───────────────────────────────────────────────────────────────────────
    // RUTAS ESPECIALES: CONVERSACIONES CON MENSAJES
    // ───────────────────────────────────────────────────────────────────────
    if (recurso === "conversaciones" && id && sub === "mensajes") {
      if (method === "GET")
        return mensajeController.getMensajes(id, event.queryStringParameters);
      if (method === "POST") return mensajeController.crearMensaje(id, event);
      return {
        statusCode: 405,
        body: JSON.stringify({ message: "Método no permitido" }),
      };
    }

    // ───────────────────────────────────────────────────────────────────────
    // RUTAS GENÉRICAS (CRUD estándar)
    // ───────────────────────────────────────────────────────────────────────
    const controller = recursosGenericos[recurso];
    if (!controller) {
      return {
        statusCode: 404,
        body: JSON.stringify({ message: `Recurso no encontrado: ${recurso}` }),
      };
    }

    if (method === "POST") return controller.crear(event);
    if (method === "GET" && !id)
      return controller.listar(event.queryStringParameters);
    if (method === "GET" && id) return controller.getPorId(id);
    if (method === "PUT" && id) return controller.actualizar(id, event);
    if (method === "DELETE" && id) return controller.eliminar(id);

    return {
      statusCode: 405,
      body: JSON.stringify({ message: "Método no permitido" }),
    };
  } catch (error) {
    console.error("Unexpected Error:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({ message: "Internal Server Error" }),
    };
  }
};
