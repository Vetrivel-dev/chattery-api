const nodemailer = require('nodemailer');
const database = require('../utils/connection');

// Gmail SMTP configuration
const smtpConfig = {
  host: 'smtp.elasticemail.com',
  port: 2525,
  secure: false,
  auth: {
    user: 'contactus.dev99@gmail.com',
    pass: '60F2DDBDE37BD38FA58DD89F2A3C327A3C41',
  },
};

const sendEmail = async (hotelId, subject, body) => {
  try {
    // Validate hotelId
    if (!hotelId) {
      throw new Error('Invalid hotelId provided.');
    }

    // Fetch recipient's email address from the database based on the provided hotelId
    const recipientEmail = await database.getHotelEmailById(hotelId);

    // Validate recipientEmail
    if (!recipientEmail) {
      throw new Error(`No email address found for hotelId ${hotelId}.`);
    }

    const transporter = nodemailer.createTransport(smtpConfig);
    const mailOptions = {
      from: smtpConfig.auth.user,
      to: recipientEmail,
      subject: subject,
      html: body,
    };
    console.log("Sending Email");
    const info = await transporter.sendMail(mailOptions);
    console.log('Email sent:', info.messageId);
    return true;
  } catch (error) {
    console.error('Error while sending email:', error.message);
    return false;
  }
};

const sendEmailToClient = async (emailId, subject, body) => {
  try {
    // Validate hotelId
    if (!emailId) {
      throw new Error('Invalid emailId provided.');
    }

    const transporter = nodemailer.createTransport(smtpConfig);
    const mailOptions = {
      from: smtpConfig.auth.user,
      to: emailId,
      subject: subject,
      html: body,
    };
    console.log("Sending Email");
    const info = await transporter.sendMail(mailOptions);
    console.log('Email sent:', info.messageId);
    return true;
  } catch (error) {
    console.error('Error while sending email:', error.message);
    return false;
  }
};


async function generateEmailTemplateForBooking(customerName, email, phone, arrivalDate, departureDate, noOfAdults, noOfChildren, roomCategory, totalPrice) {

  let emailContent = `
  <h1>Booking Quotation Details</h1>
  <p>Dear Sir/Madam,</p>
  <p>Customer has been booked a Quote with below details, please take a look and reach them with below details.</p>
  <ul>
  <li>Customer Name: ${customerName}</li>
  <li>Email: ${email}</li>
  <li>Phone: ${phone}</li>
  <li>Arrival Date: ${arrivalDate}</li>
  <li>Departure Date: ${departureDate}</li>
  <li>No of Adults: ${noOfAdults}</li>
  <li>No of Children: ${noOfChildren}</li>
  <li>Room Category: ${roomCategory}</li>
  <li>Total Price: ${totalPrice}</li>
  </ul>
  <p>Thanks,<br />Convio.</p>
    `;
    return emailContent;
  }

  async function generateEmailTemplateForBookingClient(customerName, email, phone, arrivalDate, departureDate, noOfAdults, noOfChildren, roomCategory, totalPrice) {

    let emailContent = `
    <h1>Booking Quotation Confirmation</h1>
    <p>Hi ${customerName.split(' ')[0]},</p>
    <p>Please find the the Booking Quotation Confirmation details below</p>
    <ul>
    <li>Customer Name: ${customerName}</li>
    <li>Email: ${email}</li>
    <li>Phone: ${phone}</li>
    <li>Arrival Date: ${arrivalDate}</li>
    <li>Departure Date: ${departureDate}</li>
    <li>No of Adults: ${noOfAdults}</li>
    <li>No of Children: ${noOfChildren}</li>
    <li>Room Category: ${roomCategory}</li>
    <li>Total Price: ${totalPrice}</li>
    </ul>
    <p>Thanks,<br />Convio.</p>
      `;
      return emailContent;
    }

    async function generateEmailTemplateForGroupBooking(customerName, email, phone, arrivalDate, departureDate, noOfAdults, averageAge, rooms) {

      let emailContent = `
      <h1>Group Booking Quotation Details</h1>
      <p>Dear Sir/Madam,</p>
      <p>Customer has been booked a Quote with below details, please take a look and reach them with below details.</p>
      <ul>
      <li>Customer Name: ${customerName}</li>
      <li>Email: ${email}</li>
      <li>Phone: ${phone}</li>
      <li>Arrival Date: ${arrivalDate}</li>
      <li>Departure Date: ${departureDate}</li>
      <li>No of Adults: ${noOfAdults}</li>
      <li>Average Age: ${averageAge}</li>
      <li>No of Rooms: ${rooms}</li>
      </ul>
      <p>Thanks,<br />Convio.</p>
        `;
        return emailContent;
      }
    
      async function generateEmailTemplateForGroupBookingClient(customerName, email, phone, arrivalDate, departureDate, noOfAdults, averageAge, rooms) {
    
        let emailContent = `
        <h1>Group Booking Quotation Confirmation</h1>
        <p>Hi ${customerName.split(' ')[0]},</p>
        <p>Please find the the Group Booking Quotation Confirmation details below</p>
        <ul>
        <li>Customer Name: ${customerName}</li>
        <li>Email: ${email}</li>
        <li>Phone: ${phone}</li>
        <li>Arrival Date: ${arrivalDate}</li>
        <li>Departure Date: ${departureDate}</li>
        <li>No of Adults: ${noOfAdults}</li>
        <li>Average Age: ${averageAge}</li>
        <li>No of Rooms: ${rooms}</li>
        </ul>
        <p>Thanks,<br />Convio.</p>
          `;
          return emailContent;
        }
module.exports = {
  sendEmail,
  sendEmailToClient,
  generateEmailTemplateForBooking,
  generateEmailTemplateForBookingClient,
  generateEmailTemplateForGroupBookingClient,
  generateEmailTemplateForGroupBooking
};
