# WorkConnect

## Descripción

**WorkConnect** es una aplicación móvil desarrollada en Android que conecta trabajadores independientes con clientes que buscan contratar servicios. La aplicación permite registrar usuarios, iniciar sesión, explorar proyectos, publicar ofertas de trabajo, visualizar perfiles, intercambiar mensajes y gestionar notificaciones.

El proyecto utiliza una arquitectura limpia de cliente-servidor donde la aplicación Android consume servicios REST expuestos mediante **Amazon API Gateway**. Dichos servicios ejecutan funciones **AWS Lambda**, las cuales interactúan con **Amazon DynamoDB** para almacenar y recuperar la información.

---

## Estructura del repositorio

````text
work_conect/
│
├── WorkConnect_android_studio/     # Proyecto Android
│   ├── ...
│
├── funWorkconnect/                 # Código fuente del Lambda
│   ├── ...
│
├── funWorkconnect.zip              # Código fuente del lambda zipeado listo para importar
│
├── capaNode.zip                    # Capa donde se encuentra el zod
│
└── README.md

---

## Tecnologías utilizadas

* Kotlin
* Android Studio
* AWS Lambda
* Amazon API Gateway
* Amazon DynamoDB

---

## Requisitos

* Android Studio (de preferencia la más versión reciente)
* JDK 17 o superior
* Gradle
* Cuenta de AWS (para desplegar los servicios, puede ser la freetear de studiante)
* API Gateway configurado
* Crear 2 particiones en DynamoDB PK(particion key) y SK(sort key)
* Crear 3 indices (GSI1, GSI2, GSI3)
* Subir capaNode en el lambda
* Crear tablas de DynamoDB (se pueden importar por el zip)
* Funciones Lambda desplegadas

---

## Ejecución del proyecto Android

1. Clonar el repositorio.

```bash
git clone https://github.com/Frostbyte29/app-mobile-trabajos-freelance
````

2. Abrir la carpeta **WorkConnect_android_studio** con Android Studio.

3. Esperar a que Gradle descargue todas las dependencias.

4. Configurar la URL de la API Gateway en el archivo correspondiente del proyecto (si aplica).

5. Ejecutar la aplicación en un emulador o dispositivo Android.

---

## Despliegue de AWS Lambda

1. Crear las funciones Lambda en AWS.

2. Subir el código fuente correspondiente.

3. Configurar los permisos IAM necesarios para acceder a DynamoDB.

4. Asociar cada función a un endpoint de Amazon API Gateway.

5. Desplegar la API.

---

## Recursos requeridos

### Amazon API Gateway

Se requiere una API REST que exponga los endpoints utilizados por la aplicación Android.

Ejemplo:

```
https://xxxxxxxxxx.execute-api.region.amazonaws.com/prod/
```

La URL base debe configurarse dentro del proyecto Android.

---

### AWS Lambda

Las funciones Lambda contienen la lógica del backend y son invocadas desde API Gateway.

Cada función debe tener permisos para acceder a las tablas correspondientes de DynamoDB.

---

### Amazon DynamoDB

La aplicación utiliza DynamoDB como base de datos NoSQL no relacional.

Se deben crear 2 particiones en DynamoDB PK(particion key) y SK(sort key)
y tambien crear 3 indices (GSI1, GSI2, GSI3)

Dependiendo de la implementación, pueden existir tablas para almacenar información como:

- Usuarios
- Proyectos
- Conversaciones
- Mensajes
- Notificaciones
- Contratos

---

## Variables de configuración

Para conectar correctamente la aplicación con AWS se requiere configurar:

| Variable         | Descripción                                               |
| ---------------- | --------------------------------------------------------- |
| API_BASE_URL     | URL base de Amazon API Gateway                            |
| AWS_REGION       | Región donde se encuentran los recursos AWS               |
| DYNAMODB_TABLES  | Nombres de las tablas utilizadas por las funciones Lambda |
| LAMBDA_FUNCTIONS | Funciones asociadas a cada endpoint del API               |

---

## Flujo de funcionamiento

```text
Aplicación Android
        │
        ▼
Amazon API Gateway
        │
        ▼
AWS Lambda
        │
        ▼
Amazon DynamoDB
```

---

## Autores

Proyecto desarrollado para el curso de Aplicaciones Móviles.

Integrantes del equipo:

- Carlos Emilio Hurtado Rojas
- Juan Diego Alejandro Oriundo Ticse
- Bryan Steven de la Cruz Cayao
- Andre Alejandro Arbayza Pareja
- Ariana Lisseth Mallque Rivera

---
