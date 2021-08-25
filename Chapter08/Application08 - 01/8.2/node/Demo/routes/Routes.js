const express = require('express');

const app = express.Router();

app.get('/users/v1', (req, res) => {
  res.json({
    "language": "node",
    "type": "application",
    "version": "v1",
    "user": "demo_v1",
  });
});

app.get('/users/v2', (req, res) => {
  res.json({
    "language": "node",
    "type": "application",
    "version": "v2",
    "user": "demo_v2",
  });
});

app.get('/', (req, res) => {
  res.json({
    "language": "node",
    "type": "application fallback"
  });
});

app.get('/test', (req, res) => {
  res.json({
    "language": "node",
    "type": "Test fallback"
  });
});
module.exports = app;
