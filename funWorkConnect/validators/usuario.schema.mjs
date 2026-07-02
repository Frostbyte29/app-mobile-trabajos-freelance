import { z } from "zod";

// Sub-schema para los datos de empresa del reclutador
// nullable() acepta tanto null (Gson) como undefined (JS nativo)
const empresaReclutadorSchema = z.object({
  nombre: z.string().optional(),
  rubro: z.string().optional(),
  correoContacto: z.string().optional(),   // sin .email() para evitar fallos con string vacío
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
  roles: z.array(z.enum(["candidato", "reclutador"])).min(1),
  empresaInfo: empresaReclutadorSchema,
});
