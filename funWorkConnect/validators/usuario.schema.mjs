import { z } from "zod";

const empresaReclutadorSchema = z.object({
  nombre: z.string().optional(),
  rubro: z.string().optional(),
  correoContacto: z.string().optional(),  
  telefono: z.string().optional(),
  sitioWeb: z.string().optional(),
  direccion: z.string().optional(),
}).optional().nullable();

export const usuarioSchema = z.object({
  nombres: z.string().min(1),
  apellidos: z.string().optional().default(""),
  correo: z.string().email(),
  telefono: z.string().optional(),
  fotoPerfilUrl: z.string().optional(),
  acercaDe: z.string().max(1000).optional(),
  roles: z.array(z.enum(["candidato", "reclutador"])).min(1),
  empresaInfo: empresaReclutadorSchema,
});
