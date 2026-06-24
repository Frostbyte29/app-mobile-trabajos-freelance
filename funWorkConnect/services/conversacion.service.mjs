import { randomUUID } from "crypto";
import * as repo from "../repositories/conversacion.repository.mjs";

export const crear = async (data) => {
  const nuevo = { ...data, id: randomUUID(), activa: true, fechaCreacion: new Date().toISOString() };
  await repo.crear(nuevo);
  await repo.crearPuntero(nuevo);
  return nuevo;
};

export const getPorId = async (id) => (await repo.getPorId(id)).Item;
export const cerrar = async (id) => { await repo.marcarInactiva(id); return { id, activa: false }; };
export const eliminar = (id) => repo.eliminarCompleta(id);

export const listarPorUsuario = async (usuarioId, limit = 15, lastKey) => {
  const r = await repo.listarPorUsuario(usuarioId, limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};