const {makeRequest, mewsApiRequest} = require('../response_utils/mewsApiUtils');

//To get RoomCategories using getHotel Mews API...
const getRoomCategories = async (Client, HotelId) => {
  try {
      // Make the API request
    const response = await mewsApiRequest(Client, HotelId)
    const flag = "Room Categories List";
    // Process the response data
    const roomCategories = response.RoomCategories;
    const imageBaseUrl = response.ImageBaseUrl;
    const roomInfo = roomCategories.map(room => {
      const imageUrl = room.ImageIds.map(imageId => `${imageBaseUrl}/${imageId}`);
      const id = room.Id;
      const normalBedCount = room.NormalBedCount;
      const extraBedCount = room.ExtraBedCount;
      const spaceType = room.SpaceType;
      const Name = {
        'en-US': room.Name['en-US'],
        'en-GB': room.Name['en-GB']
      };
      // const Description = {
      //   'en-US': room.Description['en-US'],
      //   'en-GB': room.Description['en-GB']
      // };
      return {
        id: id,
        name: Name,
        // description: Description,
        imageUrls: imageUrl,
        normalBedCount: normalBedCount,
        extraBedCount: extraBedCount,
        spaceType: spaceType
      };
    });
    console.log('Getting RoomCategories With Name,Description,ImageBaseURL');
    // Send the processed data as the response
    return({flag:flag,response :{roomInfo: roomInfo}});
  } catch (error) {
    console.log(error);
    return({ error: 'Something went wrong' });
  }
};
  
  //Function to check room availability and get price details
  async function checkRoomAvailability(Client, HotelId, arrivalDate,departureDate,adultCount,childCount,roomInfoReq,emailId,phone,firstName,lastName) {
  const flag = "checkAvailability";
    const url = 'https://api.mews-demo.com/api/distributor/v1/hotels/getAvailability';
  
    const data = {
      "Client": Client,
      "HotelId": HotelId,
      "StartUtc": arrivalDate,
      "EndUtc": departureDate,
      "CurrencyCode": "EUR",
      "AdultCount": adultCount,
      "ChildCount": childCount
    };
    console.log("User Request"+JSON.stringify(data));
  
    try {
      const response = await makeRequest(url, data);
      const roomInfo = [];
      let availableRoomCount = null;
      const roomInformation = response.RoomCategoryAvailabilities.filter(availability =>
        roomInfoReq.roomCategoryId.includes(availability.RoomCategoryId)
      );
      roomInformation.forEach(room => {
        availableRoomCount = room.AvailableRoomCount;
        room.RoomOccupancyAvailabilities.forEach(occupancy => {
          occupancy.Pricing.forEach(price => {
            const image = roomInfoReq.image;
            const rateId = price.RateId;
            const rate = response.Rates.find(rate =>
             rateId.includes(rate.Id)
              );
            const totalPrice = price.Price.Total.EUR;
            if(rate){
              roomInfo.push({
              name: rate.Name,
              rateId: rateId,
              imageUrls :[image],
              totalPrice: totalPrice
          });
        }
          });
        });
      });
      console.log("Getting RateName,RateId,TotalPrice and imageUrls using RoomCategoryID:")
      return({flag:flag,response :{roomCategoryDetails: {roomCategoryId:roomInfoReq.roomCategoryId, name:roomInfoReq.name, availableRoomCount:availableRoomCount},roomInfo}});
    } catch (error) {
      console.log(error);
      return { error: 'Something went wrong' };
    }
  }

// const getRoomCategories = async (Client, HotelId, arrivalDate, departureDate, adultCount, childCount) => {
//   try {
//     // Make the API request to get room categories
//     const roomCategoriesResponse = await mewsApiRequest(Client, HotelId);
//     const roomCategories = roomCategoriesResponse.RoomCategories;
//     const imageBaseUrl = roomCategoriesResponse.ImageBaseUrl;

//     // Make the API request to get room availability
//     const availabilityData = {
//       "Client": Client,
//       "HotelId": HotelId,
//       "StartUtc": arrivalDate,
//       "EndUtc": departureDate,
//       "CurrencyCode": "EUR",
//       "AdultCount": adultCount,
//       "ChildCount": childCount
//     };
//     const availabilityResponse = await makeRequest('https://api.mews-demo.com/api/distributor/v1/hotels/getAvailability', availabilityData);

//     const roomInfo = roomCategories.map(room => {
//       const imageUrl = room.ImageIds.map(imageId => `${imageBaseUrl}/${imageId}`);
//       const id = room.Id;
//       const normalBedCount = room.NormalBedCount;
//       const extraBedCount = room.ExtraBedCount;
//       const spaceType = room.SpaceType;
//       const Name = {
//         'en-US': room.Name['en-US'],
//         'en-GB': room.Name['en-GB']
//       };
//       const Description = {
//         'en-US': room.Description['en-US'],
//         'en-GB': room.Description['en-GB']
//       };

//       // Find availability data for this room category
//       const availability = availabilityResponse.RoomCategoryAvailabilities.find(avail => avail.RoomCategoryId === id);
//       const availableRoomCount = availability ? availability.AvailableRoomCount : 0;

//       const roomRates = [];
//       if (availability) {
//         availability.RoomOccupancyAvailabilities.forEach(occupancy => {
//           occupancy.Pricing.forEach(price => {
//             const rateId = price.RateId;
//             const rate = availabilityResponse.Rates.find(rate => rate.Id === rateId);
//             const totalPrice = price.Price.Total.EUR;

//             if (rate) {
//               roomRates.push({
//                 rateName: rate.Name,
//                 rateType: rate.Type,
//                 rateId: rateId,
//                 totalPrice: totalPrice
//               });
//             }
//           });
//         });
//       }

//       return {
//         id: id,
//         name: Name,
//         description: Description,
//         imageUrls: imageUrl,
//         extraBedCount: extraBedCount,
//         spaceType: spaceType,
//         availableRoomCount: availableRoomCount,
//         rates: roomRates,
//         guestPerRoom: normalBedCount,
//       };
//     });

//     console.log('Getting RoomCategories With Name,Description,ImageBaseURL');

//     return { flag: 'Room Categories List', response: { roomInfo: roomInfo } };
//   } catch (error) {
//     console.log(error);
//     return { error: 'Something went wrong' };
//   }
// };

  //Function to get Room Confirmation by user...
  async function roomConfirmation(Client,HotelId,arrivalDate,departureDate,adultCount,childCount,roomInfo,emailId,phone,firstName,lastName,configurationId) {
  
      const url = 'https://api.mews-demo.com/api/distributor/v1/reservationGroups/create';
    
      const data = {
          "Client": Client,
          "ConfigurationId": configurationId,
          "HotelId": HotelId,
          "Customer": {
              "Email": emailId,
              "FirstName": firstName,
              "LastName": lastName,
              "Telephone": phone,
              "AddressLine1": "",
              "AddressLine2": "",
              "City": "",
              "PostalCode": "",
              "StateCode": "",
              "NationalityCode": "",
              "SendMarketingEmails": false
          },
          "Reservations": [
              {
                  "RoomCategoryId": roomInfo.roomCategoryId,
                  "StartUtc": arrivalDate,
                  "EndUtc": departureDate,
                  "RateId": roomInfo.rateId,
                  "AdultCount": adultCount,
                  "ChildCount": childCount,
                  "Notes": "Quiet room please."
              }
          ]
      };
      console.log("User Request: "+JSON.stringify(data));    
      try {
        const response = await makeRequest(url, data);
        console.log("Get Room Confirmation by User:");  
        return({roomInfo: response});
      } catch (error) {
        console.log(error);
        return { error: 'Something went wrong' };
      }
    }

    //Function to Check Reservation Id in Booking Confirmation....
    async function checkReservation(Client, HotelId, reservationGroupId) {
      const flag = "checkReservation";
    
        const url = 'https://api.mews-demo.com/api/distributor/v1/reservationGroups/get';
      
        const data = {
          "Client": Client,
          "HotelId": HotelId,
          "ReservationGroupId": reservationGroupId,
          "Extent": {
                "PaymentRequests": true,
                "Payments": true
          }
        };
        console.log("User Request"+JSON.stringify(data));
      
        try {
          const response = await makeRequest(url, data);
          const customerId = response.Id;
          const paymentRequests = response.PaymentRequests;
          let message;
          if (!paymentRequests) {
            message = 'Reservation status could not be determined';
          } else if (paymentRequests.some(request => request.ReservationGroupId === reservationGroupId && request.State === 'Completed')) {
            message = 'Reservation available for that date and currently in Confirmed status';
          } else {
            message = 'Reservation available for that date and pending in Payment section';
          }
          console.log("Checking Reservation Status:");
        return message;
      } catch (error) { 
        console.error('Error:', error.message);
        throw error;
      }
  }

  
  module.exports = {
    checkRoomAvailability,
    getRoomCategories,
    roomConfirmation,
    checkReservation
    //checkRoomAvailabilityForGroupBooking,
    //roomConfirmationForGroupBooking
  };