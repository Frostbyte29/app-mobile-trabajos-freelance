import { z } from "zod";

export const projectSchema = z.object({
  titulo: z.string().min(1),
  descripcion: z.string().min(1),
  presupuesto: z.number().min(0),
  categoria: z.string().min(1),
  empresa: z.string().min(1),
});
