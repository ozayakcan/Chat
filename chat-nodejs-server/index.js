#!/usr/bin/env node


var app = require('./server');
var http = require('http');


var port = normalizePort(process.env.PORT || '3000');
app.set('port', port);

var server = http.createServer(app);

server.listen(port);
server.on('error', onError);
server.on('listening', onListening);

function normalizePort(val) {
  var port = parseInt(val, 10);

  if (isNaN(port)) {
    return val;
  }

  if (port >= 0) {
    return port;
  }

  return false;
}

function onError(error) {
  if (error.syscall !== 'listen') {
    throw error;
  }

  var bind = typeof port === 'string'
    ? 'Pipe ' + port
    : 'Port ' + port;

  switch (error.code) {
    case 'EACCES':
      console.error(bind + ' yükseltilmiş ayrıcalıklar gerektiriyor.');
      process.exit(1);
      break;
    case 'EADDRINUSE':
      console.error(bind + ' zaten kullanılıyor.');
      process.exit(1);
      break;
    default:
      throw error;
  }
}

function onListening() {
  console.log('http://localhost:' + port+" adresinde çalışıyor.");
}
