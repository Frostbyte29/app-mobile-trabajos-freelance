import * as service from "../services/contrato.service.mjs";
import { contratoSchema, estadoContratoSchema } from "../validators/contrato.schema.mjs";
import { ok, created, badRequest, notFound, conflict, internalError } from "../utils/response.mjs";

export const crear = async (event) => {
  try {
    const parsed = contratoSchema.safeParse(JSON.parse(event.body));
    if (!parsed.success) {
      return badRequest({
        message: "Datos inválidos",
        errors: parsed.error.flatten().fieldErrors,
      });
    }
    const contrato = await service.crear(parsed.data);
    return created(contrato);
  } catch (error) {
    console.error("Error al crear contrato:", error);
    return internalError({ message: "Error al crear el contrato" });
  }
};

export const getPorId = async (id) => {
  try {
    const contrato = await service.getPorId(id);
    if (!contrato) return notFound({ message: "Contrato no encontrado" });
    return ok(contrato);
  } catch (error) {
    console.error("Error al obtener contrato:", error);
    return internalError({ message: "Error al obtener el contrato" });
  }
};

export const actualizar = async (id, event) => {
  try {
    const parsed = estadoContratoSchema.safeParse(JSON.parse(event.body));
    if (!parsed.success) {
      return badRequest({
        message: "Datos inválidos",
        errors: parsed.error.flatten().fieldErrors,
      });
    }
    const contratoActualizado = await service.finalizar(id);
    return ok(contratoActualizado);
  } catch (error) {
    if (error.message.includes("no encontrado")) {
      return notFound({ message: error.message });
    }
    if (error.message.includes("no está en curso")) {
      return conflict({ message: error.message });
    }
    console.error("Error al actualizar contrato:", error);
    return internalError({ message: "Error al actualizar el contrato" });
  }
};

export const listar = async (query) => {
  try {
    const limit = query?.limit ? Math.min(parseInt(query.limit), 100) : 20;

    if (query?.freelancerId) {
      const resultado = await service.listarPorFreelancer(
        query.freelancerId,
        limit,
        query.lastKey
      );
      return ok(resultado);
    }

    if (query?.contratanteId) {
      const resultado = await service.listarPorContratante(
        query.contratanteId,
        limit,
        query.lastKey
      );
      return ok(resultado);
    }

    return badRequest({
      message: "Debes proporcionar freelancerId o contratanteId como parámetro de consulta",
    });
  } catch (error) {
    console.error("Error al listar contratos:", error);
    return internalError({ message: "Error al listar los contratos" });
  }
};

export const eliminar = async (_id) => ({
  statusCode: 405,
  body: JSON.stringify({ message: "Método no permitido" }),
});
