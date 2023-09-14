const{mewsApiRequest} = require('../response_utils/mewsApiUtils');
const{getLocation} = require('../response_utils/avvioApiUtils')

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

async function getLocationDetails() {
  try {
    const response = await getLocation();
      // Handle the response data here
      const responseData = response.siteList[0].address;
      return responseData;
  } catch (error) {
      console.error('Error fetching data:', error);
      throw error;
  }
}


  module.exports = {
    location,
    getLocationDetails
  }