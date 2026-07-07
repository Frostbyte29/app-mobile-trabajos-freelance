import { randomUUID } from "crypto";
import * as repo from "../repositories/contrato.repository.mjs";
import * as notificacionService from "../services/notificacion.service.mjs";

export const crear = async (data) => {
  const nuevo = {
    ...data,
    id: randomUUID(),
    estado: "en_curso",
    fechaInicio: new Date().toISOString(),
    fechaFin: null,
  };
  await repo.crear(nuevo);
  return nuevo;
};

export const getPorId = async (id) => {
  const result = await repo.getPorId(id);
  return result.Item;
};

export const finalizar = async (id) => {
  const contrato = await getPorId(id);

  if (!contrato) {
    throw new Error("Contrato no encontrado");
  }
  if (contrato.estado !== "en_curso") {
    throw new Error("El contrato no está en curso");
  }

  const fechaFin = new Date().toISOString();
  const r = await repo.actualizarEstado(id, "finalizado", fechaFin);

  try {
    await notificacionService.crear({
      usuarioId: contrato.freelancerId,
      titulo: "Trabajo finalizado",
      mensaje: `El contrato "${contrato.tituloOferta}" ha sido marcado como finalizado.`,
      tipo: "contrato_finalizado",
      referenciaId: id,
    });
  } catch (e) {
    console.error("No se pudo crear notificación:", e.message);
  }

  return r.Attributes;
};

export const listarPorFreelancer = async (freelancerId, limit = 20, lastKey) => {
  const r = await repo.listarPorFreelancer(freelancerId, limit, lastKey);
  return {
    items: r.Items || [],
    nextKey: r.LastEvaluatedKey
      ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64")
      : null,
  };
};

export const listarPorContratante = async (contratanteId, limit = 20, lastKey) => {
  const r = await repo.listarPorContratante(contratanteId, limit, lastKey);
  return {
    items: r.Items || [],
    nextKey: r.LastEvaluatedKey
      ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64")
      : null,
  };
};
