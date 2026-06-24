import { randomUUID } from "crypto";
import * as repo from "../repositories/project.repository.mjs";

export const crear = async (data) => {
  const newProject = {
    ...data,
    id: randomUUID(),
    createdAt: new Date().toISOString(),
  };
  await repo.crearProject(newProject);
  return newProject;
};

export const getById = async (id) => {
  const result = await repo.getProjectById(id);
  return result.Item;
};

export const eliminar = async (id) => {
  await repo.eliminarProject(id);
};

export const actualizar = async (id, body) => {
  let updateExpression = "set ";
  let names = {};
  let values = {};

  const keys = Object.keys(body);

  keys.forEach((key, index) => {
    updateExpression += `#${key} = :${key}`;
    if (index < keys.length - 1) updateExpression += ", ";
    names[`#${key}`] = key;
    values[`:${key}`] = body[key];
  });

  const result = await repo.actualizarProject(id, updateExpression, names, values);
  return result.Attributes;
};

export const listar = async (limit = 10, lastKey) => {
  const result = await repo.getProjects(limit, lastKey);

  return {
    items: result.Items,
    nextKey: result.LastEvaluatedKey
      ? Buffer.from(JSON.stringify(result.LastEvaluatedKey)).toString("base64")
      : null,
  };
};
