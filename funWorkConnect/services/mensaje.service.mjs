import { randomUUID } from "crypto";
import * as repo from "../repositories/mensaje.repository.mjs";

export const crear = async (conversacionId, data) => {
  const nuevo = { ...data, id: randomUUID(), leido: false, fechaEnvio: new Date().toISOString() };
  await repo.crear(conversacionId, nuevo);
  return nuevo;
};

export const listarPorConversacion = async (conversacionId, limit = 30, lastKey) => {
  const r = await repo.listarPorConversacion(conversacionId, limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};
