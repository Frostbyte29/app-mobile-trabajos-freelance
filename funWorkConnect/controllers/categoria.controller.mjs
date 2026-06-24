import * as service from "../services/categoria.service.mjs";
import { categoriaSchema } from "../validators/categoria.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = categoriaSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const categoria = await service.getPorId(id);
  if (!categoria) return notFound("Categoría no encontrada");
  return ok(categoria);
};

export const actualizar = async (id, event) => ok(await service.actualizar(id, JSON.parse(event.body)));
export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };
export const listar = async (query) => ok(await service.listar(query?.limit ? parseInt(query.limit) : 20, query?.lastKey));
