import { randomUUID } from "crypto";
import * as repo from "../repositories/postulacion.repository.mjs";

export const crear = async (data) => {
  const nuevo = {
    ...data, id: randomUUID(), estado: "postulado",
    fechaPostulacion: new Date().toISOString(), fechaActualizacion: new Date().toISOString(),
  };
  await repo.crear(nuevo);
  return nuevo;
};

export const getPorId = async (id) => (await repo.getPorId(id)).Item;
export const eliminar = (id) => repo.eliminar(id);

export const actualizarEstado = async (id, estado) => {
  const r = await repo.actualizar(
    id, "set #estado = :estado, #fechaActualizacion = :fecha",
    { "#estado": "estado", "#fechaActualizacion": "fechaActualizacion" },
    { ":estado": estado, ":fecha": new Date().toISOString() }
  );
  return r.Attributes;
};

export const listarPorCandidato = async (candidatoId, limit = 10, lastKey) => {
  const r = await repo.listarPorCandidato(candidatoId, limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};

export const listarPorVacante = async (vacanteId, limit = 10, lastKey) => {
  const r = await repo.listarPorVacante(vacanteId, limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};
