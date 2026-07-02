import { randomUUID } from "crypto";
import * as repo from "../repositories/postulacion.repository.mjs";
import * as notificacionService from "../services/notificacion.service.mjs";

// Textos de notificación según el nuevo estado
const textoNotificacion = (estado) => {
  switch (estado) {
    case "en_revision": return { titulo: "Tu postulación está en revisión 🔍", mensaje: "Un reclutador está revisando tu postulación." };
    case "aceptado":    return { titulo: "¡Felicitaciones! Fuiste aceptado 🎉", mensaje: "Tu postulación fue aceptada. Revisa los detalles en Mi Actividad." };
    case "rechazado":   return { titulo: "Postulación no seleccionada", mensaje: "Tu postulación no fue seleccionada esta vez. ¡Sigue intentando!" };
    default:            return { titulo: "Estado de postulación actualizado", mensaje: `Tu postulación cambió a: ${estado}` };
  }
};

// Crear una nueva postulación con validaciones de negocio
export const crear = async (data) => {
  // Validar que no exista una postulación previa del mismo candidato a la misma vacante
  const postulacionesExistentes = await repo.listarPorCandidato(data.candidatoId, 100);
  const yaPostulo = postulacionesExistentes.Items?.some(p => p.vacanteId === data.vacanteId);
  
  if (yaPostulo) {
    throw new Error("Ya has postulado a esta oferta anteriormente");
  }

  const nuevo = {
    ...data,
    id: randomUUID(),
    estado: "postulado",
    fechaPostulacion: new Date().toISOString(),
    fechaActualizacion: new Date().toISOString(),
    // Asegurar que mensajePresentacion tenga un valor
    mensajePresentacion: data.mensajePresentacion || "",
    cvUrl: data.cvUrl || null
  };
  
  await repo.crear(nuevo);
  return nuevo;
};

export const getPorId = async (id) => {
  const result = await repo.getPorId(id);
  return result.Item;
};

export const eliminar = (id) => repo.eliminar(id);

// Actualizar el estado de una postulación con comentario opcional
export const actualizarEstado = async (id, estado, comentario = null) => {
  // Verificar que la postulación existe antes de actualizar
  const postulacionActual = await getPorId(id);
  if (!postulacionActual) {
    throw new Error("Postulación no encontrada");
  }

  // Construir la expresión de actualización dinámicamente
  let updateExpression = "set #estado = :estado, #fechaActualizacion = :fecha";
  const names = {
    "#estado": "estado",
    "#fechaActualizacion": "fechaActualizacion"
  };
  const values = {
    ":estado": estado,
    ":fecha": new Date().toISOString()
  };

  // Añadir comentario si se proporciona
  if (comentario) {
    updateExpression += ", #comentarioEstado = :comentario";
    names["#comentarioEstado"] = "comentarioEstado";
    values[":comentario"] = comentario;
  }

  const r = await repo.actualizar(id, updateExpression, names, values);

  // Notificar al candidato sobre el cambio de estado
  try {
    const { titulo, mensaje } = textoNotificacion(estado);
    await notificacionService.crear({
      usuarioId: postulacionActual.candidatoId,
      titulo,
      mensaje,
      tipo: "estado_postulacion",
      referenciaId: id
    });
  } catch (e) {
    console.error("No se pudo crear notificación:", e.message);
  }

  return r.Attributes;
};

export const listarPorCandidato = async (candidatoId, limit = 20, lastKey) => {
  const r = await repo.listarPorCandidato(candidatoId, limit, lastKey);
  return {
    items: r.Items || [],
    nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null,
    total: r.Items?.length || 0
  };
};

export const listarPorVacante = async (vacanteId, limit = 20, lastKey) => {
  const r = await repo.listarPorVacante(vacanteId, limit, lastKey);
  
  // Calcular estadísticas de la vacante
  const items = r.Items || [];
  const estadisticas = {
    total: items.length,
    postulados: items.filter(p => p.estado === "postulado").length,
    enRevision: items.filter(p => p.estado === "en_revision").length,
    aceptados: items.filter(p => p.estado === "aceptado").length,
    rechazados: items.filter(p => p.estado === "rechazado").length
  };

  return {
    items,
    nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null,
    estadisticas
  };
};

// Obtener estadísticas globales de un candidato
export const obtenerEstadisticasCandidato = async (candidatoId) => {
  const r = await repo.listarPorCandidato(candidatoId, 100);
  const items = r.Items || [];
  
  return {
    totalPostulaciones: items.length,
    postulados: items.filter(p => p.estado === "postulado").length,
    enRevision: items.filter(p => p.estado === "en_revision").length,
    aceptados: items.filter(p => p.estado === "aceptado").length,
    rechazados: items.filter(p => p.estado === "rechazado").length,
    tasaExito: items.length > 0 
      ? ((items.filter(p => p.estado === "aceptado").length / items.length) * 100).toFixed(1)
      : 0
  };
};
