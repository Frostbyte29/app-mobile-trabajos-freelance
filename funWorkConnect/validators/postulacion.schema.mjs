import { z } from "zod";

// Schema para crear una postulación
export const postulacionSchema = z.object({
  candidatoId: z.string().uuid("El ID del candidato debe ser un UUID válido"),
  vacanteId: z.string().uuid("El ID de la vacante debe ser un UUID válido"),
  mensajePresentacion: z.string()
    .max(2000, "El mensaje no puede exceder 2000 caracteres")
    .optional()
    .transform(val => val?.trim()),
  cvUrl: z.string()
    .url("La URL del CV debe ser válida")
    .optional()
});

// Schema para actualizar el estado de una postulación
export const estadoPostulacionSchema = z.object({
  estado: z.enum(["postulado", "en_revision", "aceptado", "rechazado"], {
    errorMap: () => ({ message: "Estado inválido. Valores permitidos: postulado, en_revision, aceptado, rechazado" })
  }),
  comentario: z.string()
    .max(500, "El comentario no puede exceder 500 caracteres")
    .optional()
    .transform(val => val?.trim())
});
