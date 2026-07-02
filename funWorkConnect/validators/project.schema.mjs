import { z } from "zod";

export const projectSchema = z.object({
  titulo: z.string().min(1),
  descripcion: z.string().min(1),
  presupuesto: z.number().min(0),
  categoria: z.string().min(1),

  // Tipo de oferta:
  // "trabajo"   → publicada por reclutador, busca candidatos. Muestra nombre de empresa.
  // "servicio"  → publicada por trabajador/freelancer, ofrece sus servicios. Muestra nombre personal.
  tipoOferta: z.enum(["trabajo", "servicio"]),

  // Nombre visible en la tarjeta — empresa si es "trabajo", nombre personal si es "servicio"
  empresa: z.string().min(1),

  // ID del usuario que publicó (para filtrar "Mis Ofertas" y resolver nombre)
  creadoPorId: z.string().optional(),
});
