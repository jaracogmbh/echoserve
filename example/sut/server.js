const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json());

const AUTH_SERVER_URL = process.env.AUTH_SERVER_URL || 'http://echoserve:8089/authenticate';

const authenticate = async (req, res, next) => {
  try {
    const authResponse = await axios.post(AUTH_SERVER_URL, {}, {
      headers: {
        'Authorization': req.headers['authorization'] || '',
        'Content-Type': 'application/json',
      }
    });

    if (authResponse.status === 200) {
      req.user = authResponse.data;
      next();
    } else {
      res.status(401).json({ error: 'Unauthorized' });
    }
  } catch (error) {
    res.status(401).json({ error: 'Authentication failed' });
  }
};

app.post('/addEmployee', authenticate, (req, res) => {
  res.status(200).json({ message: 'Employee added successfully', token: req.user.token });
});

app.put('/editEmployee', authenticate, (req, res) => {
  res.status(200).json({ message: 'Employee edited successfully', token: req.user.token });
});

app.delete('/deleteSomething', authenticate, (req, res) => {
  res.status(200).json({ message: 'Item deleted successfully', token: req.user.token });
});

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