import { PutCommand, GetCommand, DeleteCommand, UpdateCommand, QueryCommand } from "@aws-sdk/lib-dynamodb";
import { ddb } from "../config/dynamo.mjs";

const TABLE = process.env.TABLE_NAME;
const TIPO = "POSTULACION";
const itemKey = (id) => ({ PK: `${TIPO}#${id}`, SK: "METADATA" });
const stripKeys = ({ PK, SK, GSI1PK, GSI1SK, GSI2PK, GSI2SK, GSI3PK, GSI3SK, ...rest } = {}) => rest;

export const crear = (item) => ddb.send(new PutCommand({
  TableName: TABLE,
  Item: {
    ...item,
    ...itemKey(item.id),
    GSI2PK: `USUARIO#${item.candidatoId}`,
    GSI2SK: `POSTULACION#${item.fechaPostulacion}`,
    GSI3PK: `VACANTE#${item.vacanteId}`,
    GSI3SK: `POSTULACION#${item.fechaPostulacion}`,
  },
}));

export const getPorId = async (id) => {
  const r = await ddb.send(new GetCommand({ TableName: TABLE, Key: itemKey(id) }));
  return { Item: stripKeys(r.Item) };
};

export const eliminar = (id) => ddb.send(new DeleteCommand({
  TableName: TABLE, Key: itemKey(id), ConditionExpression: "attribute_exists(PK)",
}));

export const actualizar = async (id, updateExpression, names, values) => {
  const r = await ddb.send(new UpdateCommand({
    TableName: TABLE, Key: itemKey(id), UpdateExpression: updateExpression,
    ExpressionAttributeNames: names, ExpressionAttributeValues: values,
    ConditionExpression: "attribute_exists(PK)", ReturnValues: "ALL_NEW",
  }));
  return { Attributes: stripKeys(r.Attributes) };
};

export const listarPorCandidato = async (candidatoId, limit, lastKey) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE, IndexName: "GSI2",
    KeyConditionExpression: "GSI2PK = :p and begins_with(GSI2SK, :tipo)",
    ExpressionAttributeValues: { ":p": `USUARIO#${candidatoId}`, ":tipo": "POSTULACION#" },
    Limit: limit, ScanIndexForward: false,
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...r, Items: r.Items.map(stripKeys) };
};

export const listarPorVacante = async (vacanteId, limit, lastKey) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE, IndexName: "GSI3",
    KeyConditionExpression: "GSI3PK = :p", ExpressionAttributeValues: { ":p": `VACANTE#${vacanteId}` },
    Limit: limit, ScanIndexForward: false,
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...r, Items: r.Items.map(stripKeys) };
};
