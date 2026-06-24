import * as service from "../services/usuario.service.mjs";
import { usuarioSchema } from "../validators/usuario.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const body = JSON.parse(event.body);
  const parsed = usuarioSchema.safeParse(body);
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const usuario = await service.getPorId(id);
  if (!usuario) return notFound("Usuario no encontrado");
  return ok(usuario);
};

export const actualizar = async (id, event) => ok(await service.actualizar(id, JSON.parse(event.body)));

export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };

export const listar = async (query) => ok(await service.listar(query?.limit ? parseInt(query.limit) : 10, query?.lastKey));
