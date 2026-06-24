import { z } from "zod";

export const postulacionSchema = z.object({
  candidatoId: z.string().min(1),
  vacanteId: z.string().min(1),
  mensajePresentacion: z.string().optional(),
  cvUrl: z.string().optional(),
});

export const estadoPostulacionSchema = z.object({
  estado: z.enum(["postulado", "en_revision", "aceptado", "rechazado"]),
});
