const { detectIntent } = require('./dialogflow/dialogflow_functions');
const { compareSuites,guestPerRoom,getActiveRateAndRooms, getGuestCountForRooms} = require('./dialogflow/aboutTheRooms');
const { includeInRate,paidSeperately } = require('./dialogflow/serviceAndAmenities');
const { getHotelsWithRestaurant,breakfast } = require('./dialogflow/foodAndDrinks');
const { location,getLocationDetails } = require('./dialogflow/location');
const { checkRoomAvailability, getRoomCategories} = require('./dialogflow/reservation'); 
const { responseMessage } = require('./response_utils/apiresponse');
const {getClientIdHotelIdByBusinessId,getStaticData,getTypeFromHotelInfo} = require('./utils/connection')

//Function to call All Intends...
const handler = async (event, context) => {
  try {
    const requestBody = JSON.parse(event.body);
    const { languageCode, queryText, sessionId,flag,businessId,roomInfo} = requestBody;
    // const clientHotel = await getClientIdHotelIdByBusinessId(businessId);
    // const Client=clientHotel.mewsClientId;
    // const HotelId=clientHotel.mewsHotelId;
    console.log("Request Body : "+JSON.stringify(requestBody));

    if (!flag) {
      return createResponse(400, { message: 'Invalid Flag' });
    }

    if (flag === 'detectIndent') {
      const detectResponse = await detectIntent(languageCode, queryText, sessionId,businessId,roomInfo);
      console.log("Request by user :"+JSON.stringify(roomInfo));
      console.log('Index.js', JSON.stringify(detectResponse));
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

//Function to call All Webhooks...
const webhook = async (event,context) => {
  try{
    const requestBody = JSON.parse(event.body);
    const client=requestBody.sessionInfo.parameters.Client;
    const hotelId=requestBody.sessionInfo.parameters.hotelId;
    const businessId=requestBody.sessionInfo.parameters.businessId;
    const apiType=requestBody.sessionInfo.parameters.apiType;

    if (requestBody.sessionInfo.parameters.webhook === 'compareSuites'){
      if(apiType === 'mews'){
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
    else{
      const avvioCompareSuitesResponse = await getActiveRateAndRooms();
      console.log('Index.js', JSON.stringify(avvioCompareSuitesResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "compareSuitesResponse": avvioCompareSuitesResponse
              }
            } 
      };
      console.log('Index.js', responseData);
      return createResponse(200,responseData);
    }
    }


    if (requestBody.sessionInfo.parameters.webhook === 'guestPerRoom'){
      if(apiType === 'mews'){
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
    else{
      const avvioGuestPerRoomResponse = await getGuestCountForRooms();
      console.log('Index.js', JSON.stringify(avvioGuestPerRoomResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "guestPerRoomResponse": avvioGuestPerRoomResponse
              }
            } 
      };
      console.log('Index.js', responseData);
      return createResponse(200,responseData);
    }
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

    if (requestBody.sessionInfo.parameters.webhook === 'getRoomCategories'){
      const getRoomCategoryResponse = await getRoomCategories(client,hotelId);
      console.log('Index.js', JSON.stringify(getRoomCategoryResponse));
      let responseData = {
            "session_info":{
              "parameters": {
                "getRoomCategoryResponse": getRoomCategoryResponse
              }
            } 
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    }

    if (requestBody.sessionInfo.parameters.webhook === 'staticInfo'){
      let type = requestBody.sessionInfo.parameters.type;
      console.log("type"+JSON.stringify(type));
      const hotelInfoResponse = await getStaticData(type,businessId);
      console.log('Index.js', JSON.stringify(hotelInfoResponse));
      // if(apiType==='mews'){
      let responseData = {
            "session_info":{
              "parameters": {
                "hotelInfoResponse": hotelInfoResponse
              }
            }
      };
      console.log('Index.js', responseData);
    return createResponse(200,responseData);
    //  }
    //   else{
    //     const avvioLocationResponse=await getLocationDetails();
    //     console.log("AvvioLocation" +JSON.stringify(avvioLocationResponse));
    //     let responseData = {
    //       "session_info":{
    //         "parameters": {
    //           "hotelInfoResponse": avvioLocationResponse
    //         }
    //       }
    // };
    //  console.log('Index.js', responseData);
    //  return createResponse(200,responseData);
    //   }
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
