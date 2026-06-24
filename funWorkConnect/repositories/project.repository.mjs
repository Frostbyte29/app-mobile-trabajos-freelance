import {
  PutCommand,
  GetCommand,
  DeleteCommand,
  UpdateCommand,
  QueryCommand
} from "@aws-sdk/lib-dynamodb";
import { ddb } from "../config/dynamo.mjs";

const TABLE = process.env.TABLE_NAME;

const itemKey = (id) => ({ PK: `VACANTE#${id}`, SK: "METADATA" });

const stripKeys = (item) => {
  if (!item) return item;
  const { PK, SK, GSI1PK, GSI1SK, GSI2PK, GSI2SK, GSI3PK, GSI3SK, ...rest } = item;
  return rest;
};

export const crearProject = (item) =>
  ddb.send(new PutCommand({
    TableName: TABLE,
    Item: {
      ...item,
      ...itemKey(item.id),
      GSI1PK: "VACANTE",
      GSI1SK: `${item.createdAt}#${item.id}`,
    },
  }));

export const getProjectById = async (id) => {
  const result = await ddb.send(new GetCommand({
    TableName: TABLE,
    Key: itemKey(id),
  }));
  return { Item: stripKeys(result.Item) };
};

export const eliminarProject = (id) =>
  ddb.send(new DeleteCommand({
    TableName: TABLE,
    Key: itemKey(id),
    ConditionExpression: "attribute_exists(PK)"
  }));

export const actualizarProject = async (id, updateExpression, names, values) => {
  const result = await ddb.send(new UpdateCommand({
    TableName: TABLE,
    Key: itemKey(id),
    UpdateExpression: updateExpression,
    ExpressionAttributeNames: names,
    ExpressionAttributeValues: values,
    ConditionExpression: "attribute_exists(PK)",
    ReturnValues: "ALL_NEW",
  }));
  return { Attributes: stripKeys(result.Attributes) };
};

// Listado real via GSI1 (mas nuevo primero), reemplaza el Scan
export const getProjects = async (limit, lastKey) => {
  const result = await ddb.send(new QueryCommand({
    TableName: TABLE,
    IndexName: "GSI1",
    KeyConditionExpression: "GSI1PK = :p",
    ExpressionAttributeValues: { ":p": "VACANTE" },
    Limit: limit,
    ScanIndexForward: false,
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...result, Items: result.Items.map(stripKeys) };
};
