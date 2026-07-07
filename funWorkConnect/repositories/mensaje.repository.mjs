import { PutCommand, QueryCommand } from "@aws-sdk/lib-dynamodb";
import { ddb } from "../config/dynamo.mjs";

const TABLE = process.env.TABLE_NAME;
const stripKeys = ({ PK, SK, GSI1PK, GSI1SK, GSI2PK, GSI2SK, GSI3PK, GSI3SK, ...rest } = {}) => rest;

export const crear = (conversacionId, item) => ddb.send(new PutCommand({
  TableName: TABLE,
  Item: { ...item, PK: `CONVERSACION#${conversacionId}`, SK: `MENSAJE#${item.fechaEnvio}#${item.id}` },
}));

export const listarPorConversacion = async (conversacionId, limit, lastKey) => {
  const r = await ddb.send(new QueryCommand({
    TableName: TABLE,
    KeyConditionExpression: "PK = :p and begins_with(SK, :sk)",
    ExpressionAttributeValues: { ":p": `CONVERSACION#${conversacionId}`, ":sk": "MENSAJE#" },
    Limit: limit, ScanIndexForward: true, 
    ExclusiveStartKey: lastKey ? JSON.parse(Buffer.from(lastKey, "base64").toString()) : undefined,
  }));
  return { ...r, Items: r.Items.map(stripKeys) };
};
