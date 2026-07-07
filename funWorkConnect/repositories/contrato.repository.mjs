import { PutCommand, GetCommand, DeleteCommand, UpdateCommand, QueryCommand } from "@aws-sdk/lib-dynamodb";
import { ddb } from "../config/dynamo.mjs";

const TABLE = process.env.TABLE_NAME;
const TIPO = "CONTRATO";
const itemKey = (id) => ({ PK: `${TIPO}#${id}`, SK: "METADATA" });
const stripKeys = ({ PK, SK, GSI1PK, GSI1SK, GSI2PK, GSI2SK, GSI3PK, GSI3SK, ...rest } = {}) => rest;

export const crear = (item) => ddb.send(new PutCommand({
  TableName: TABLE,
  Item: {
    ...item,
    ...itemKey(item.id),
    GSI2PK: `USUARIO#${item.freelancerId}`,
    GSI2SK: `CONTRATO#${item.fechaInicio}`,
    GSI3PK: `USUARIO#${item.contratanteId}`,
    GSI3SK: `CONTRATO#${item.fechaInicio}`,
    tipo: TIPO,
  },
}));

export const getPorId = async (id) => {
  const r = await ddb.send(new GetCommand({ TableName: TABLE, Key: itemKey(id) }));
  return { Item: r.Item ? stripKeys(r.Item) : null };
};

export const actualizarEstado = async (id, estado, fechaFin) => {
  const r = await ddb.send(new UpdateCommand({
    TableName: TABLE,
    Key: itemKey(id),
    UpdateExpression: "set #estado = :estado, #fechaFin = :fechaFin",
    ExpressionAttributeNames: {
      "#estado": "estado",
      "#fechaFin": "fechaFin",
    },
    ExpressionAttributeValues: {
      ":estado": estado,
      ":fechaFin": fechaFin,
    },
    ConditionExpression: "attribute_exists(PK)",
    ReturnValues: "ALL_NEW",
  }));
  return { Attributes: stripKeys(r.Attributes) };
};

export const listarPorFreelancer = async (freelancerId, limit, lastKey) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE,
    IndexName: "GSI2",
    KeyConditionExpression: "GSI2PK = :p and begins_with(GSI2SK, :tipo)",
    ExpressionAttributeValues: {
      ":p": `USUARIO#${freelancerId}`,
      ":tipo": "CONTRATO#",
    },
    Limit: limit,
    ScanIndexForward: false,
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...r, Items: r.Items?.map(stripKeys) || [] };
};

export const listarPorContratante = async (contratanteId, limit, lastKey) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE,
    IndexName: "GSI3",
    KeyConditionExpression: "GSI3PK = :p and begins_with(GSI3SK, :tipo)",
    ExpressionAttributeValues: {
      ":p": `USUARIO#${contratanteId}`,
      ":tipo": "CONTRATO#",
    },
    Limit: limit,
    ScanIndexForward: false,
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...r, Items: r.Items?.map(stripKeys) || [] };
};
