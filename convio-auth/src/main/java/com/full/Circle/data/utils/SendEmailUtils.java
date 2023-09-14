package com.full.Circle.data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.full.Circle.data.dto.CustomException;

@Component
public class SendEmailUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(SendEmailUtils.class);

	private String emailFrom = System.getenv("SES_EMAIL_FROM");

	private String accessKey = System.getenv("SES_ACCESS_KEY");

	private String secretKey = System.getenv("SES_SECRET_KEY");
	

//	private String emailFrom = "akash.g@hubino.com";
//
//	private String accessKey = "AKIASKA4JS3QJPYVAONN";
//
//	private String secretKey ="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";
	
	/**
	 * Send an Email
	 * 
	 * @param subject
	 * @param textBody
	 * @throws Exception 
	 */
	public void sendMail(String subject, String textBody, String emailTo) throws Exception{
		try {
			AmazonSimpleEmailService client = amazonSimpleEmailServiceConfig();

			SendEmailRequest req = new SendEmailRequest().withDestination(new Destination().withToAddresses(emailTo))
					.withMessage(new Message()
							.withBody(new Body().withText(new Content().withCharset("UTF-8").withData(textBody)))
							.withSubject(new Content().withCharset("UTF-8").withData(subject)))
					.withSource(emailFrom);
			client.sendEmail(req);
		} catch (Exception e) {
			logger.error("Error in sendEmail. Error = "+e);
			throw new CustomException(Constants.EMAIL_FAILURE_MSG);
		}
	}
	
	public AmazonSimpleEmailService amazonSimpleEmailServiceConfig() {
		return AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
	}
}
