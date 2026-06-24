import * as service from "../services/valoracion.service.mjs";
import { valoracionSchema, valoracionUpdateSchema } from "../validators/valoracion.schema.mjs";
import {
  ok,
  created,
  noContent,
  badRequest,
  notFound,
} from "../utils/response.mjs";

export const crear = async (event) => {
  const parsed = valoracionSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return created(await service.crear(parsed.data));
};

export const getPorId = async (id) => {
  const valoracion = await service.getPorId(id);
  if (!valoracion) return notFound("Valoración no encontrada");
  return ok(valoracion);
};

export const eliminar = async (id) => {
  await service.eliminar(id);
  return noContent();
};

// GET /valoraciones?usuarioReceptorId=x (obligatorio)
export const listar = async (query) => {
  if (!query?.usuarioReceptorId)
    return badRequest({
      message: "Debes pasar usuarioReceptorId como query param",
    });
  return ok(
    await service.listarPorReceptor(
      query.usuarioReceptorId,
      query?.limit ? parseInt(query.limit) : 15,
      query.lastKey,
    ),
  );
};

// Editar solo puntuacion/comentario, queda marcada "editada"
export const actualizar = async (id, event) => {
  const parsed = valoracionUpdateSchema.safeParse(JSON.parse(event.body));
  if (!parsed.success) return badRequest(parsed.error);
  return ok(await service.actualizar(id, parsed.data));
};
