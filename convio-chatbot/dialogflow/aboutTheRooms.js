const{mewsApiRequest} = require('../response_utils/mewsApiUtils')
const { getRateRoomMapping, getGuestCount } = require('../response_utils/avvioApiUtils');

//To get CompareSuites using getHotel Mews API...
const compareSuites = async (Client, HotelId) => {
    try {
        // Make the API request
      const response = await mewsApiRequest(Client, HotelId);
      const flag = "Compare Suites";
      // Process the response data
      const roomCategories = response.RoomCategories;
      const imageBaseUrl = response.ImageBaseUrl;
  
      const roomInfo = roomCategories.map(room => {
        const imageUrl = room.ImageIds.map(imageId => `${imageBaseUrl}/${imageId}`);
        const Name = {
          'en-US': room.Name['en-US'],
          'en-GB': room.Name['en-GB']
        };
        const Description = {
          'en-US': room.Description['en-US'],
          'en-GB': room.Description['en-GB']
        };
        return {
          name: Name,
          description: Description,
          category: room.Category,
          imageUrls: imageUrl,
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

  //To get Guest per Room using getHotel Mews Api...
  const guestPerRoom = async (Client, HotelId) => {
    try {
    const response = await mewsApiRequest(Client, HotelId);
    const flag = "Guest per room";    
    // Process the response data
    const roomCategories = response.RoomCategories;
    const imageBaseUrl = response.ImageBaseUrl;

    const roomInfo = roomCategories.map(room => {
    const imageUrl = room.ImageIds.map(imageId => `${imageBaseUrl}/${imageId}`);

      const Name = {
        'en-US': room.Name['en-US'],
        'en-GB': room.Name['en-GB']
      };
      const Description = {
        'en-US': room.Description['en-US'],
        'en-GB': room.Description['en-GB']
      };
        return {
            name: Name,
            description: Description,
            guestPerRoom: room.NormalBedCount,
            imageUrls: imageUrl
          };
        });

        console.log("Getting RoomCategories With Name,Description,NormalBedCount");
        return{flag:flag,response:{roomInfo : roomInfo}};
  
    } catch (error) {
      console.log(error);
      return({ error: 'Something went wrong' });
    }
  }; 


const getActiveRateAndRooms = async () => {

  try {
      const flag = "AVVIO_COMPARE_SUITES";
      const response = await getRateRoomMapping();

      // Make the Avivo API request
      const siteList = response.siteList;

      if (siteList && siteList.length > 0) {
        const rates = siteList[0].rates;

        // Find the first rate with status "active"
        const activeRate = rates.find(rate => rate.status === 'active');
 
        if (activeRate) {

          // Get the rooms inside the active rate
          const activeRateRooms = activeRate.rooms;
          console.log('Active Rate Details:');

          console.log(activeRate);
          console.log('Rooms inside the Active Rate:');

          activeRateRooms.forEach(room => {
            return(`Room ID: ${room.roomID}, Room Code: ${room.roomCode}`);
          });
          return ({flag:flag,response:{roomInfo :{activeRate,activeRateRooms}}});
        } else {
          return('No active rates found.');
        }
      } else {
        return('No data found for the site.');
      }
    } catch (error) {
    console.log(error);
    return({ error: 'Something went wrong' });
  }
};

async function getGuestCountForRooms() {
  try {

    const flag = "AVVIO_GUEST_PER_ROOM";
    const response = await getGuestCount();
    
      // Handle the response data here
      const responseData = response.data;
      return {flag:flag,response:{roomInfo :responseData}};
  } catch (error) {
      console.error('Error fetching data:', error);
      throw error;
  }
}



  module.exports = {
    compareSuites,
    guestPerRoom,
    getActiveRateAndRooms,
    getGuestCountForRooms
};