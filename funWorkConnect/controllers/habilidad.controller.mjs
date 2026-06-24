import * as service from "../services/habilidad.service.mjs";
import { habilidadSchema } from "../validators/habilidad.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = habilidadSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const habilidad = await service.getPorId(id);
  if (!habilidad) return notFound("Habilidad no encontrada");
  return ok(habilidad);
};

export const actualizar = async (id, event) => ok(await service.actualizar(id, JSON.parse(event.body)));
export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };
export const listar = async (query) => ok(await service.listar(query?.limit ? parseInt(query.limit) : 20, query?.lastKey));
