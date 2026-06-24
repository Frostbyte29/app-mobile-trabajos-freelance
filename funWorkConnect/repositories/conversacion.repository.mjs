import { PutCommand, GetCommand, QueryCommand, UpdateCommand, DeleteCommand } from "@aws-sdk/lib-dynamodb";
import { ddb } from "../config/dynamo.mjs";

const TABLE = process.env.TABLE_NAME;
const stripKeys = ({ PK, SK, GSI1PK, GSI1SK, GSI2PK, GSI2SK, GSI3PK, GSI3SK, ...rest } = {}) => rest;

export const crear = (item) => ddb.send(new PutCommand({
  TableName: TABLE,
  Item: {
    ...item, PK: `CONVERSACION#${item.id}`, SK: "METADATA",
    GSI2PK: `USUARIO#${item.participanteAId}`, GSI2SK: `CONVERSACION#${item.fechaCreacion}`,
  },
}));

export const crearPuntero = (item) => ddb.send(new PutCommand({
  TableName: TABLE,
  Item: {
    PK: `CONVERSACION#${item.id}`, SK: `PARTICIPANTE#${item.participanteBId}`,
    GSI2PK: `USUARIO#${item.participanteBId}`, GSI2SK: `CONVERSACION#${item.fechaCreacion}`,
    conversacionId: item.id,
  },
}));

export const getPorId = async (id) => {
  const r = await ddb.send(new GetCommand({ TableName: TABLE, Key: { PK: `CONVERSACION#${id}`, SK: "METADATA" } }));
  return { Item: stripKeys(r.Item) };
};

export const marcarInactiva = (id) => ddb.send(new UpdateCommand({
  TableName: TABLE, Key: { PK: `CONVERSACION#${id}`, SK: "METADATA" },
  UpdateExpression: "set #activa = :activa",
  ExpressionAttributeNames: { "#activa": "activa" },
  ExpressionAttributeValues: { ":activa": false },
}));

export const eliminarCompleta = async (id) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE,
    KeyConditionExpression: "PK = :p",
    ExpressionAttributeValues: { ":p": `CONVERSACION#${id}` },
  }));
  const items = r.Items || [];
  await Promise.all(items.map((item) =>
    ddb.send(new DeleteCommand({ TableName: TABLE, Key: { PK: item.PK, SK: item.SK } }))
  ));
  return items.length;
};

export const listarPorUsuario = async (usuarioId, limit, lastKey) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE, IndexName: "GSI2",
    KeyConditionExpression: "GSI2PK = :p and begins_with(GSI2SK, :tipo)",
    ExpressionAttributeValues: { ":p": `USUARIO#${usuarioId}`, ":tipo": "CONVERSACION#" },
    Limit: limit, ScanIndexForward: false,
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...r, Items: r.Items.map(stripKeys) };
};