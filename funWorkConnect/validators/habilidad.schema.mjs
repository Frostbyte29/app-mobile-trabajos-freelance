import { z } from "zod";

export const habilidadSchema = z.object({
  nombre: z.string().min(1),
  descripcion: z.string().optional(),
});
