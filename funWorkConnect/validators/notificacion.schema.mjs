import { z } from "zod";

export const notificacionSchema = z.object({
  usuarioId: z.string().min(1),
  titulo: z.string().min(1),
  mensaje: z.string().min(1),
  tipo: z.string().min(1),
  referenciaId: z.string().optional(),
});
