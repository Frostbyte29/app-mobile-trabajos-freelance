import * as service from "../services/conversacion.service.mjs";
import { conversacionSchema } from "../validators/conversacion.schema.mjs";
import { ok, noContent, created, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = conversacionSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const conversacion = await service.getPorId(id);
  if (!conversacion) return notFound("Conversación no encontrada");
  return ok(conversacion);
};

// PUT /conversaciones/{id} cierra la conversacion (activa=false)
export const actualizar = async (id) => ok(await service.cerrar(id));
// DELETE /conversaciones/{id} borra todo todito 
export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };

// GET /conversaciones?usuarioId=x (obligatorio)
export const listar = async (query) => {
  if (!query?.usuarioId) return badRequest({ message: "Debes pasar usuarioId como query param" });
  return ok(await service.listarPorUsuario(query.usuarioId, query?.limit ? parseInt(query.limit) : 15, query.lastKey));
};