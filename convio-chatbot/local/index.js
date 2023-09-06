
const express = require('express');
const { detectIntent } = require('../dialogflow/dialogflow_functions');
const { compareSuites,guestPerRoom } = require('../dialogflow/aboutTheRooms');
const { includeInRate,paidSeperately } = require('../dialogflow/serviceAndAmenities');
const { getHotelsWithRestaurant,breakfast } = require('../dialogflow/foodAndDrinks');
const { location } = require('../dialogflow/location');
const { checkRoomAvailability } = require('../dialogflow/reservation'); 
const { responseMessage } = require('../response_utils/apiresponse');
const webApp = express();
webApp.use(express.urlencoded({ extended: true }));
webApp.use(express.json());

const PORT = 4000;

webApp.post('/testLocally', async (req, res) => {
  const resp = await handler(req, res);
  res.send(resp);
});

webApp.post('/webhook', async (req, res) => {
  const resp = await webhook(req, res);
  res.send(resp);
});

webApp.listen(PORT, () => {
  console.log(`Server is up and running at ${PORT}`);
});

const handler = async (event, context) => {
  try {
    const requestBody = event.body;
    const { languageCode, queryText, sessionId,flag,Client,HotelId} = requestBody;

    if (!flag) {
      return createResponse(400, { message: 'Invalid Flag' });
    }

    if (flag === 'detectIndent') {
      const detectResponse = await detectIntent(languageCode, queryText, sessionId,Client,HotelId);
     // console.log('Index.js', detectResponse);
      return createResponse(200, detectResponse);
    }

    if (flag === 'healthcheck') {
      console.log('Dialog Flow request', requestBody);
      return createResponse(200, { message: 'API Working Fine..!' });
    }

    return createResponse(400, { message: 'API Not Working Fine..!' });
  } catch (error) {
    console.log('Error in Lambda =>', error);
    return createResponse(400, { message: 'Something went wrong' });
  }
};

const webhook = async (event,context) => {
  try{
    const requestBody = event.body;
    console.log(requestBody);
    const client=requestBody.sessionInfo.parameters.Client;
    const hotelId=requestBody.sessionInfo.parameters.HotelId;
    if (requestBody.sessionInfo.parameters.webhook === 'compareSuites'){
      const compareSuitesResponse = await compareSuites(client,hotelId);
      console.log('Index.js', JSON.stringify(compareSuitesResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "compareSuitesResponse": compareSuitesResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }


    if (requestBody.sessionInfo.parameters.webhook === 'guestPerRoom'){
      const guestPerRoomResponse = await guestPerRoom(client,hotelId);
      console.log('Index.js', JSON.stringify(guestPerRoomResponse));
      let responseData = {
        "fulfillment_response": {
            },
            "session_info":{
              "parameters": {
                "guestPerRoomResponse": guestPerRoomResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'includeInRate'){
      const includeInRateResponse = await includeInRate(client,hotelId);
      console.log('Index.js', JSON.stringify(includeInRateResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "includeInRateResponse": includeInRateResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'paidSeperately'){
      const paidSeperatelyResponse = await paidSeperately(client,hotelId);
      console.log('Index.js', JSON.stringify(paidSeperatelyResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "paidSeperatelyResponse": paidSeperatelyResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'breakFast'){
      const breakFastResponse = await breakfast(client,hotelId);
      console.log('Index.js', JSON.stringify(breakFastResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "breakFastResponse": breakFastResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'restaurant'){
      const getHotelsWithRestaurantResponse = await getHotelsWithRestaurant(client,hotelId);
      console.log('Index.js', getHotelsWithRestaurantResponse);
      let responseData = {
            "session_info":{
              "parameters": {
                "getHotelsWithRestaurantResponse": getHotelsWithRestaurantResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'location'){
      const locationResponse = await location(client,hotelId);
      console.log('Index.js', JSON.stringify(locationResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "locationResponse": locationResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'checkAvailability'){
      const checkAvailabilityResponse = await checkRoomAvailability(client,hotelId);
      console.log('Index.js', JSON.stringify(checkAvailabilityResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "checkAvailabilityResponse": checkAvailabilityResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    
  } catch (error) {
    console.log('Error in Lambda =>', error);
    let responseData = {
      fulfillment_response: {
        messages: [
          'Something went wrong with Webhook API.'
        ]
      }
    }
   
    return responseData;
  }
};
const createResponse = (statusCode, body) => {
  return {
    statusCode,
    headers: {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Headers': 'Content-Type',
      'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
    },
    body: JSON.stringify(body)
  };
};

module.exports = {
  handler,
  webhook
};
