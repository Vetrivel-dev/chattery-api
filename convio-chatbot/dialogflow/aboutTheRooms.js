const{mewsApiRequest} = require('../response_utils/mewsApiUtils')

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

  module.exports = {
    compareSuites,
    guestPerRoom
};