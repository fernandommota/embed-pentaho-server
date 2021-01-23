var express = require("express");
var HTTPStatus = require("http-status");

function onSuccess(res, data) {
  return res.status(HTTPStatus.OK).json({ payload: data });
}

function onError(res, message, err) {
  console.log(`Error: ${err}`);
  return res.status(HTTPStatus.INTERNAL_SERVER_ERROR).send(message);
}

module.exports = {
  onSuccess: onSuccess,
  onError: onError
};
