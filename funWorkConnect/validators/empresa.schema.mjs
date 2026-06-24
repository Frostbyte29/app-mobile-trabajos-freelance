import { z } from "zod";

export const empresaSchema = z.object({
  nombre: z.string().min(1),
  descripcion: z.string().optional(),
  rubro: z.string().min(1),
  correoContacto: z.string().email(),
  telefono: z.string().optional(),
  sitioWeb: z.string().optional(),
  logoUrl: z.string().optional(),
  direccion: z.string().optional(),
});
