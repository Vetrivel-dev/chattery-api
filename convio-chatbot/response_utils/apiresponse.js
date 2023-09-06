const responseMessage = (code, message) => {
  let respMessage = null;

  if (code === 200) {
    respMessage = message;
  }

  if (code === 400) {
    respMessage = {
      error: message
    };
  }

  return {
    body: JSON.stringify(respMessage),
    contentType: 'application/json',
    statusCode: code
  };
};

module.exports = {
  responseMessage
};
