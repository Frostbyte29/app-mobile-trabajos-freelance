import { z } from "zod";

export const usuarioSchema = z.object({
  nombres: z.string().min(1),
  apellidos: z.string().min(1),
  correo: z.string().email(),
  telefono: z.string().optional(),
  fotoPerfilUrl: z.string().optional(),
  roles: z.array(z.enum(["candidato", "reclutador"])).min(1),
});
