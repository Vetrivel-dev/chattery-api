const { mewsApiRequest } = require('../response_utils/mewsApiUtils');

//Function to get Include in Rate using getHotel Mews API...
const includeInRate = async (Client, HotelId) => {
  try {
    // Make the API request
    const response = await mewsApiRequest(Client, HotelId);
    const flag = "includeInRates";

    // Process the response data
    const products = response.Products;

    const roomsInfo = products.filter(product => product.IncludedByDefault === true);

    const includedServicesFormatted = roomsInfo.length > 0 ? roomsInfo.map(service => {
      const Name = {
        'en-US': service.Name['en-US'],
        'en-GB': service.Name['en-GB']
      };
      const Description = {
        'en-US': service.Description['en-US'],
        'en-GB': service.Description['en-GB']
      };
      // Only return the service if either the Name or Description has content
      if (Name['en-US'] || Name['en-GB'] || Description['en-US'] || Description['en-GB']) {
      return {
        name: Name,
        description: Description
      };
    }
    }) : 'No services available';

    console.log("Get Include In Rate with Name, Description:");
    return {flag: flag,response: { roomInfo: includedServicesFormatted } };

  } catch (error) {
    console.log(error);
    return { error: 'Something went wrong' };
  }
};

//Function to get Paid Separately using getHotel Mews API...
const paidSeperately = async (Client, HotelId) => {
  try {
    // Make the API request
    const responseMews = await mewsApiRequest(Client, HotelId);

    const flag = "paidSeperately";

    // Process the response data
    const response = responseMews.Products;

    const roomsInfo = response.filter(product => product.IncludedByDefault === false);

    const separateServicesFormatted = roomsInfo.length > 0 ? roomsInfo.map(service => {
      const Name = {
        'en-US': service.Name['en-US'],
        'en-GB': service.Name['en-GB']
      };
      const Description = {
        'en-US': service.Description['en-US'],
        'en-GB': service.Description['en-GB']
      };

      // Only return the service if either the Name or Description has content
      if (Name['en-US'] || Name['en-GB'] || Description['en-US'] || Description['en-GB']) {
        return {
          name: Name,
          description: Description
        };
      }
    }) : 'No services available';

    console.log("Get Paid Separately with Name and Description:");
    return {flag: flag, response: { roomInfo: separateServicesFormatted } };

  } catch (error) {
    console.log(error);
    return { error: 'Something went wrong' };
  }
};

module.exports = {
  includeInRate,
  paidSeperately
};
