// Assuming you are using a SQL database with the 'mysql2' library for the database connection
const mysql = require('mysql2/promise');

// For Local

const dbConfig = {
  host: 'convio-api-dev.c2gz9p8itqtx.eu-west-2.rds.amazonaws.com',
  user: 'admin',
  password: 'M#d4Ktre3ToY&8F',
  database: 'convio_api_dev',
};

// For Cloud

// const dbConfig = {
//   host: process.env.DATASOURCE_HOST_NAME,
//   user: process.env.DATASOURCE_USERNAME,
//   password: process.env.DATASOURCE_PASSWORD,
//   database: process.env.DATASOURCE_DB_NAME,
// };

// To Get HotelEmail Using HotelId From Business....
const getHotelEmailById = async (hotelId) => {
  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log("Database Connection"+JSON.stringify(connection));
    const [rows] = await connection.execute('SELECT hotel_email FROM business WHERE mews_hotel_id = ?', [hotelId]);
    console.log("getHotelEmailById");
    connection.end();

    if (rows.length === 0) {
      throw new Error(`HotelId ${hotelId} not found.`);
    }

    return rows[0].hotel_email;
  } catch (error) {
    console.error('Error while fetching hotel email:', error.message);
    throw error;
  }
};

// To Get ConfigurationId Using HotelId From Business....
const getConfigurationId = async (hotelId) => {
  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log("Database Connection"+JSON.stringify(connection));
    const [rows] = await connection.execute('SELECT configuration_id FROM business WHERE mews_hotel_id = ?', [hotelId]);
    console.log("getConfigurationId");
    connection.end();

    if (rows.length === 0) {
      throw new Error(`HotelId ${hotelId} not found.`);
    }

    return rows[0].configuration_id;
  } catch (error) {
    console.error('Error while fetching configurationId:', error.message);
    throw error;
  }
};

// To Add User Details In Customers....
async function insertCustomerId(customerId, categoryId, rateId, firstName, lastName, email, phone, startDate, endDate,reservationGroupId) {
  const query = 'INSERT INTO customers (id, firstname, lastname, email, phone, room_category_id, rate_id, start_date, end_date,reservation_group_id,booking_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,"Single Booking")';
  const values = [customerId, firstName, lastName, email, phone, categoryId, rateId, startDate, endDate,reservationGroupId];
  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log("Database Connection"+JSON.stringify(connection));
    const insertResult = await connection.query(query, values);

    console.log('CustomerId inserted:'+JSON.stringify(insertResult));

    connection.end();
    return insertResult;
  } catch (error) {
    console.error('Error while inserting customer details:', error);
    throw error;
  }
};

// async function insertCustomerIdForGroupBooking(customerId, categoryId, rateId, firstName, lastName, email, phone, startDate, endDate,reservationGroupId,averageAge,rooms) {
//   const query = 'INSERT INTO customers (id, firstname, lastname, email, phone, room_category_id, rate_id, start_date, end_date,reservation_group_id,booking_type,average_age,no_rooms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,"Group Booking",?,?)';
//   const values = [customerId, firstName, lastName, email, phone, categoryId, rateId, startDate, endDate,reservationGroupId,averageAge,rooms];
//   try {
//     const connection = await mysql.createConnection(dbConfig);
//     const insertResult = await connection.query(query, values);

//     console.log('CustomerId inserted:'+JSON.stringify(insertResult));

//     connection.end();
//     return insertResult;
//   } catch (error) {
//     console.error('Error inserting CustomerId:', error);
//     throw error;
//   }
// };

// To Get Reservation GroupId From Customers....
const getReservationGroupId = async (email, arrivalDate) => {
  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log("Database Connection" + JSON.stringify(connection));
    const [rows] = await connection.execute('SELECT reservation_group_id FROM customers WHERE email = ? and start_date = ?', [email, arrivalDate]);
    console.log("getReservationGroupId");
    connection.end();

    if (rows.length === 0) {
      return "Invalid Date"; // No reservation found, return message
    }

    return rows[0].reservation_group_id;
  } catch (error) {
    console.error('Error while fetching reservation group id:', error.message);
    throw error;
  }
};

// To Get ClientId and HotelId Using BusinessId from Business....
const getClientIdHotelIdByBusinessId = async (businessId) => {
  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log("Database Connection"+JSON.stringify(connection));
    const [rows] = await connection.execute('SELECT mews_client_id,mews_hotel_id FROM business WHERE id = ?', [businessId]);
    console.log("getClientIdHotelIdByBusinessId");
    connection.end();

    if (rows.length === 0) {
      throw new Error(`BusinessId ${businessId} not found.`);
    }

    return {
      mewsClientId: rows[0].mews_client_id,
      mewsHotelId: rows[0].mews_hotel_id
    };  
  } catch (error) {
    console.error('Error while fetching hotelId and clientId by businessId:', error.message);
    throw error;
  }
};

// To Get Static Data From HotelInfo.... 
const getStaticData = async (typeName,businessId) => {
  try {
    const connection = await mysql.createConnection(dbConfig);
    console.log("Database Connection"+JSON.stringify(connection));
    const [rows] = await connection.execute('SELECT text FROM hotel_info WHERE type = ? and business_id=? ', [typeName,businessId]);
    console.log("getStaticData");
    connection.end();

    if (rows.length === 0) {
      throw new Error(`TypeName:${typeName},BusinessId:${businessId} not found.`);
    }

    return rows[0].text;  
  } catch (error) {
    console.error('Error while fetching text by businessId & type:', error.message);
    throw error;
  }
};

module.exports = {
  getHotelEmailById,
  getConfigurationId,
  insertCustomerId,
  //insertCustomerIdForGroupBooking,
  getReservationGroupId,
  getClientIdHotelIdByBusinessId,
  getStaticData
};
