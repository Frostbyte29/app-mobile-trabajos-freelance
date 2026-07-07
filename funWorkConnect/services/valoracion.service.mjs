import { randomUUID } from "crypto";
import * as repo from "../repositories/valoracion.repository.mjs";
import * as contratoRepo from "../repositories/contrato.repository.mjs";

export const crear = async (data) => {
  const { usuarioEmisorId, usuarioReceptorId } = data;
  const [comoFreelancer, comoContratante] = await Promise.all([
    contratoRepo.listarPorFreelancer(usuarioEmisorId, 100),
    contratoRepo.listarPorContratante(usuarioEmisorId, 100),
  ]);

  const todosLosContratos = [
    ...(comoFreelancer.Items || []),
    ...(comoContratante.Items || []),
  ];

  const vinculoFinalizado = todosLosContratos.some(
    (c) =>
      c.estado === "finalizado" &&
      (c.contratanteId === usuarioReceptorId || c.freelancerId === usuarioReceptorId)
  );

  if (!vinculoFinalizado) {
    throw new Error("Solo puedes valorar a personas con las que completaste un trabajo");
  }

  const nuevo = { ...data, id: randomUUID(), fechaCreacion: new Date().toISOString(), editada: false };
  await repo.crear(nuevo);
  return nuevo;
};

export const getPorId = async (id) => (await repo.getPorId(id)).Item;
export const eliminar = (id) => repo.eliminar(id);

export const actualizar = async (id, body) => {
  let updateExpression = "set #editada = :editada, #fechaEdicion = :fechaEdicion";
  const names = { "#editada": "editada", "#fechaEdicion": "fechaEdicion" };
  const values = { ":editada": true, ":fechaEdicion": new Date().toISOString() };

  Object.keys(body).forEach((key) => {
    updateExpression += `, #${key} = :${key}`;
    names[`#${key}`] = key;
    values[`:${key}`] = body[key];
  });

  const r = await repo.actualizar(id, updateExpression, names, values);
  return r.Attributes;
};

export const listarPorReceptor = async (usuarioReceptorId, limit = 15, lastKey) => {
  const r = await repo.listarPorReceptor(usuarioReceptorId, limit, lastKey);
  return { items: r.Items, nextKey: r.LastEvaluatedKey ? Buffer.from(JSON.stringify(r.LastEvaluatedKey)).toString("base64") : null };
};