import algosdk from 'algosdk';

const token = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
const server = "http://127.0.0.1";
const port = 4001;

// algod client
const AlgorandClient = new algosdk.Algod(token, server, port);
export default AlgorandClient;