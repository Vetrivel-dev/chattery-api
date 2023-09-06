const axios = require('axios');

// Configuration for the client
const CONFIGURATION = {
  credentials: {
      private_key: "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC4sE5vtP3NtyGI\nk3VaW5/ALrU0xuVkzQtED7uuAsi8dcepfg/mL1CPrkwTFR+Gu3ddS2QheAZtRrcr\neHXyU7Fslyc7yCRpzBM2beWtJ7QBJJwIEM1HhvqACrRS9ArTPByg3/LHujbsiVfY\niDlKglEf0QxUGm63j5csESymLkhkPlW2VM0rVBffTT76zohe8HPPYkJMK3krHRqb\n8edxkj6TS2z4PybC4sPpTd1VzdqnFPZDsyIpfzi87f/CRqihWCD6zZ+quAq5IciG\neADnBB9oDTVKmqw0mDnGs3gQ/TeT3syeBviwcToZxSsPUbVV4uIUNGHNZpf8X6LS\nuqCaNpAZAgMBAAECggEAT5unY503XWnXqYAuMHGYDYXUk9g73tRHS+IgOuR2VE3M\nb3GoNsckSZqOrhcHzNKXo/4wPgD3fDjkEXfOF1MeAjmxLqKZl4TgrzB1tMqNW4TF\nQPDFG1TCO1jh23PYCq85g9qkUpUgwZp95Bbr4wMDJty8HLB8EuFw/wy0GzmN+K7s\nLYvH+sFWvKPAdUg21UIWhuo2Ns232u1oaiLwsWAtc5pnnC97HN71mCII5sWCnCx8\nHJTtTaGYnLka0qOtzjM49mK6+bi2/+vLdgmuBnaFeojZcqbLk+oIkfepQ9fyNhWV\n6eJcxt/BmWNTo4FhiDb+ktjlnaIFTX01Com6fm54hQKBgQD9R59vscWBXyK0s301\nZ1HKcPDimkxf0zSvgT8dkNRD7LueQTnwrLcfB5Yff5ldqvTX+jZh+yzmG0BomIDU\nk6sQBl3VOhnveAJ7EHhpoOFfmRxgvhQ/bYO8rpRFfGN7N58ylV4H3NPpEHLqC8ns\nWI5agLl2mX/mkS8kwWmaE7LF2wKBgQC6rBi8uTUclg5h/xp6a4uGJyWz5Gc+mgF0\nTn6+2vYoXNm+nW/O2ulit54okJgR7RTMBOmr0B7GPr0i6itSWvfGKuBi3PgIq8TW\n7ZCPN2CeLYL3817dtIh446rYUyRQw4YdfvJ77icvg4bS7UhJFE6tYqOq5rGkouP7\n5kgpjLa2GwKBgDqmgz0SJXI33YmKpsvfCQmf7agLovPtrJ2XiE4IY573Ctsyr4AT\n/rOoxhK1MYaHN/trbXxy9YqzdM7DwdF4AHzNfJ0Lv0W8vJZS0g9jucyxX+jnGzk0\n2vSuWJdenorB7pOAEy4u7MNjk7iu0itwtkYu2MOl+AAkTo5sW0/aj+NRAoGBAJrL\n32+QffSIksC81O4zhG3oxKEahGsrNo2GEiCeXoY2CPLCh1cXvXV5IpQs443p9dcB\nM+ygFwT6PclYOjh+6vzPzWbuKli2Z/J66ed5WouQ3gxTKpXbAViOIrxkpba7jNmk\nzdvrL199SC5dShYf2uZ455Pu3dzSnw/idHDQGSuFAoGAPwtRGEbo36gWOGFn3/t4\n+DTddEIteVeHoxb8Aq0Ya/e+ii2e92IgT+kwGcTfdWQM+f6SbeQUOMpSadQWOsf9\nfED5lzYb59ZHDPiYHiLc/ONDtRkABpVVyJSQ0dEa95vYmkZFOgekdWjQ+WGO9zAs\nMVZ9I0ujVT1Vr+ht65IeO+s=\n-----END PRIVATE KEY-----\n",
      client_email: "convioservice@aerobic-name-397710.iam.gserviceaccount.com"
  },
  apiEndpoint: 'us-central1-dialogflow.googleapis.com'
}

//Function to get Mews API Request...
const mewsApiRequest = async (Client, HotelId) => {
    try {  
      // Prepare the data payload for the request to the API
      console.log("Calling Mews API");
      const data = JSON.stringify({
        "Client": Client,
        "HotelId": HotelId
      });
      console.log("Client and HotelId "+JSON.stringify(data));
  
      // Configure the request to the API
      const config = {
        method: 'post',
        maxBodyLength: Infinity,
        url: 'https://api.mews-demo.com/api/distributor/v1/hotels/get',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        data: data
      };
  
      // Send the request to the API using axios
      const response = await axios.request(config);
  
      // Return the response data
      return response.data;
    } catch (error) {
      console.log(error);
      return { error: 'Mews API Request Failed' };
    }
  };

  // Function to make API requests
async function makeRequest(url, data) {
    try {
      console.log("Calling Mews API");
      const response = await axios.post(url, data);
      return response.data;
    } catch (error) {
      console.error('Error making API request:', error);
      throw error;
    }
  }

module.exports = {
    CONFIGURATION,
    mewsApiRequest,
    makeRequest,
}