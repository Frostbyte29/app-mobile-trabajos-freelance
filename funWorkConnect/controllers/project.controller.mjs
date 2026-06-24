import * as service from "../services/project.service.mjs";
import { projectSchema } from "../validators/project.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crearProject = async (event) => {
  const body = JSON.parse(event.body);
  const parsed = projectSchema.safeParse(body);

  if (!parsed.success) return badRequest(parsed.error);

  const project = await service.crear(parsed.data);
  return created(project);
};

export const getProject = async (id) => {
  const project = await service.getById(id);
  if (!project) return notFound("Proyecto no encontrado");
  return ok(project);
};

export const actualizarProject = async (id, event) => {
  const body = JSON.parse(event.body);
  const project = await service.actualizar(id, body);
  return ok(project);
};

export const eliminarProject = async (id) => {
  await service.eliminar(id);
  return noContent();
};

export const getProjects = async (query) => {
  const limit = query?.limit ? parseInt(query.limit) : 10;
  const result = await service.listar(limit, query?.lastKey);
  return ok(result);
};
