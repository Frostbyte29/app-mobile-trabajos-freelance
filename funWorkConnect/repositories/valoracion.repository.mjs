import {
  PutCommand,
  GetCommand,
  DeleteCommand,
  UpdateCommand,
  QueryCommand,
} from "@aws-sdk/lib-dynamodb";
import { ddb } from "../config/dynamo.mjs";

const TABLE = process.env.TABLE_NAME;
const TIPO = "VALORACION";
const itemKey = (id) => ({ PK: `${TIPO}#${id}`, SK: "METADATA" });
const stripKeys = ({
  PK,
  SK,
  GSI1PK,
  GSI1SK,
  GSI2PK,
  GSI2SK,
  GSI3PK,
  GSI3SK,
  ...rest
} = {}) => rest;

export const crear = (item) =>
  ddb.send(
    new PutCommand({
      TableName: TABLE,
      Item: {
        ...item,
        ...itemKey(item.id),
        GSI2PK: `USUARIO#${item.usuarioReceptorId}`,
        GSI2SK: `VALORACION#${item.fechaCreacion}`,
      },
    }),
  );

export const getPorId = async (id) => {
  const r = await ddb.send(
    new GetCommand({ TableName: TABLE, Key: itemKey(id) }),
  );
  return { Item: stripKeys(r.Item) };
};

export const eliminar = (id) =>
  ddb.send(
    new DeleteCommand({
      TableName: TABLE,
      Key: itemKey(id),
      ConditionExpression: "attribute_exists(PK)",
    }),
  );

export const actualizar = async (id, updateExpression, names, values) => {
  const r = await ddb.send(
    new UpdateCommand({
      TableName: TABLE,
      Key: itemKey(id),
      UpdateExpression: updateExpression,
      ExpressionAttributeNames: names,
      ExpressionAttributeValues: values,
      ConditionExpression: "attribute_exists(PK)",
      ReturnValues: "ALL_NEW",
    }),
  );
  return { Attributes: stripKeys(r.Attributes) };
};

export const listarPorReceptor = async (usuarioReceptorId, limit, lastKey) => {
  const r = await ddb.send(
    new QueryCommand({
      TableName: TABLE,
      IndexName: "GSI2",
      KeyConditionExpression: "GSI2PK = :p and begins_with(GSI2SK, :tipo)",
      ExpressionAttributeValues: {
        ":p": `USUARIO#${usuarioReceptorId}`,
        ":tipo": "VALORACION#",
      },
      Limit: limit,
      ScanIndexForward: false,
      ExclusiveStartKey: lastKey
        ? JSON.parse(Buffer.from(lastKey, "base64").toString())
        : undefined,
    }),
  );
  return { ...r, Items: r.Items.map(stripKeys) };
};
