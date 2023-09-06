const{mewsApiRequest} = require('../response_utils/mewsApiUtils');

async function getHotelsWithRestaurant(Client, HotelId) {
    try {
      // Make the API request
      const response = await mewsApiRequest(Client, HotelId);
      const flag = "Restaurant";

      const products = response.Products;

      // Filter products based on title containing "restaurant"
      const roomsInfo = products.filter(product =>
        product.title && product.title.toLowerCase().includes('restaurant')
      );
      if (roomsInfo.length === 0) {
        const errorMessage = 'No restaurant details available.';
        console.log(errorMessage);
        return {flag: flag,response:{roomInfo : errorMessage }};
      }
      console.log("Get Hotels With Restaurant");
      return {flag: flag,response:{roomInfo:roomsInfo}};
    } catch (error) {
      console.error('Error:', error.message);
    }
  }

  const breakfast = async (Client, HotelId) => {
    try {
  
    // Make the API request
    const response = await mewsApiRequest(Client,HotelId)
    const flag = "Breakfast";
  
    // Process the response data
    const products = response.Products;

    if (products) {
      const includeBreakfast = products.filter(product => {
        const enUSName = product.Name && product.Name['en-US'];
        const enGBName = product.Name && product.Name['en-GB'];
        return enUSName && enUSName.includes('Breakfast') || enGBName && enGBName.includes('Breakfast');
      });
      
      const roomsInfo = {
        havingBreakfast: includeBreakfast.length > 0 ? includeBreakfast.map(service => {
          const Name = {
            'en-US': service.Name['en-US'],
            'en-GB': service.Name['en-GB']
          };
          return {
            name: Name,
          };
        }) : 'No services available'
      };

      console.log("Get Hotels With BreakFast");
      return {flag: flag,response:{roomInfo: roomsInfo}};
    } else {  
      return {flag: flag,response:{roomInfo: 'No products available' }};
    }
    
  
    } catch (error) {
      console.log(error);
      return({ error: 'Something went wrong' });
    }
  };  
  module.exports = {
    getHotelsWithRestaurant,
    breakfast
  }