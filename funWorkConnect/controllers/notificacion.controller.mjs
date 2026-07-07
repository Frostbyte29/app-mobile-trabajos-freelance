import * as service from "../services/notificacion.service.mjs";
import { notificacionSchema } from "../validators/notificacion.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = notificacionSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const notificacion = await service.getPorId(id);
  if (!notificacion) return notFound("Notificación no encontrada");
  return ok(notificacion);
};

export const actualizar = async (id) => ok(await service.marcarLeida(id));
export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };

export const listar = async (query) => {
  if (!query?.usuarioId) return badRequest({ message: "Debes pasar usuarioId como query param" });
  return ok(await service.listarPorUsuario(query.usuarioId, query?.limit ? parseInt(query.limit) : 15, query.lastKey));
};
