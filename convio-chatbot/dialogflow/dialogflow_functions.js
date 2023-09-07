
const dialogflow = require('@google-cloud/dialogflow-cx');
const {PROJECID} =require('../config/dialogFlowConfig');
const {structProtoToJson,valueProtoToJson} =require('../response_utils/conversionUtils');
const {CONFIGURATION,response} = require('../response_utils/mewsApiUtils');
const {struct} = require('pb-util');
const { Console } = require('console');
const axios = require('axios');
const {sendEmail,sendEmailToClient,generateEmailTemplateForBooking,generateEmailTemplateForBookingClient,generateEmailTemplateForGroupBooking,generateEmailTemplateForGroupBookingClient} =require('../utils/emailUtils');
const {roomConfirmation,checkReservation,getRoomCategories,checkRoomAvailability } = require('./reservation');
const {getConfigurationId,insertCustomerId,getReservationGroupId,getClientIdHotelIdByBusinessId} = require('../utils/connection');
const dialogFlowLocation = process.env.DIALOGFLOW_LOCATION;
const dialogflowAgents = process.env.DIALOGFLOW_AGENTS;

// Create a new session
const sessionClient = new dialogflow.SessionsClient(CONFIGURATION);

// Detect intent method
const detectIntent = async (languageCode, queryText, sessionId,businessId,roomInfo) => {
    const clientHotel = await getClientIdHotelIdByBusinessId(businessId);
    const client=clientHotel.mewsClientId;
    const hotelId=clientHotel.mewsHotelId;

    // For Local
    
    let sessionPath = sessionClient.projectLocationAgentSessionPath(PROJECID,"us-central1","5495f9d3-3760-4b1a-8236-01f233ed1e00", sessionId);

    // For Server

    // let sessionPath = sessionClient.projectLocationAgentSessionPath(PROJECID,dialogFlowLocation,dialogflowAgents,sessionId);

    //The text query request.
    let request = {
        session: sessionPath,
        queryInput: {
            text: {
                text: queryText,
            },
            languageCode
        },queryParams: {
            parameters: {
              fields: {
                Client: { kind: 'stringValue', stringValue: client },
                hotelId: { kind: 'stringValue', stringValue: hotelId },
                businessId: { kind: 'stringValue', stringValue: businessId}
              }
            }
          }
    };

    // Send request and log result
   const resp = await sessionClient.detectIntent(request);
//    console.log("result"+JSON.stringify(resp));
   if(resp[0].queryResult.responseMessages){
    if(resp[0].queryResult.responseMessages[0]){
        finalResp=(resp[0].queryResult.responseMessages[0]);
    }
   }

   const intent= (resp[0].queryResult.intent);
    let buttons=null;
    let hint=null;
    let carouselType="Carousel";
    let tableType="Table";
    let listType="List";
    let form="Form";
    let responseString= null;
    let datePicker=false

    const params = structProtoToJson(resp[0].queryResult.parameters);
    console.log("Request"+JSON.stringify(params));

    let displayName= null;

    if(intent){ //true if indent was not null
         displayName=intent.displayName;
    }else{
        if(params.webhook){
        if(params.webhook.toLowerCase() === 'chatgpt'){
            displayName='chatGPT';
        }
    }
    }
    console.log("Resp  :: "+JSON.stringify(finalResp));
    console.log("displayName  :: "+displayName);
    console.log("webhook  :: "+JSON.stringify(params.webhook));
    console.log("intent  :: "+JSON.stringify(intent));
 

    if(displayName === 'Compare Suites'){
    const response = params.compareSuitesResponse;
        return  ({
            "type":carouselType,
            "webhookResponse":response,
            "buttons":buttons,
            "hint":hint
        });
    }

    if(displayName === 'Guests per room'){
        const response = params.guestPerRoomResponse;
        return({
            "type":carouselType,
            "webhookResponse":response,
            "buttons":buttons,
            "hint":hint
        });
    }

    if(displayName === 'Include in rates'){
        const response = params.includeInRateResponse;
        return({
            "type":carouselType,
            "webhookResponse":response,
            "buttons":buttons,
            "hint":hint
        });
    }

    if(displayName === 'paid separately'){
        const response = params.paidSeperatelyResponse;
        return({
            "type":carouselType,
            "webhookResponse":response,
            "buttons":buttons,
            "hint":hint
        });
    }
    
    // if(displayName === 'Breakfast'){
    //     const response = params.breakFastResponse;
    //     return({
    //         "type":listType,
    //         "webhookResponse":response,
    //         "buttons":buttons,
    //         "hint":hint
    //     });
    // }

    // if(displayName === 'Restaurant'){
    //     const response = params.getHotelsWithRestaurantResponse;
    //     return({
    //         "type":listType,
    //         "webhookResponse":response,
    //         "buttons":buttons,
    //         "hint":hint
    //     });
    // }

    // if(displayName === 'Location'){
    //     const response = params.locationResponse;
    //     return({
    //         "type":tableType,
    //         "webhookResponse":response,
    //         "buttons":buttons,
    //         "hint":hint
    //     });
    // }

    // if(displayName === 'Booking quotation'){
    //     return({
    //         "type":form,
    //         "webhookResponse":{"flag":"Room Booking Details"}
    //     })
    // }
    
    // if(displayName === 'Booking quotation webhook'){

    //     const formattedArrivalDate = roomInfo.checkInDate;
    //     const formattedDepartureDate = roomInfo.checkOutDate;
    //     const adultCount = roomInfo.adultCount;
    //     const childCount = roomInfo.childrenCount;
    //     const roomCount = roomInfo.roomCount;
        
    //     const response = await getRoomCategories (client, hotelId, formattedArrivalDate, formattedDepartureDate, adultCount, childCount);
    //     return({
    //         "type":carouselType,
    //         "webhookResponse":response,
    //         "buttons":buttons,
    //         "hint":hint,
    //     });
    // }

    if(displayName === 'Number of guests children'){
        const response = params.getRoomCategoryResponse;
        return({
            "type":carouselType,
            "webhookResponse":response,
            "buttons":buttons,
            "hint":hint,
            "text":"Please Choose a Room Category"
        });
    }

    if ( displayName === 'Check availability') {
      const firstName = params.first_name;
      const lastName = params.last_Name;
      const emailId = params.email;
      const phone = params.phone_number;
      const yearArr = params.arrivaldate.year;
      const dayArr = params.arrivaldate.day;
      const monthArr = params.arrivaldate.month;
      const formattedArrivalDate = `${yearArr}-${monthArr}-${dayArr}`;
    
      const year1 = params.departuredate.year;
      const day1 = params.departuredate.day;
      const month1 = params.departuredate.month;
      const formattedDepartureDate = `${year1}-${month1}-${day1}`;
    
      const adultCount = params.no_adult;
      const childCount = params.no_children;
    //   const hotelIdReq = hotelId;
      const checkAvailability = await checkRoomAvailability(client,hotelId,formattedArrivalDate,formattedDepartureDate,adultCount,childCount,roomInfo,emailId,phone,firstName,lastName);
    //   await sendEmail(hotelIdReq, "Booking Quotation", emailContent);
    //   const message = 'Great! I’ve sent your message to our reservations team, and they will reply as soon as possible!';
      return({
        "type" : carouselType,
        "webhookResponse":checkAvailability,
        "buttons" : buttons,
        "hint" : hint
      })
    }
    if(displayName == 'Booking Send email'){
      const firstName = params.first_name;
      const lastName = params.last_name;
      const emailId = params.email;
      const phone = params.phone_number;
      const yearArr = params.arrivaldate.year;
      const dayArr = params.arrivaldate.day;
      const monthArr = params.arrivaldate.month;
      const formattedArrivalDate = `${yearArr}-${monthArr}-${dayArr}`;
      const year1 = params.departuredate.year;
      const day1 = params.departuredate.day;
      const month1 = params.departuredate.month;
      const formattedDepartureDate = `${year1}-${month1}-${day1}`;
      const adultCount = params.no_adult;
      const childCount = params.no_children;
      const finalResponse = "Reservation completed successfully and email will be sent for confirmation.  Please complete payment as per email";
      const configurationId = await getConfigurationId(hotelId);
    const customerDetails = await roomConfirmation(client, hotelId, formattedArrivalDate,formattedDepartureDate,adultCount,childCount,roomInfo,emailId,phone,firstName,lastName,configurationId);
    console.log("CustomerDetails"+JSON.stringify(customerDetails));
    const customerId = customerDetails.roomInfo.CustomerId;
    const rateId = customerDetails.roomInfo.Reservations[0].RateId;
    const categoryId = customerDetails.roomInfo.Reservations[0].RoomCategoryId;
    const reservationGroupId= customerDetails.roomInfo.Id;    
    await insertCustomerId(customerId,categoryId,rateId,firstName,lastName,emailId,phone,formattedArrivalDate,formattedDepartureDate,reservationGroupId);
    const emailContent = await generateEmailTemplateForBooking(firstName+' '+lastName, emailId, phone, formattedArrivalDate, formattedDepartureDate, adultCount, childCount, roomInfo.name, roomInfo.totalPrice);
    await sendEmail(hotelId, "Booking Quotation", emailContent);
    const emailContentToClient = await generateEmailTemplateForBookingClient(firstName+' '+lastName, emailId, phone, formattedArrivalDate, formattedDepartureDate, adultCount, childCount, roomInfo.name, roomInfo.totalPrice);
    await sendEmailToClient(emailId, "Booking Quotation", emailContentToClient);
    const userDetails = emailContentToClient.replace(/<[^>]*>/g, '');
    console.log("EmailResult" + JSON.stringify(userDetails));
    return({
        "text" : finalResponse,
        "userDetails" : userDetails,
        "buttons" : buttons,
        "hint" : hint
      })
    }


    if(displayName === 'Confirmation Email'){

        const emailId = params.email;
        const name = params.name;
        const yearArr = params.arrivaldate.year;
        const dayArr = params.arrivaldate.day;
        const monthArr = params.arrivaldate.month;
        const formattedArrivalDate = `${yearArr}-${monthArr}-${dayArr}`;
        const reservationGroupId = await getReservationGroupId(emailId,formattedArrivalDate);

        const response = await checkReservation(client,hotelId,reservationGroupId);
        return({
            "text":response,
            "buttons":buttons,
            "hint":hint
        });
    }

    if(displayName == 'Number of rooms'){

        const firstName = params.gfirst_name;
        const lastName = params.glast_name;
        const emailId = params.email;
        const phone = params.number;
        const yearArr = params.arrivaldate.year;
        const dayArr = params.arrivaldate.day;
        const monthArr = params.arrivaldate.month;
        const formattedArrivalDate = `${yearArr}-${monthArr}-${dayArr}`;
        const year1 = params.departuredate.year;
        const day1 = params.departuredate.day;
        const month1 = params.departuredate.month;
        const formattedDepartureDate = `${year1}-${month1}-${day1}`;
        const adultCount = params.no_people;
        const averageAge = params.avg_age;
        const rooms = params.no_rooms

        const finalResponse = "Reservation completed successfully and email will be sent for confirmation.  Please complete payment as per email";

        // const checkAvailability = await checkRoomAvailabilityForGroupBooking(client, hotelId, formattedArrivalDate,formattedDepartureDate,adultCount,roomInfo,emailId,phone,firstName,lastName,averageAge,rooms);
        // console.log("groupChecking"+JSON.stringify(checkAvailability));

        // const finalResponse = "Reservation completed successfully and email will be sent for confirmation.  Please complete payment as per email";
        // const configurationId = await getConfigurationId(hotelId);

        // const customerDetails = await roomConfirmationForGroupBooking(client, hotelId, formattedArrivalDate,formattedDepartureDate,adultCount,averageAge,roomInfo,emailId,phone,firstName,lastName,configurationId,rooms);
        // console.log("CustomerDetails"+JSON.stringify(customerDetails));

        // const customerId = customerDetails.roomInfo.CustomerId;
        // const rateId = customerDetails.roomInfo.Reservations[0].RateId;
        // const categoryId = customerDetails.roomInfo.Reservations[0].RoomCategoryId;
        // const reservationGroupId= customerDetails.roomInfo.Id;    
        // await insertCustomerIdForGroupBooking(customerId,categoryId,rateId,firstName,lastName,emailId,phone,formattedArrivalDate,formattedDepartureDate,reservationGroupId,averageAge,rooms);
        const emailContent = await generateEmailTemplateForGroupBooking(firstName+' '+lastName, emailId, phone, formattedArrivalDate, formattedDepartureDate, adultCount, averageAge, rooms);
        await sendEmail(hotelId, "Group Booking Quotation", emailContent);

        const emailContentToClient = await generateEmailTemplateForGroupBookingClient(firstName+' '+lastName, emailId, phone, formattedArrivalDate, formattedDepartureDate, adultCount, averageAge, rooms);
        await sendEmailToClient(emailId, "Group Booking Quotation", emailContentToClient);
        const userDetails = emailContentToClient.replace(/<[^>]*>/g, '');
        console.log("EmailResult" + JSON.stringify(userDetails));
        return({
          "text":finalResponse,
          "userDetails" : userDetails,
          "buttons" : buttons,
          "hint" : hint
        })
      }

    if(displayName === 'Bank account information' ||
       displayName === 'Cancellation policy' ||
       displayName === 'Breakfast' ||
       displayName === 'Cash payments' ||
       displayName === 'Check-In / Check-out' ||
       displayName === 'Credit and debit cards' ||
       displayName === 'Events' ||
       displayName === 'Airplane' ||
       displayName === 'Bus' ||
       displayName === 'Car' ||
       displayName === 'Transportation and shuttle' ||
       displayName === 'Opening hours' ||
       displayName === 'I will be late for my check-in' ||
       displayName === 'Late payments' ||
       displayName === 'Location' ||
       displayName === 'Luggage' ||
       displayName === 'Pets' ||
       displayName === 'Pre arrival' ||
       displayName === 'Special requests' ||
       displayName === 'Restaurant' ||
       displayName === 'Services and amenities' ||
       displayName === 'Weddings'){
        const response = params.hotelInfoResponse;
        return({
            "text":response,
            "buttons":buttons,
            "hint":hint
        });
    }

      if(displayName=== 'chatGPT' ){
        if(finalResp.text!=undefined){
            responseString=finalResp.text.text[0];
            return  ({
                "text":responseString,
                "buttons":buttons,
                "hint":hint
            });
        }
    }

    if( displayName === 'Arrival date' || displayName === 'Phone Number' 
       || displayName==='Average Age' || displayName === 'Name'){
        //Enabling datepicker for next Inputs
        datePicker=true;
    }
    
    const customPayload=structProtoToJson(finalResp.payload).convio_custom_payload;
    if(customPayload!=undefined){
        let payload=customPayload.attachment.payload;
        if(payload){
            responseString=payload.messages.texts;
            if(payload.messages.richcontents){
            buttons=payload.messages.richcontents[0].content.buttons;
            }
            hint=payload.messages.texts[0].hint;
        }

        return  ({
            "text":responseString,
            "buttons":buttons,
            "hint":hint,
            "datePicker":datePicker
        });
    }
}
  
module.exports = {
    detectIntent
};