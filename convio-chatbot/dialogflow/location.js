const{mewsApiRequest} = require('../response_utils/mewsApiUtils');

const location = async (Client, HotelId) => {
  try {
    // Make the API request
    const response = await mewsApiRequest(Client, HotelId);
    const flag = "location";

    // Process the response data
    const address = response.Address;

    if (!address) {
      console.log("Location not available");
      return { flag: flag, response: { roomInfo: "Location not found" } };
    }

    console.log("Get Hotel Address");
    return { flag: flag, response: { roomInfo: address } };
  } catch (error) {
    console.log(error);
    return { error: 'Something went wrong' };
  }
};


  module.exports = {
    location
  }