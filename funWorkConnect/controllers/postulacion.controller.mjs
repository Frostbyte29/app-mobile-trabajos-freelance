import * as service from "../services/postulacion.service.mjs";
import { postulacionSchema, estadoPostulacionSchema } from "../validators/postulacion.schema.mjs";
import { ok, created, noContent, badRequest, notFound, conflict, internalError } from "../utils/response.mjs";

export const crear = async (event) => {
  try {
    const parsed = postulacionSchema.safeParse(JSON.parse(event.body));
    
    if (!parsed.success) {
      return badRequest({
        message: "Datos inválidos",
        errors: parsed.error.flatten().fieldErrors
      });
    }

    const postulacion = await service.crear(parsed.data);
    return created(postulacion);
  } catch (error) {
    if (error.message.includes("Ya has postulado")) {
      return conflict({ message: error.message });
    }
    console.error("Error al crear postulación:", error);
    return internalError({ message: "Error al crear la postulación" });
  }
};

export const getPorId = async (id) => {
  try {
    const postulacion = await service.getPorId(id);
    if (!postulacion) {
      return notFound({ message: "Postulación no encontrada" });
    }
    return ok(postulacion);
  } catch (error) {
    console.error("Error al obtener postulación:", error);
    return internalError({ message: "Error al obtener la postulación" });
  }
};

export const actualizar = async (id, event) => {
  try {
    const parsed = estadoPostulacionSchema.safeParse(JSON.parse(event.body));
    
    if (!parsed.success) {
      return badRequest({
        message: "Datos inválidos",
        errors: parsed.error.flatten().fieldErrors
      });
    }

    const postulacionActualizada = await service.actualizarEstado(
      id,
      parsed.data.estado,
      parsed.data.comentario
    );
    
    return ok(postulacionActualizada);
  } catch (error) {
    if (error.message.includes("no encontrada")) {
      return notFound({ message: error.message });
    }
    console.error("Error al actualizar postulación:", error);
    return internalError({ message: "Error al actualizar la postulación" });
  }
};

export const eliminar = async (id) => {
  try {
    await service.eliminar(id);
    return noContent();
  } catch (error) {
    console.error("Error al eliminar postulación:", error);
    return internalError({ message: "Error al eliminar la postulación" });
  }
};

export const listar = async (query) => {
  try {
    const limit = query?.limit ? Math.min(parseInt(query.limit), 100) : 20;

    if (query?.candidatoId) {
      const resultado = await service.listarPorCandidato(
        query.candidatoId,
        limit,
        query.lastKey
      );
      return ok(resultado);
    }

    if (query?.vacanteId) {
      const resultado = await service.listarPorVacante(
        query.vacanteId,
        limit,
        query.lastKey
      );
      return ok(resultado);
    }

    return badRequest({
      message: "Debes proporcionar candidatoId o vacanteId como parámetro de consulta"
    });
  } catch (error) {
    console.error("Error al listar postulaciones:", error);
    return internalError({ message: "Error al listar las postulaciones" });
  }
};

export const obtenerEstadisticas = async (candidatoId) => {
  try {
    if (!candidatoId) {
      return badRequest({ message: "Se requiere el ID del candidato" });
    }

    const estadisticas = await service.obtenerEstadisticasCandidato(candidatoId);
    return ok(estadisticas);
  } catch (error) {
    console.error("Error al obtener estadísticas:", error);
    return internalError({ message: "Error al obtener estadísticas" });
  }
};
