import { randomUUID } from "crypto";
import * as repo from "../repositories/postulacion.repository.mjs";
import * as notificacionService from "../services/notificacion.service.mjs";
import * as projectService from "../services/project.service.mjs";
import * as contratoService from "../services/contrato.service.mjs";

const textoNotificacion = (estado) => {
  switch (estado) {
    case "en_revision": return { titulo: "Tu postulación está en revisión", mensaje: "Un reclutador está revisando tu postulación." };
    case "aceptado":    return { titulo: "Fuiste aceptado", mensaje: "Tu postulación fue aceptada. Revisa los detalles en Mi Actividad." };
    case "rechazado":   return { titulo: "Postulación no seleccionada", mensaje: "Tu postulación no fue seleccionada esta vez. ¡Sigue intentando!" };
    default:            return { titulo: "Estado de postulación actualizado", mensaje: `Tu postulación cambió a: ${estado}` };
  }
};

export const crear = async (data) => {
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

export const actualizarEstado = async (id, estado, comentario = null) => {
  const postulacionActual = await getPorId(id);
  if (!postulacionActual) {
    throw new Error("Postulación no encontrada");
  }

  let updateExpression = "set #estado = :estado, #fechaActualizacion = :fecha";
  const names = {
    "#estado": "estado",
    "#fechaActualizacion": "fechaActualizacion"
  };
  const values = {
    ":estado": estado,
    ":fecha": new Date().toISOString()
  };

  if (comentario) {
    updateExpression += ", #comentarioEstado = :comentario";
    names["#comentarioEstado"] = "comentarioEstado";
    values[":comentario"] = comentario;
  }

  const r = await repo.actualizar(id, updateExpression, names, values);

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

  if (estado === "aceptado" && postulacionActual.estado !== "aceptado") {
    try {
      const proyecto = await projectService.getById(postulacionActual.vacanteId);
      if (proyecto?.creadoPorId) {
        await contratoService.crear({
          contratanteId: proyecto.creadoPorId,
          freelancerId: postulacionActual.candidatoId,
          ofertaId: postulacionActual.vacanteId,
          tituloOferta: proyecto.titulo,
          tipoOrigen: "trabajo",
          postulacionId: id,
        });
      }
    } catch (e) {
      console.error("No se pudo crear contrato automático:", e.message);
    }
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
