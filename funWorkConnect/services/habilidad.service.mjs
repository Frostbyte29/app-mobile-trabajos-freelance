import { randomUUID } from "crypto";
import * as repo from "../repositories/habilidad.repository.mjs";

export const crear = async (data) => {
  const nuevo = { ...data, id: randomUUID() };
  await repo.crear(nuevo);
  return nuevo;
};

export const getPorId = async (id) => (await repo.getPorId(id)).Item;
export const eliminar = (id) => repo.eliminar(id);

export const actualizar = async (id, body) => {
  let updateExpression = "set ";
  const names = {}, values = {};
  const keys = Object.keys(body);
  keys.forEach((key, i) => {
    updateExpression += `#${key} = :${key}` + (i < keys.length - 1 ? ", " : "");
    names[`#${key}`] = key;
    values[`:${key}`] = body[key];
  });
  const r = await repo.actualizar(id, updateExpression, names, values);
  return r.Attributes;
};

export const listar = async (limit = 20, lastKey) => {
  const r = await repo.listar(limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};
