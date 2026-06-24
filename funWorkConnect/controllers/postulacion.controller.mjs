import * as service from "../services/postulacion.service.mjs";
import { postulacionSchema, estadoPostulacionSchema } from "../validators/postulacion.schema.mjs";
import { ok, created, noContent, badRequest, notFound } from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = postulacionSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const postulacion = await service.getPorId(id);
  if (!postulacion) return notFound("Postulación no encontrada");
  return ok(postulacion);
};

// PUT /postulaciones/{id} solo cambia estado (aceptar/rechazar/revisar)
export const actualizar = async (id, event) => {
  const parsed = estadoPostulacionSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return ok(await service.actualizarEstado(id, parsed.data.estado));
};

export const eliminar = async (id) => { await service.eliminar(id); return noContent(); };

// GET /postulaciones?candidatoId=x  ó  GET /postulaciones?vacanteId=y
export const listar = async (query) => {
  const limit = query?.limit ? parseInt(query.limit) : 10;
  if (query?.candidatoId) return ok(await service.listarPorCandidato(query.candidatoId, limit, query.lastKey));
  if (query?.vacanteId) return ok(await service.listarPorVacante(query.vacanteId, limit, query.lastKey));
  return badRequest({ message: "Debes pasar candidatoId o vacanteId como query param" });
};
