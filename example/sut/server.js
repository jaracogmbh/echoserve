const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json());

const AUTH_SERVER_URL = process.env.AUTH_SERVER_URL || 'http://echoserve:8089/authenticate';

app.post('/login', async (req, res) => {
  const { username, password } = req.body;

  try {
    const authResponse = await axios.post(AUTH_SERVER_URL, { username, password }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });

    res.status(200).json(authResponse.data);
  } catch (error) {
    res.status(401).json({ error: 'Authentication failed' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Service Under Test running on port ${PORT}`);
});