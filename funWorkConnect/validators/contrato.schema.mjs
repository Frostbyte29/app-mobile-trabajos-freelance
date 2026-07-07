import { z } from "zod";

export const contratoSchema = z.object({
  contratanteId: z.string().min(1),
  freelancerId: z.string().min(1),
  ofertaId: z.string().min(1),
  tituloOferta: z.string().min(1),
  tipoOrigen: z.enum(["trabajo", "servicio"]),
  postulacionId: z.string().optional(),
});

export const estadoContratoSchema = z.object({
  estado: z.enum(["finalizado"]),
});
