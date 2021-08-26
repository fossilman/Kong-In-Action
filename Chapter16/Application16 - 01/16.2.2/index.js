const {
  Server
} = require('@webserverless/fc-express');
const express = require('express');
const app = express();
const port = 3000;
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectId;
// for parsing application/json
app.use(express.json());
// for parsing application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));

function connect(cb) {
  return MongoClient.connect('mongodb://root:1qaz2WSX@dds-j6ca86a564f0e8441.mongodb.rds.aliyuncs.com:3717,dds-j6ca86a564f0e8442.mongodb.rds.aliyuncs.com:3717/admin?replicaSet=mgset-43533659', cb)
}

app.all("/getPets", (req, resp) => {
  connect(function (err, client) {
    if (err) throw err
    const db = client.db('pets')
    const collection = db.collection('list');
    collection.find().toArray(function (err, result) {
      if (err) throw err
      resp.send(JSON.stringify(result));
    });
  });
});

app.all("/addPet", (req, resp) => {
  connect(function (err, client) {
    if (err) throw err
    const db = client.db('pets')
    const collection = db.collection('list');
    collection.insertMany([{
      ...req.body
    }], function (err, result) {
      if (err === null && result.result.n === 1) {
        resp.send(JSON.stringify({ status: 'success' }));
      } else {
        resp.send(JSON.stringify({ status: 'error' }));
      }
    });
  });
});

app.all("/delPet", (req, resp) => {
  connect(function (err, client) {
    if (err) throw err
    const db = client.db('pets');
    const collection = db.collection('list');
    collection.deleteOne({ _id: ObjectId(req.body._id) }, function (err, result) {
      if (err === null && result.result.n === 1) {
        resp.send(JSON.stringify({ status: 'success' }));
      } else {
        resp.send(JSON.stringify({ status: 'error' }));
      }
    });
  });
});

app.listen(process.env.PORT || port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});

const server = new Server(app);

// http trigger entry
module.exports.handler = function (req, res, context) {
  server.httpProxy(req, res, context);
};