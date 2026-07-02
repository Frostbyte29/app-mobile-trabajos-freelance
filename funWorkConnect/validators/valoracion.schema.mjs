import { z } from "zod";

export const valoracionSchema = z.object({
  usuarioEmisorId: z.string().min(1),
  usuarioReceptorId: z.string().min(1),
  vacanteId: z.string().optional(),      // ID del proyecto/oferta relacionada
  proyectoId: z.string().optional(),     // alias de vacanteId para mayor claridad
  puntuacion: z.number().min(1).max(5),
  comentario: z.string().optional(),
});

export const valoracionUpdateSchema = z.object({
  puntuacion: z.number().min(1).max(5).optional(),
  comentario: z.string().optional(),
}).refine(
  (data) => data.puntuacion !== undefined || data.comentario !== undefined,
  { message: "Debes enviar puntuacion y/o comentario para editar" }
);
