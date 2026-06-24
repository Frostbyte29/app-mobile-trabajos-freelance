import * as service from "../services/empresa.service.mjs";
import { empresaSchema } from "../validators/empresa.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = empresaSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const empresa = await service.getPorId(id);
  if (!empresa) return notFound("Empresa no encontrada");
  return ok(empresa);
};

export const actualizar = async (id, event) => ok(await service.actualizar(id, JSON.parse(event.body)));
export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };
export const listar = async (query) => ok(await service.listar(query?.limit ? parseInt(query.limit) : 10, query?.lastKey));
