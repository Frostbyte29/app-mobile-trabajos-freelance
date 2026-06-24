import * as service from "../services/mensaje.service.mjs";
import { mensajeSchema } from "../validators/conversacion.schema.mjs";
import { ok, created, badRequest } from "../utils/response.mjs";

export const crearMensaje = async (conversacionId, event) => {
  const parsed = mensajeSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(conversacionId, parsed.data));
};

export const getMensajes = async (conversacionId, query) =>
  ok(await service.listarPorConversacion(conversacionId, query?.limit ? parseInt(query.limit) : 30, query?.lastKey));
