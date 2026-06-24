import { randomUUID } from "crypto";
import * as repo from "../repositories/notificacion.repository.mjs";

export const crear = async (data) => {
  const nuevo = { ...data, id: randomUUID(), leida: false, fechaCreacion: new Date().toISOString() };
  await repo.crear(nuevo);
  return nuevo;
};

export const getPorId = async (id) => (await repo.getPorId(id)).Item;
export const eliminar = (id) => repo.eliminar(id);
export const marcarLeida = async (id) => (await repo.marcarLeida(id)).Attributes;

export const listarPorUsuario = async (usuarioId, limit = 15, lastKey) => {
  const r = await repo.listarPorUsuario(usuarioId, limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};
