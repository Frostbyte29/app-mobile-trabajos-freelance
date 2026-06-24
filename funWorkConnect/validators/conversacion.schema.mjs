import { z } from "zod";

export const conversacionSchema = z.object({
  vacanteId: z.string().optional(),
  candidatoId: z.string().min(1),
  reclutadorId: z.string().min(1),
});

export const mensajeSchema = z.object({
  emisorId: z.string().min(1),
  contenido: z.string().min(1),
  archivoUrl: z.string().optional(),
});
