const axios = require('axios');
const currentDate = new Date();
const year = currentDate.getFullYear();
const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // Month is zero-based
const day = String(currentDate.getDate()).padStart(2, '0');

const formattedDate = `${year}-${month}-${day}`;
console.log(formattedDate);

// Create a function to make the Mews API request
const getRateRoomMapping = async () => {
    try {  

      // Prepare the data payload for the request to the API
      console.log("Calling Avivo API");
      const data = {
        userAuth: {
          username: 'fullcircledataapi',
          password: 'Dgb4j24tMW'
          },
          operation: 'getRateRoomMapping',
          siteIDList: [15145]
      };
      console.log("Request Body "+JSON.stringify(data));
  
      const response = await axios.post('https://api.avvio.com/api/ws_api', data);
      return response.data;
    } catch (error) {
      console.error('Avvio API Request Failed:', error);
      throw new Error('Avvio API Request Failed');
    }
  };

  const getGuestCount = async () => {
    try {  

      // Prepare the data payload for the request to the API
      console.log("Calling Avivo API");
      const data = {
        userAuth: {
            username: 'fullcircledataapi',
            password: 'Dgb4j24tMW'
        },
        operation: 'getRates',
        siteIDList: [15145],
        rateIDList: [75498101],
        startDate: formattedDate,
        inventoryHorizon: 2,
        losOptions: [1]
    };
      console.log("Request Body "+JSON.stringify(data));
  
      const response = await axios.post('https://api.avvio.com/api/ws_api', data);
      return response;
    } catch (error) {
      console.error('Avvio API Request Failed:', error);
      throw new Error('Avvio API Request Failed');
    }
  };

  const getLocation = async () => {
    try {  
      // Prepare the data payload for the request to the API
      console.log("Calling Avivo API");
      const data = {
        userAuth: {
          username: 'fullcircledataapi',
          password: 'Dgb4j24tMW',
        },
        operation: 'getPropertyInformation',
        siteIDList: [15145],
        options: ['fullPropertyInformation'],
      };
      console.log("Request Body "+JSON.stringify(data));
      const response = await axios.post('https://api.avvio.com/api/ws_api', data);
      return response.data;
    } catch (error) {
      console.error('Avvio API Request Failed:', error);
      throw new Error('Avvio API Request Failed');
    }
  };


  module.exports = {
    getRateRoomMapping,
    getGuestCount,
    getLocation  
  }